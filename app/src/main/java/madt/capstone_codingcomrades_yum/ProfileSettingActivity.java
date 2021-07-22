package madt.capstone_codingcomrades_yum;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.createprofile.FinishProfileActivity;
import madt.capstone_codingcomrades_yum.createprofile.FoodTopicsActivity;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;

public class ProfileSettingActivity extends BaseActivity {

    ChipGroup chipGroupEatingPref, chipGroupTastePref, chipGroupTalkPref, chipGroupNoEatPref, chipGroupNoTalkPref;
    Spinner spnEatingPref, spnTastePref, spnTalkPref, spnNoEatPref, spnNoTalkPref;
    private List<String> enjoyEatingList, tasteList, interestList, notEatList, notTalkList;
    List<String> resultEating = new ArrayList<>();
    List<String> resultTastes = new ArrayList<>();
    List<String> resultInterest = new ArrayList<>();
    List<String> resultNotEat = new ArrayList<>();
    List<String> resultNotTalk = new ArrayList<>();
    Boolean checkEating = false, checkTaste = false, checkInterest = false, checkNoEat = false, checkNoTalk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        chipGroupEatingPref = findViewById(R.id.chipGroupEatingPref);
        chipGroupTastePref = findViewById(R.id.chipGroupTastePref);
        chipGroupTalkPref = findViewById(R.id.chipGroupTalkPref);
        chipGroupNoEatPref = findViewById(R.id.chipGroupNoEatPref);
        chipGroupNoTalkPref = findViewById(R.id.chipGroupNoTalkPref);
        spnEatingPref = findViewById(R.id.spnEatingPref);
        spnTastePref = findViewById(R.id.spnTastePref);
        spnTalkPref = findViewById(R.id.spnTalkPref);
        spnNoEatPref = findViewById(R.id.spnNoEatPref);
        spnNoTalkPref = findViewById(R.id.spnNoTalkPref);

