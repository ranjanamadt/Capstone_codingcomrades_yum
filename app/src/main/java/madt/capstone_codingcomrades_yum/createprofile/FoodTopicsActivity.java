
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
import madt.capstone_codingcomrades_yum.databinding.ActivityFoodTopicsBinding;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FirebaseConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FoodTopicsActivity extends BaseActivity {
    private ActivityFoodTopicsBinding binding;
    final static String[] notEat = {"Sushi", "Ramen", "Halal", "Dessert", "Coffee", "Italian", "Ceviche"};
    final static String[] notTalk = {"Salty", "Sweet", "Sour"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_topics);

        binding.btnConfirmFoodTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.spnNotEat.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_eat_about_empty));
                } else if (binding.spnNotTalk.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_talk_about_empty));
                } else {
                    StringBuilder resultNotEat = new StringBuilder("");
                    for (int i = 0; i < binding.chipGroupNotEat.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupNotEat.getChildAt(i);
                        if (chip.isChecked()) {
                            resultNotEat.append(chip.getText()).append(",");
                        }
                    }

                    StringBuilder resultNotTalk = new StringBuilder("");
                    for (int i = 0; i < binding.chipGroupNotTalk.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupNotTalk.getChildAt(i);
                        if (chip.isChecked()) {
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

                    yLog("not eat list :", "" + resultNotEatPref);
                    yLog("not talk list :", "" + resultNotTalkPref);

                    Map<String, Object> notEatPreference = new HashMap<>();
                    notEatPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, FirebaseConstants.PREFERENCE_TYPE.NOT_EAT);
                    notEatPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultNotEatPref);
                    notEatPreference.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());

                    Map<String, Object> notTalkPreference = new HashMap<>();
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, FirebaseConstants.PREFERENCE_TYPE.NOT_TALK);
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultNotTalkPref);
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());


                    addNotEatToDB(notEatPreference, notTalkPreference);


                }
            }
        });
    }

    private void addNotEatToDB(Map<String, Object> notEatPreference, Map<String, Object> notTalkPreference) {
        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notEatPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                yLog("preference_id", documentReference.getId());
                setAddNotTalkToDB(notTalkPreference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                CommonUtils.hideProgress();
            }
        });
    }

    private void setAddNotTalkToDB(Map<String, Object> notTalkPreference) {
        FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notTalkPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                yLog("preference_id", documentReference.getId());
                CommonUtils.hideProgress();
                Intent i = new Intent(FoodTopicsActivity.this, FinishProfileActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_talk));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.chipGroupNotEat.removeAllViews();
        binding.chipGroupNotTalk.removeAllViews();

        binding.spnNotEat.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, notEat));
        binding.spnNotTalk.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                notTalk));

        binding.spnNotEat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(++check > 1)
                addNotEatChip(notEat[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spnNotTalk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addNotTalkChip(notTalk[position]);
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

    private void addNotEatChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNotEat, false);
        newChip.setText(topic);
        binding.chipGroupNotEat.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNotEat.removeView(v);
            }
        });

    }

    private void addNotTalkChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupNotTalk, false);
        newChip.setText(topic);
        binding.chipGroupNotTalk.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNotTalk.removeView(v);
            }
        });

    }
}