package madt.capstone_codingcomrades_yum.matcheslisting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.databinding.FragmentMatchesBinding;
import madt.capstone_codingcomrades_yum.external.ExternalProfileActivity;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class MatchesFragment extends Fragment {

    private List<User> matchesList = new ArrayList<>();
    private FragmentMatchesBinding binding;
    MatchesAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_matches, container, false);

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
                            matchesList.add(new User(
                                    document.getId(),
                                    document.get("firstName").toString(),
                                    document.get("lastName").toString(),
                                    document.get("dob").toString(),
                                    document.get("gender").toString(),
                                    document.get("sePref").toString(),
                                    document.get("bio") != null ? document.get("bio").toString() : "",
                                    Arrays.asList(document.get("interest")).toArray() ,
                                    Arrays.asList(document.get("not_eat")).toArray() ,
                                    Arrays.asList(document.get("not_talk")).toArray() ,
                                    Arrays.asList(document.get("taste")).toArray() ,
                                    Arrays.asList(document.get("enjoy_eating")).toArray()
                                    )
                            );

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
            return 10;
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
        //      ((ImageView) v.findViewById(R.id.imageView)).setImageResource(matchesList.get(position).getImgId());*/
                ((TextView) v.findViewById(R.id.mtcUserName)).setText(matchesList.get(position).getFullName());
                ((TextView) v.findViewById(R.id.mtcBio)).setText(matchesList.get(position).getBio());
                v.findViewById(R.id.acept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.swipeDeck.swipeTopCardRight(1000);
                    }
                });
                v.findViewById(R.id.reject).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.swipeDeck.swipeTopCardLeft(1000);
                    }
                });
                v.findViewById(R.id.info_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity() , ExternalProfileActivity.class);
                        i.putExtra("otherUserID", matchesList.get(position).getUuid());
                        startActivity(i);
                    }
                });


            }


            return v;
        }
    }
}