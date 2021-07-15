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
import madt.capstone_codingcomrades_yum.databinding.ActivityTastesBinding;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FirebaseConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class TasteActivity extends BaseActivity  {
    private ActivityTastesBinding binding;

    final static String[] foods = {"Sushi","Ramen", "Halal", "Dessert", "Coffee", "Italian", "Ceviche","Acorn Squash","Apple","Arugula","Asparagus","Banana","Blackberries","Broccoli","Brussel Sprouts","Butternut Squash","Cabbage","Carrots","Cauliflower","Chicken","Collard Greens","Cucumber"
            ,"Garlic","Grapes","IceCream","Kale","Lemon","Lettuce","Mustard greens","Oatmeal","Onion","Orange","Papaya","Pear","Peas","Peppers","Pork","Strawberries","Vegan","Vegetarian","Zucchini","Yolk"};
    final static String[] preferences = {"Salty", "Sweet", "Sour","Spicy","Healthy","Fried","Boiled","Stir fry","Soups"};

    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tastes);
      /*  FirebaseCRUD.getInstance().getDocument("users/2ftrg0GzUpVGR3Nrqzx0/prefer", "m4Eh2qw495xepc3SpZZX").addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", documentSnapshot.getData().toString()+"//");

            }
        });*/



        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.spnEatingPreferences.getSelectedItem().toString().isEmpty()){
                    ySnackbar(TasteActivity.this,getString(R.string.err_enjoy_eating_name_empty));

                }else if(binding.spnTastesPreferences.getSelectedItem().toString().isEmpty()){
                    ySnackbar(TasteActivity.this,getString(R.string.err_preferences_taste_name_empty));
                }else{

                    StringBuilder resultEating = new StringBuilder("");
                    for(int i=0; i<binding.chipGroupEating.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipGroupEating.getChildAt(i);
                        if(chip.isChecked()){
                            resultEating.append(chip.getText()).append(",");
                        }
                    }

                    StringBuilder resultTastes = new StringBuilder("");
                    for(int i=0; i<binding.chipGroupTastes.getChildCount(); i++){
                        Chip chip = (Chip) binding.chipGroupTastes.getChildAt(i);
                        if(chip.isChecked()){
                            resultTastes.append(chip.getText()).append(",");
                        }
                    }

                    String resultEatingPref = resultEating.toString();
                    String resultTastesPref = resultTastes.toString();

                    if (resultEatingPref != null && resultEatingPref.length() > 0){
                        resultEatingPref = resultEatingPref.substring(0, resultEatingPref.length() - 1);
                    }
                    if (resultTastesPref != null && resultTastesPref.length() > 0) {
                        resultTastesPref = resultTastesPref.substring(0, resultTastesPref.length() - 1);
                    }

                    yLog("enjoy eating list :","" + resultEatingPref);
                    yLog("preference for taste list :","" + resultTastesPref);

                    Map<String, Object> eatingPref = new HashMap<>();
                    eatingPref.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, FirebaseConstants.PREFERENCE_TYPE.ENJOY_EATING);
                    eatingPref.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultEatingPref);
                    eatingPref.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());

                    Map<String, Object> tastePref = new HashMap<>();
                    tastePref.put(FirebaseConstants.PREFERENCE.PREFERENCE_TYPE, FirebaseConstants.PREFERENCE_TYPE.TASTE);
                    tastePref.put(FirebaseConstants.PREFERENCE.PREFERENCE_NAME, resultTastesPref);
                    tastePref.put(FirebaseConstants.PREFERENCE.USER_UID, FirebaseAuth.getInstance().getUid());

                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, eatingPref).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ySnackbar(TasteActivity.this, getString(R.string.error_saving_eating));
                        }
                    });

                    FirebaseCRUD.getInstance().create(FirebaseConstants.Collections.PREFERENCES, tastePref).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            yLog("preference_id",documentReference.getId());

                            Intent i = new Intent(TasteActivity.this, InterestActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ySnackbar(TasteActivity.this, getString(R.string.error_saving_tastes));
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

        binding.spnEatingPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, foods));
        binding.spnTastesPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                preferences));


        binding.spnEatingPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(++check > 1)
                addEatingChip(foods[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spnTastesPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addTastesChip(preferences[position]);
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
                getString(R.string.title_tastes),
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
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupEating, false);
        newChip.setText(topic);
        binding.chipGroupEating.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupEating.removeView(v);
            }
        });

    }
    private void addTastesChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastes, false);
        newChip.setText(topic);
        binding.chipGroupTastes.addView(newChip);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupTastes.removeView(v);
            }
        });

    }


}