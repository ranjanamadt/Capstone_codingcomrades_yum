package madt.capstone_codingcomrades_yum.matcheslisting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
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
import madt.capstone_codingcomrades_yum.chat.Message;
import madt.capstone_codingcomrades_yum.core.BaseFragment;
import madt.capstone_codingcomrades_yum.databinding.FragmentMatchesBinding;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class MatchesFragment extends BaseFragment {

    private List<User> matchesList = new ArrayList<>();
    private FragmentMatchesBinding binding;
    private MatchesAdapter mAdapter;
    private String currentUserName = "";
    private String currentUserId = "";
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
                //TODO: reject event
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                //TODO: accept event
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                //TODO: no more cards event
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

        // CommonUtils.showProgress(getActivity());
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                FirebaseCRUD.getInstance().
                        findMatches(FSConstants.Collections.USERS, (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE)).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                        Log.e("matches :", task.getResult()+ "//");
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            matchesList.add(new User(document));
                        }
                        // on below line we are creating a variable for our adapter class and passing array list to it.
                        mAdapter = new MatchesAdapter(matchesList, getContext());

                        // on below line we are setting adapter to our card stack.
                        binding.swipeDeck.setAdapter(mAdapter);

                        //  CommonUtils.hideProgress();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("matches :", "infailure");
                        //   CommonUtils.hideProgress();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // CommonUtils.hideProgress();

            }
        });

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
                if (shownUser.getProfileImage() != null) {
                    ((ImageView) v.findViewById(R.id.imageView)).setImageBitmap(shownUser.getProfileBitmapImage());
                }

                ((TextView) v.findViewById(R.id.mtdAge)).setText(String.valueOf(shownUser.getAge()));
                ((TextView) v.findViewById(R.id.mtcUserName)).setText(shownUser.getFullName());
                ((TextView) v.findViewById(R.id.mtcBio)).setText(shownUser.getAboutMe());
                v.findViewById(R.id.accept_btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.swipeDeck.swipeTopCardRight(1000);
                        Map<String, Object> chatList = new HashMap<>();
                        Message firstMessage = new Message(mLoginDetail.getFirstName() + " " + mLoginDetail.getLastName(),
                                mLoginDetail.getUuid(),
                                "67.7",
                                "Hello");
                        List<Message> messageList = new ArrayList<Message>();
                        messageList.add(firstMessage);
                        chatList.put(FSConstants.CHAT_List.MESSAGES, messageList);

                        FirebaseCRUD.getInstance().createSubCollection(FSConstants.Collections.USERS,

                                FSConstants.Collections.CHATROOM, FirebaseAuth.getInstance().getUid(),
                                FirebaseAuth.getInstance().getUid()+"@"+matchesList.get(position).getFirstName()+matchesList,
                                chatList
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                            }
                        });
                        FirebaseCRUD.getInstance().createSubCollection(FSConstants.Collections.USERS,

                                FSConstants.Collections.CHATROOM, matchesList.get(position).getUuid(),

                                matchesList.get(position).getUuid()+"@"+mLoginDetail.getFirstName() + " " + mLoginDetail.getLastName(),
                                chatList
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                            }
                        });

                      /*  binding.swipeDeck.swipeTopCardRight(1000);
                        Map<String, Object> chatList1 = new HashMap<>();
                        chatList.put(FSConstants.CHAT_List.USER_NAME, currentUserName);

                        FirebaseCRUD.getInstance().createSubCollection(FSConstants.Collections.USERS,
                                matchesList.get(position).getUuid(),
                                FSConstants.Collections.CHATROOM, chatList1
                        );

*/
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

    @Override
    public void onResume() {
        super.onResume();
    }
}