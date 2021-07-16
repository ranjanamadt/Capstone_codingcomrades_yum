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
import madt.capstone_codingcomrades_yum.databinding.ActivityTastesBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class TasteActivity extends BaseActivity {
    private ActivityTastesBinding binding;

    /*    static String[] topics = {"Sushi", "Ramen", "Halal", "Dessert", "Coffee", "Italian", "Ceviche"};
        final static String[] preferences = {"Salty", "Sweet", "Sour"};*/
    private List<String> enjoyEatingList;
    private List<String> tasteList;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tastes);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.ENJOY_EATING).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                enjoyEatingList = (List<String>) documentSnapshot.get("data");
                setEnjoyEatingDropdown(enjoyEatingList);
                getTastesPreferences();

            }
        });


        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.spnEatingPreferences.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(TasteActivity.this, getString(R.string.err_enjoy_eating_name_empty));

                } else if (binding.spnTastesPreferences.getSelectedItem().toString().isEmpty()) {
                    ySnackbar(TasteActivity.this, getString(R.string.err_preferences_taste_name_empty));
                } else {

                    List<String> resultEating = new ArrayList<>();
                    for (int i = 0; i < binding.chipGroupEating.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupEating.getChildAt(i);
                        //  if (chip.isChecked()) {
                        resultEating.add(chip.getText().toString());
                        // }
                    }

                    List<String>  resultTastes = new ArrayList<>();
                    for (int i = 0; i < binding.chipGroupTastes.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupTastes.getChildAt(i);
                       // if (chip.isChecked()) {
                            resultTastes.add(chip.getText().toString());
                       // }
                    }

                    yLog("enjoy eating list :", "" + resultEating);
                    yLog("preference for taste list :", "" + resultTastes);

                    Map<String, Object> eatingPref = new HashMap<>();
                    eatingPref.put(FSConstants.PREFERENCE_TYPE.ENJOY_EATING, resultEating);


                    Map<String, Object> tastePref = new HashMap<>();
                    tastePref.put(FSConstants.PREFERENCE_TYPE.TASTE, resultTastes);

                    addEatingPrefToDB(eatingPref, tastePref);


                }

            }
        });


    }

    private void getTastesPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.TASTE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tasteList = (List<String>) documentSnapshot.get("data");
                setTastesDropdown(tasteList);
            }
        });
    }

    private void setTastesDropdown(List<String> tasteList) {
        binding.spnTastesPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                tasteList));
        binding.spnTastesPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addTastesChip(tasteList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setEnjoyEatingDropdown(List<String> topicsList) {
        binding.spnEatingPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topicsList));


        binding.spnEatingPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                addEatingChip(topicsList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void addEatingPrefToDB(Map<String, Object> eatingPref, Map<String, Object> tastePref) {
        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), eatingPref).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //  yLog("preference_id: ", documentReference.getId());
                addTastePreferencesToDB(tastePref);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(TasteActivity.this, getString(R.string.error_saving_eating));
            }
        });

      /*


        FirebaseCRUD.getInstance().create(FSConstants.Collections.PREFERENCES, eatingPref).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                yLog("preference_id: ", documentReference.getId());
                addTastePreferencesToDB(tastePref);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(TasteActivity.this, getString(R.string.error_saving_eating));
            }
        });*/
    }

    private void addTastePreferencesToDB(Map<String, Object> tastePref) {
        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), tastePref).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CommonUtils.hideProgress();
                AppSharedPreferences.getInstance().setBoolean(SharedConstants.TASTE_DONE, true);
                Intent i = new Intent(TasteActivity.this, InterestActivity.class);
                startActivity(i);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(TasteActivity.this, getString(R.string.error_saving_tastes));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.chipGroupEating.removeAllViews();
        binding.chipGroupTastes.removeAllViews();

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