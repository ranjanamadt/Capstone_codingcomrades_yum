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
    int checkEating = 0;
    int checkTaste = 0;
    List<String> resultEating = new ArrayList<>();
    List<String> resultTastes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tastes);


        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultEating.isEmpty()) {
                    ySnackbar(TasteActivity.this, getString(R.string.err_enjoy_eating_chip_empty));
                } else if (resultTastes.isEmpty()) {
                    ySnackbar(TasteActivity.this, getString(R.string.err_enjoy_taste_chip_empty));
                } else {

                    yLog("enjoy eating list :", "" + resultEating);
                    yLog("preference for taste list :", "" + resultTastes);
/*
                    if(resultEating == null || resultEating.size() == 0){
                        ySnackbar(TasteActivity.this, getString(R.string.err_enjoy_eating_chip_empty));
                        return;
                    }
                    if(resultTastes == null || resultTastes.size() == 0){
                        ySnackbar(TasteActivity.this, getString(R.string.err_enjoy_taste_chip_empty));
                        return;
                    }*/

                    Map<String, Object> eatingPref = new HashMap<>();
                    eatingPref.put(FSConstants.PREFERENCE_TYPE.ENJOY_EATING, resultEating);
                    eatingPref.put(FSConstants.PREFERENCE_TYPE.TASTE, resultTastes);

                    addEatingPrefToDB(eatingPref, eatingPref);

                }
            }
        });


    }


    private void getEatingPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.ENJOY_EATING).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                enjoyEatingList = (List<String>) documentSnapshot.get("data");
                setEnjoyEatingDropdown(enjoyEatingList);
                getTastesPreferences();
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
                    if (resultTastes != null && !resultTastes.isEmpty()) {
                        if (!resultTastes.contains(tasteList.get(position)))
                            addTastesChip(tasteList.get(position));
                    } else {
                        addTastesChip(tasteList.get(position));
                    }
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
                    if (resultEating != null && !resultEating.isEmpty()) {
                        if (!resultEating.contains(enjoyEatingList.get(position)))
                            addEatingChip(enjoyEatingList.get(position));
                    } else {
                        addEatingChip(enjoyEatingList.get(position));
                    }
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
                CommonUtils.hideProgress();
                AppSharedPreferences.getInstance().setBoolean(SharedConstants.TASTE_DONE, true);
                Intent i = new Intent(TasteActivity.this, InterestActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(TasteActivity.this, getString(R.string.error_saving_eating));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();

        binding.chipGroupEating.removeAllViews();
        binding.chipGroupTastes.removeAllViews();
        getCurrentUserPreferences();
    }

    private void getCurrentUserPreferences() {

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user id :", documentSnapshot.getId() + " ");

                if (documentSnapshot.exists()) {
                    List<String>   resultE = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING);
                    List<String>   resultT = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE);

                    if (resultE != null && resultE.size() > 0) {
                        resultEating.addAll(resultE);
                        addEnjoyEating(resultT);
                    }
                    if (resultT != null && resultT.size() > 0) {
                        resultTastes.addAll(resultT);
                        addTaste(resultT);
                    }
                }
                getEatingPreferences();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(TasteActivity.this, getString(R.string.error_saving_not_eat));
                getEatingPreferences();
            }
        });

    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
        for (String enjoyEat : enjoyEatingList) {

            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupEating, false);
            newChip.setText(enjoyEat);
            binding.chipGroupEating.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupEating.removeView(v);
                    resultEating.remove(((Chip) v).getText());
                }
            });
        }
    }

    private void addTaste(List<String> tasteList) {
        for (String taste : tasteList) {

            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastes, false);
            newChip.setText(taste);
            binding.chipGroupTastes.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupTastes.removeView(v);
                    resultTastes.remove(((Chip) v).getText());
                    yLog("remove taste", resultTastes.toString());
                }
            });
        }
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
        resultEating.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupEating, false);
        newChip.setText(topic);
        binding.chipGroupEating.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupEating.removeView(v);
                resultTastes.remove(((Chip) v).getText());
            }
        });

    }

    private void addTastesChip(String topic) {
        resultTastes.add(topic);
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastes, false);
        newChip.setText(topic);
        binding.chipGroupTastes.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupTastes.removeView(v);
                resultTastes.remove(((Chip) v).getText());
            }
        });

    }


}