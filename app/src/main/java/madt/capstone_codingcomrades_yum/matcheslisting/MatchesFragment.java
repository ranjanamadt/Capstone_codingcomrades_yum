package madt.capstone_codingcomrades_yum.matcheslisting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.chat.ChatDetail;
import madt.capstone_codingcomrades_yum.chat.Message;
import madt.capstone_codingcomrades_yum.core.BaseFragment;
import madt.capstone_codingcomrades_yum.databinding.FragmentMatchesBinding;
import madt.capstone_codingcomrades_yum.fcm.SendPushHelper;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.ChatMessagesHelper;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class MatchesFragment extends BaseFragment {

    private List<User> matchesList = new ArrayList<>();
    private FragmentMatchesBinding binding;
    private MatchesAdapter mAdapter;

    protected LoginUserDetail mLoginDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_matches, container, false);

        mLoginDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);

        binding.swipeDeck.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                //CREATE MATCH IN CURRENT USER
                List<String> matchedList = mLoginDetail.getMatched_users();
                matchedList.add(matchesList.get(position).getUuid());

                //CREATE MATCH IN OTHER USER`
                List<String> matchedListOther = matchesList.get(position).getMatched_users();
                matchedListOther.add(mLoginDetail.getUuid());

                addMatchToUser(matchedList,matchedListOther,matchesList.get(position).getUuid());

                //CREATE MESSAGE
                Message firstMessage = new Message(
                        mLoginDetail.getFullName(),
                        mLoginDetail.getUuid(),
                        System.currentTimeMillis() + "",
                        "Hello",
                        mLoginDetail.getProfileImage().get(0));
                List<Message> messageList = new ArrayList<Message>();
                messageList.add(firstMessage);

                //CREATE CHATROOM FOR LOGGED USER
                Map<String, Object> currentChatList = new HashMap<>();
                currentChatList.put(FSConstants.CHAT_List.MESSAGES, messageList);
                currentChatList.put(FSConstants.CHAT_List.USER_DETAIL, new ChatDetail(
                        matchesList.get(position).getFirstName(),
                        matchesList.get(position).getLastName(),
                        System.currentTimeMillis() + "",
                        getString(R.string.initial_chat_message),
                        matchesList.get(position).getProfileImage().get(0),
                        mLoginDetail.getUuid(),
                        matchesList.get(position).getUuid(),
                        matchesList.get(position).getDeviceToken()

                ));

                // CHATROOM ADDED IN DB FOR CURRENT USER
                ChatMessagesHelper.createCurrentUserChatRoom(mLoginDetail.getUuid() +
                        matchesList.get(position).getUuid(), currentChatList);

                //CREATE CHATROOM FOR LIKED USER
                Map<String, Object> likedChatList = new HashMap<>();

                likedChatList.put(FSConstants.CHAT_List.MESSAGES, messageList);
                likedChatList.put(FSConstants.CHAT_List.USER_DETAIL, new ChatDetail(
                        mLoginDetail.getFirstName(),
                        mLoginDetail.getLastName(),
                        System.currentTimeMillis() + "",
                        getString(R.string.initial_chat_message),
                        mLoginDetail.getProfileImage().get(0),
                        matchesList.get(position).getUuid(),
                        mLoginDetail.getUuid(),
                       AppSharedPreferences.getInstance().getString(SharedConstants.DEVICE_TOKEN)
                ));

                // Send Push notification on receiver device
                SendPushHelper.sendPush(getActivity(), matchesList.get(position).getDeviceToken(), getString(R.string.new_like)
                        , mLoginDetail.getFullName() + " liked your profile!");


                // CHATROOM ADDED IN DB FOR OTHER USER
                ChatMessagesHelper.createLikedUserChatRoom(
                        matchesList.get(position).getUuid(),matchesList.get(position).getUuid() + mLoginDetail.getUuid(),  likedChatList);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
            }

            @Override
            public void cardActionDown() {
            }

            @Override
            public void cardActionUp() {
            }

        });
        getMatchesList();

     /*   if (user != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            },30000);

            mLoginDetail = user;
        } else {

        }*/
        return binding.getRoot();
    }


    private void getMatchesList() {

        CommonUtils.showProgress(getActivity());
        List<String> reported = mLoginDetail.getReport_list();
        List<String> matched = mLoginDetail.getMatched_users();
        List<String> myPreferences = mLoginDetail.getPreferences();
        int maxDistance = mLoginDetail.getMaxDistance();
        int mLoginDetailMaxAge = mLoginDetail.getMaxAge();
        int mLoginDetailMinAge = mLoginDetail.getMinAge();
        String mLoginDetailLookingFor = mLoginDetail.getLookingFor();
        Location locationLoginUser = mLoginDetail.getLocationObject();

        FirebaseCRUD.getInstance().
                findMatches().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                        Log.e("matches :", task.getResult()+ "//");
                matchesList.clear();

                Log.d("MATCHES ITERATION :", "************************************************************************");
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    User matchUser = new User(document);
                    Log.d("Match Name:", matchUser.getFullName());

                    Boolean isActive = matchUser.getActiveStatus() != null ? true : false;
                    List<String> matchPreferences = matchUser.getPreferences();

                    Float distances = locationLoginUser.distanceTo(matchUser.getLocationObject()) / (1609);


                    if(!isActive){
                        Log.e("Inactive :", document.getId());
                    }
                    else if (!mLoginDetailLookingFor.equalsIgnoreCase("Any") && !mLoginDetailLookingFor.equalsIgnoreCase(matchUser.getGender()) ) {
                        Log.e("Different gender :", document.getId());
                    }
                    else if(distances > (float) maxDistance){
                        Log.e("Distances : " + distances.toString(), document.getId());
                    }
                    else if (matchUser.getAge() < mLoginDetailMinAge ) {
                        Log.e("Younger than " + mLoginDetailMinAge + " :", document.getId());
                    }
                    else if (matchUser.getAge() > mLoginDetailMaxAge ) {
                        Log.e("Older than " + mLoginDetailMaxAge + " :", document.getId());
                    }

                    else if (reported != null && reported.contains(document.getId())) {
                        Log.e("Reported :", document.getId());
                    }
                    else if (matched != null && matched.contains(document.getId())) {
                        Log.e("Already Matched :", document.getId());
                    }
                    else if (matchPreferences != null && !validatePreferences(myPreferences, matchPreferences)) {
                        Log.e("Not common preferences:", document.getId());
                    } else {
                        matchesList.add(matchUser);

                    }

                }
                // on below line we are creating a variable for our adapter class and passing array list to it.
                mAdapter = new MatchesAdapter(matchesList, getContext());

                // on below line we are setting adapter to our card stack.
                binding.swipeDeck.setAdapter(mAdapter);

                CommonUtils.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e("matches :", "infailure");
                CommonUtils.hideProgress();
            }
        });


    }

    public Boolean validatePreferences(List<String> myPreferences, List<String> matchPreferences){


        for (String matchPref: matchPreferences) {
            if(myPreferences.contains(matchPref)){
                return true;
            }
        }
        return false;
    }

    class MatchesAdapter extends BaseAdapter {

        // on below line we have created variables
        // for our array list and context.
        private ArrayList<User> matchesList;
        private Context context;

        // on below line we have created constructor for our variables.
        public MatchesAdapter(List<User> matchesList, Context context) {
            this.matchesList = (ArrayList<User>) matchesList;
            this.context = context;
        }

        @Override
        public int getCount() {
            // in get count method we are returning the size of our array list.
            return matchesList.size();
        }

        @Override
        public Object getItem(int position) {
            // in get item method we are returning the item from our array list.
            return matchesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // in get item id we are returning the position.
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // in get view method we are inflating our layout on below line.
            View v = convertView;
            if (v == null) {
                // on below line we are inflating our layout.
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_stack_card, parent, false);
            }
            // on below line we are initializing our variables and setting data to our variables.
            if (matchesList.size() > 0 && position < matchesList.size()) {
                User shownUser = matchesList.get(position);
                if (shownUser.getProfileImage() != null && !shownUser.getProfileImage().isEmpty()) {
                    ((ImageView) v.findViewById(R.id.imageView)).setImageBitmap(shownUser.getProfileBitmapImage());
                }

                ((TextView) v.findViewById(R.id.mtdAge)).setText(String.valueOf(shownUser.getAge()));
                ((TextView) v.findViewById(R.id.mtcUserName)).setText(shownUser.getFullName());
                ((TextView) v.findViewById(R.id.mtcBio)).setText(shownUser.getAboutMe());
                v.findViewById(R.id.accept_btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.swipeDeck.swipeTopCardRight(1000);
                    }
                });
                v.findViewById(R.id.reject_btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.swipeDeck.swipeTopCardLeft(1000);
                    }
                });
                v.findViewById(R.id.info_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), MatchDetail.class);
                        i.putExtra(FSConstants.MATCHES_DETAIL.OTHER_USER_ID, shownUser.getUuid());
                        launcher.launch(i);
                    }
                });
            }


            return v;
        }


    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String action = data.getStringExtra(FSConstants.MATCHES_DETAIL.ACTION);
                    if (action.equals("Accept")) {
                        binding.swipeDeck.swipeTopCardRight(4000);
                    } else if (action.equals("Reject")) {
                        binding.swipeDeck.swipeTopCardLeft(4000);
                    }
                }
            });


    private void addMatchToUser(List<String> matchedList, List<String> othersMatchedList, String matchedUUID) {
        Map<String, Object> mapMatchedList = new HashMap<>();
        mapMatchedList.put(FSConstants.USER.MATCHED_USERS, matchedList);

        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, mLoginDetail.getUuid(), mapMatchedList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mLoginDetail.setMatched_users(matchedList);
                        AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(mLoginDetail).toString());

                    }
                });

        Map<String, Object> mapOtherMatchedList = new HashMap<>();

        mapOtherMatchedList.put(FSConstants.USER.MATCHED_USERS, othersMatchedList);
        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, matchedUUID, mapOtherMatchedList)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}