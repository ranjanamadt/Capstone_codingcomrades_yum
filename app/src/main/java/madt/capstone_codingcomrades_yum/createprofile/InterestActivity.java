package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityInterestsBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class InterestActivity extends BaseActivity {
    private ActivityInterestsBinding binding;
    private List<String> interestList;
    List<String> userNoTalkList = new ArrayList<>();
    List<String> resultInt = new ArrayList<>();
    Boolean checkInterest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interests);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.chipInterest.removeAllViews();

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userNoTalkList = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(InterestActivity.this, getString(R.string.err_fetching_data));
            }
        });

        checkInterest=false;
        if( resultInt.isEmpty()) {
            getSavedInterest();
        } else{
            addInterest(resultInt);
        }

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (resultInt.isEmpty()) {
                    ySnackbar(InterestActivity.this, getString(R.string.err_interest_chip_empty));
                } else {

                    Map<String, Object> interest = new HashMap<>();
                    interest.put(FSConstants.PREFERENCE_TYPE.INTEREST, resultInt);

                    CommonUtils.showProgress(InterestActivity.this);

                    FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), interest).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            CommonUtils.hideProgress();
                            AppSharedPreferences.getInstance().setBoolean(SharedConstants.INTEREST_DONE, true);
                            Intent i = new Intent(InterestActivity.this, FoodTopicsActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.hideProgress();
                            ySnackbar(InterestActivity.this, getString(R.string.error_saving_interest));
                        }
                    });

                }
            }
        });
    }

    private void getSavedInterest() {
        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user id :", documentSnapshot.getId() + " ");

                if (documentSnapshot.exists()) {
                    List<String>  listI = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST);

                    if (listI != null && listI.size() > 0) {
                        resultInt.addAll(listI);
                        addInterest(listI);
                    }
                }
                getAllInterests();
                CommonUtils.hideProgress();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                getAllInterests();
            }
        });


    }

    private void addInterest(List<String> addInterestList) {
        for (String interest : addInterestList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipInterest, false);
            newChip.setText(interest);
            binding.chipInterest.addView(newChip);
            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipInterest.removeView(v);
                    resultInt.remove(((Chip) v).getText());
                    Log.e("after remove :",resultInt.toString()+"//");
                }
            });
        }
    }

    private void getAllInterests() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.INTEREST).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                interestList = (List<String>) documentSnapshot.get("data");
                setInterestDropdown();
                CommonUtils.hideProgress();
            }
        });

    }

    private void setInterestDropdown() {
        if(userNoTalkList != null && !userNoTalkList.isEmpty()){
            interestList.removeAll(userNoTalkList);
            yLog("interest drop down list: ", interestList.toString());
            yLog("userNoTalkList: ", userNoTalkList.toString());
        }
        binding.interestTopics.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, interestList));
        binding.interestTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("results",resultInt.toString());
                if(checkInterest) {
                    if (resultInt != null && !resultInt.isEmpty()) {
                        if (!resultInt.contains(interestList.get(position)))
                            addInterestChip(interestList.get(position));
                    } else {
                        addInterestChip(interestList.get(position));
                    }
                }else{
                    checkInterest=true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.title_interests),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    private void addInterestChip(String topic) {
        resultInt.add(topic);
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipInterest, false);
        newChip.setText(topic);
        binding.chipInterest.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipInterest.removeView(v);
                resultInt.remove(((Chip) v).getText());
            }
        });
    }
}