        getEatingPreferences();
        getTastesPreferences();
        getNotEatPreferences();
        getNotTalkPreferences();
        getInterestsPreferences();

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addTalkAbout((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, getString(R.string.error_saving_not_eat));
            }
        });

    }

    private void getEatingPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.ENJOY_EATING).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                enjoyEatingList = (List<String>) documentSnapshot.get("data");
                //setEnjoyEatingDropdown(enjoyEatingList);

                spnEatingPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, enjoyEatingList));

                spnEatingPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(checkEating){
                            if (resultEating != null && !resultEating.isEmpty()) {
                                if (!resultEating.contains(enjoyEatingList.get(position)))
                                    addEatingChip(enjoyEatingList.get(position));
                            } else {
                                addEatingChip(enjoyEatingList.get(position));
                            }
                        } else{
                            checkEating = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void getTastesPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.TASTE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tasteList = (List<String>) documentSnapshot.get("data");
                //setEnjoyEatingDropdown(enjoyEatingList);

                spnTastePref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, tasteList));

                spnTastePref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(checkTaste){
                            if (resultTastes != null && !resultTastes.isEmpty()) {
                                if (!resultTastes.contains(tasteList.get(position)))
                                    addTastesChip(tasteList.get(position));
                            } else {
                                addTastesChip(tasteList.get(position));
                            }
                        } else{
                            checkTaste = true;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void getNotEatPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_EAT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                notEatList = (List<String>) documentSnapshot.get("data");

                spnNoEatPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, notEatList));

                spnNoEatPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(checkNoEat){
                            if (resultNotEat != null && !resultNotEat.isEmpty()) {
                                if (!resultNotEat.contains(notEatList.get(position)))
                                    addNoEatChip(notEatList.get(position));
                            } else {
                                addNoEatChip(notEatList.get(position));
                            }
                        } else{
                            checkNoEat = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, e.getLocalizedMessage());
            }
        });
    }

    private void getNotTalkPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_TALK).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                notTalkList = (List<String>) documentSnapshot.get("data");

                spnNoTalkPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, notTalkList));

                spnNoTalkPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(checkNoTalk){
                            if (resultNotTalk != null && !resultNotTalk.isEmpty()) {
                                if (!resultNotTalk.contains(notTalkList.get(position)))
                                    addNoTalkChip(notTalkList.get(position));
                            } else {
                                addNoTalkChip(notTalkList.get(position));
                            }
                        } else{
                            checkNoTalk = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, e.getLocalizedMessage());
            }
        });
    }

    private void getInterestsPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.INTEREST).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                interestList = (List<String>) documentSnapshot.get("data");

                spnTalkPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, interestList));

                spnTalkPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(checkInterest){
                            if (resultInterest != null && !resultInterest.isEmpty()) {
                                if (!resultInterest.contains(interestList.get(position)))
                                    addInterestChip(interestList.get(position));
                            } else {
                                addInterestChip(interestList.get(position));
                            }
                        } else{
                            checkInterest = false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
        resultEating.addAll(enjoyEatingList);
        for (String enjoyEat : enjoyEatingList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, chipGroupEatingPref, false);
            newChip.setText(enjoyEat);
            chipGroupEatingPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroupEatingPref.removeView(v);
                }
            });
        }
    }

    private void addTaste(List<String> tasteList) {
        resultTastes.addAll(tasteList);
        for (String taste : tasteList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupTastePref, false);
            newChip.setText(taste);
            chipGroupTastePref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroupTastePref.removeView(v);
                }
            });
        }
    }

    private void addTalkAbout(List<String> TalkList) {
        resultInterest.addAll(TalkList);
        for (String talk : TalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupTalkPref, false);
            newChip.setText(talk);
            chipGroupTalkPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroupTalkPref.removeView(v);
                }
            });
        }
    }

    private void addNotEat(List<String> notEatList) {
        resultNotEat.addAll(notEatList);
        for (String notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, chipGroupNoEatPref, false);
            newChip.setText(notEat);
            chipGroupNoEatPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroupNoEatPref.removeView(v);
                }
            });
        }

        // getNotTalk(FirebaseAuth.getInstance().getUid());
    }

    private void addNotTalk(List<String> notTalkList) {
        resultNotTalk.addAll(notTalkList);
        for (String notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupNoTalkPref, false);
            newChip.setText(notTalk);
            chipGroupNoTalkPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroupNoTalkPref.removeView(v);
                }
            });
        }
    }

    private void addEatingChip(String topic) {
        resultEating.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, chipGroupEatingPref, false);
        newChip.setText(topic);
        chipGroupEatingPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroupEatingPref.removeView(v);
                resultEating.remove(((Chip) v).getText());
            }
        });

    }

    private void addTastesChip(String topic) {
        resultTastes.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupTastePref, false);
        newChip.setText(topic);
        chipGroupTastePref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroupTastePref.removeView(v);
                resultTastes.remove(((Chip) v).getText());
            }
        });
    }

    private void addInterestChip(String topic) {
        resultInterest.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupTalkPref, false);
        newChip.setText(topic);
        chipGroupTalkPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroupTalkPref.removeView(v);
                resultInterest.remove(((Chip) v).getText());
            }
        });
    }

    private void addNoEatChip(String topic) {
        resultNotEat.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, chipGroupNoEatPref, false);
        newChip.setText(topic);
        chipGroupNoEatPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroupNoEatPref.removeView(v);
                resultNotEat.remove(((Chip) v).getText());
            }
        });

    }

    private void addNoTalkChip(String topic) {
        resultNotTalk.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, chipGroupNoTalkPref, false);
        newChip.setText(topic);
        chipGroupNoTalkPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroupNoTalkPref.removeView(v);
                resultNotTalk.remove(((Chip) v).getText());
            }
        });

    }

    @Override
    protected void setTopBar() {

    }
}