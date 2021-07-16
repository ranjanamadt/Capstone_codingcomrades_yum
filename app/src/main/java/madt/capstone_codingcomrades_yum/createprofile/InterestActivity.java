package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityInterestsBinding;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class InterestActivity extends BaseActivity {
    private ActivityInterestsBinding binding;
     static private List<String>  interestList;

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

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.INTEREST).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                interestList = (List<String>) documentSnapshot.get("data");
                setInterestDropdown();


            }
        });


        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (binding.interestTopics.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(InterestActivity.this, getString(R.string.err_talk_about_empty));
                } else {
                    List<String> resultInt = new ArrayList<>();
                    for (int i = 0; i < binding.chipInterest.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipInterest.getChildAt(i);
                        if (chip.isChecked()) {
                            resultInt.add(chip.getText().toString());
                        }
                    }

                    yLog("interest list :", "" + resultInt);

                    Map<String, Object> interest = new HashMap<>();
                    interest.put(FSConstants.PREFERENCE_TYPE.INTEREST, resultInt);

                    CommonUtils.showProgress(InterestActivity.this);

                    FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), interest).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            CommonUtils.hideProgress();
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

    private void setInterestDropdown() {

        binding.interestTopics.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, interestList));
        binding.interestTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addInterestChip(interestList.get(position));
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
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipInterest, false);
        newChip.setText(topic);
        binding.chipInterest.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipInterest.removeView(v);
            }
        });
    }
}
