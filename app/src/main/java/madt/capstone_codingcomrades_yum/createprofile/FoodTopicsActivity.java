
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
import madt.capstone_codingcomrades_yum.databinding.ActivityFoodTopicsBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FoodTopicsActivity extends BaseActivity {
    private ActivityFoodTopicsBinding binding;
    /*final static String[] nofood = {"Acorn Squash","Apple","Arugula","Asparagus","Banana","Blackberries","Broccoli","Brussel Sprouts","Butternut Squash","Cabbage","Carrots","Cauliflower","Chicken","Collard Greens","Cucumber"
    ,"Garlic","Grapes","IceCream","Kale","Lemon","Lettuce","Mustard greens","Oatmeal","Onion","Orange","Papaya","Pear","Peas","Peppers","Pork","Strawberries","Vegan","Vegetarian","Zucchini","Yolk"};
    final static String[] notopics = {"Art", "Movies", "Sports", "Gym", "Politics"};*/
    static private List<String> notEatList;
    static private List<String> notTalkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_topics);

        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_EAT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                notEatList = (List<String>) documentSnapshot.get("data");
                setNotEatDropdown();

                getNotTalkListFromDB();

            }
        });

        binding.btnConfirmFoodTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.spnNoFood.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_eat_about_empty));
                } else if(binding.spnNoTopic.getSelectedItem().toString().isEmpty()){
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_talk_about_empty));
                } else {
                    List<String> resultNotEat = new ArrayList<>();
                    for(int i=0; i<binding.chipGroupNoFood.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipGroupNoFood.getChildAt(i);
                        if(chip.isChecked()){
                            resultNotEat.add(chip.getText().toString());
                        }
                    }

                    List<String> resultNotTalk = new ArrayList<>();
                    for(int i=0; i<binding.chipNotTalk.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipNotTalk.getChildAt(i);
                        if(chip.isChecked()){
                            resultNotTalk.add(chip.getText().toString());
                        }
                    }

                    yLog("not eat list :","" + resultNotEat);
                    yLog("not talk list :","" + resultNotTalk);

                    Map<String, Object> notEatPreference = new HashMap<>();
                    notEatPreference.put(FSConstants.PREFERENCE_TYPE.NOT_EAT, resultNotEat);

                    Map<String, Object> notTalkPreference = new HashMap<>();
                    notTalkPreference.put(FSConstants.PREFERENCE_TYPE.NOT_TALK, resultNotTalk);

                    CommonUtils.showProgress(FoodTopicsActivity.this);

                    FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), notEatPreference).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateNotTalk(notTalkPreference);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.hideProgress();
                            ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                        }
                    });


                }
            }
        });
    }

    private void updateNotTalk(Map<String, Object> notTalkPreference) {
        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), notTalkPreference).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CommonUtils.hideProgress();
                AppSharedPreferences.getInstance().setBoolean(SharedConstants.FOOD_TOPIC_DONE, true);
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

    private void getNotTalkListFromDB() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_TALK).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                notTalkList = (List<String>) documentSnapshot.get("data");
                setNotTalkDropdown();
                CommonUtils.hideProgress();


            }
        });

    }

    private void setNotTalkDropdown() {

        binding.spnNoTopic.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                notTalkList));

        binding.spnNoTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addNoTalkChip(notTalkList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setNotEatDropdown() {
        binding.spnNoFood.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, notEatList));
        binding.spnNoFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(++check > 1)
                addNoEatChip(notEatList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.chipGroupNoFood.removeAllViews();
        binding.chipGroupNoFood.removeAllViews();
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

    private void addNoEatChip(String topic) {
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
    private void addNoTalkChip(String topic) {
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