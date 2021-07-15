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

import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityInterestsBinding;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FirebaseConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class InterestActivity extends BaseActivity {
    private ActivityInterestsBinding binding;
    final static String[] topics = {"Art", "Movies", "Sports", "Gym", "Politics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interests);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.interestTopics.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topics));
        binding.interestTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addInterestChip(topics[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (binding.interestTopics.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(InterestActivity.this, getString(R.string.err_talk_about_empty));
                } else {
                    StringBuilder resultInt = new StringBuilder("");
                    for (int i = 0; i < binding.chipInterest.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipInterest.getChildAt(i);
                        if (chip.isChecked()) {
                            resultInt.append(chip.getText()).append(",");
                        }
                    }

                    String resultInterest = resultInt.toString();
                    if (resultInterest != null && resultInterest.length() > 0) {
                        resultInterest = resultInterest.substring(0, resultInterest.length() - 1);
                    }

                    yLog("interest list :", "" + resultInterest);

                    Map<String, Object> interest = new HashMap<>();
                    interest.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, FirebaseConstants.PREFERENCE_TYPE.INTEREST);
                    interest.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultInterest);
                    interest.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());
                    CommonUtils.showProgress(InterestActivity.this);
                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, interest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id", documentReference.getId());
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
    }
}
