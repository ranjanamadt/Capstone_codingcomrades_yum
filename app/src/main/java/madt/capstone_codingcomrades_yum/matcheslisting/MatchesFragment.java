package madt.capstone_codingcomrades_yum.matcheslisting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class MatchesFragment extends Fragment {

    private List<User> matchesList = new ArrayList<>();
    private FragmentMatchesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_matches, container, false);



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
                            matchesList.add(new User(document.get("firstName").toString(),
                                    document.get("lastName").toString(),
                                    document.get("dob").toString(),
                                    document.get("gender").toString(),
                                    document.get("sePref").toString(),
                                    Arrays.asList(document.get("interest")).toArray() ,
                                    Arrays.asList(document.get("not_eat")).toArray() ,
                                    Arrays.asList(document.get("not_talk")).toArray() ,
                                    Arrays.asList(document.get("taste")).toArray() ,
                                    Arrays.asList(document.get("enjoy_eating")).toArray()
                                    )
                            );

                        }
                        // on below line we are creating a variable for our adapter class and passing array list to it.
                        final MatchesAdapter adapter = new MatchesAdapter(matchesList, getContext());

                        // on below line we are setting adapter to our card stack.
                        binding.swipeDeck.setAdapter(adapter);
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
}