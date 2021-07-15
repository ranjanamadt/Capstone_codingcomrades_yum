
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
    final static String[] nofood = {"Acorn Squash","Apple","Arugula","Asparagus","Banana","Blackberries","Broccoli","Brussel Sprouts","Butternut Squash","Cabbage","Carrots","Cauliflower","Chicken","Collard Greens","Cucumber"
    ,"Garlic","Grapes","IceCream","Kale","Lemon","Lettuce","Mustard greens","Oatmeal","Onion","Orange","Papaya","Pear","Peas","Peppers","Pork","Strawberries","Vegan","Vegetarian","Zucchini","Yolk"};
    final static String[] notopics = {"Art", "Movies", "Sports", "Gym", "Politics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_topics);

        binding.btnConfirmFoodTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.spnNoFood.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_eat_about_empty));
                } else if(binding.spnNoTopic.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_talk_about_empty));
                } else {
                    StringBuilder resultNotEat = new StringBuilder("");
                    for(int i=0; i<binding.chipGroupNoFood.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipGroupNoFood.getChildAt(i);
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
                    notEatPreference.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());

                    Map<String, Object> notTalkPreference = new HashMap<>();
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, "not_talk");
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultNotTalkPref);
                    notTalkPreference.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());

                    CommonUtils.showProgress(FoodTopicsActivity.this);
                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notEatPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.hideProgress();
                            ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                        }
                    });

                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, notTalkPreference).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());
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
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.chipGroupNoFood.removeAllViews();
        binding.chipGroupNoFood.removeAllViews();
        binding.spnNoFood.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nofood));
        binding.spnNoTopic.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                notopics));


        binding.spnNoFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(++check > 1)
                addEatingChip(nofood[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spnNoTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addTastesChip(notopics[position]);
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

    private void addEatingChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNoFood, false);
        newChip.setText(topic);
        binding.chipGroupNoFood.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNoFood.removeView(v);
            }
        });

    }
    private void addTastesChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipNotTalk, false);
        newChip.setText(topic);
        binding.chipNotTalk.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipNotTalk.removeView(v);
            }
        });

    }

}