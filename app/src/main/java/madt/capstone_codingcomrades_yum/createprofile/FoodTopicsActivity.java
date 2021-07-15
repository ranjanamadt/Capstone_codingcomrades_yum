
package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityFoodTopicsBinding;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FirebaseConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FoodTopicsActivity extends BaseActivity {
    private ActivityFoodTopicsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_topics);

        binding.btnConfirmFoodTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.spinner.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_eat_about_empty));
                } else if(binding.spinner2.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_talk_about_empty));
                } else {
                    StringBuilder resultNotEat = new StringBuilder("");
                    for(int i=0; i<binding.chipGroup.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                        if(chip.isChecked()){
                            resultNotEat.append(chip.getText()).append(",");
                        }
                    }

                    StringBuilder resultNotTalk = new StringBuilder("");
                    for(int i=0; i<binding.chipNotTalk.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipNotTalk.getChildAt(i);
                        if(chip.isChecked()){
                            resultNotTalk.append(chip.getText()).append(",");
                        }
                    }

                    String resultNotEatPref = resultNotEat.toString();
                    String resultNotTalkPref = resultNotTalk.toString();

                    if (resultNotEatPref != null && resultNotEatPref.length() > 0) {
                        resultNotEatPref = resultNotEatPref.substring(0, resultNotEatPref.length() - 1);
                    }
                    if (resultNotTalkPref != null && resultNotTalkPref.length() > 0) {
                        resultNotTalkPref = resultNotTalkPref.substring(0, resultNotTalkPref.length() - 1);
                    }

                    yLog("not eat list :","" + resultNotEatPref);
                    yLog("not talk list :","" + resultNotTalkPref);

                    Map<String, Object> notEatPreference = new HashMap<>();
                    notEatPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, "not_eat");
                    notEatPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultNotEatPref);
                    notEatPreference.put(FirebaseConstants.PREFERENCE.USER_UID, AboutMeActivity.user_uid);

                    Map<String, Object> notTalkPreference = new HashMap<>();
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, "not_talk");
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultNotTalkPref);
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.USER_UID, AboutMeActivity.user_uid);

                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notEatPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                        }
                    });

                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notTalkPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());

                            Intent i = new Intent(FoodTopicsActivity.this, FinishProfileActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_talk));
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();

    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.title_food_topics),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

}