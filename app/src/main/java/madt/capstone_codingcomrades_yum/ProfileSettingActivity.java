package madt.capstone_codingcomrades_yum;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

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
    private List<String> enjoyEatingList;
    private List<String> tasteList;
    private List<String> interestList;
    private List<String> notEatList;
    private List<String> notTalkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        chipGroupEatingPref = findViewById(R.id.chipGroupEatingPref);
        chipGroupTastePref = findViewById(R.id.chipGroupTastePref);
        chipGroupTalkPref = findViewById(R.id.chipGroupTalkPref);
        chipGroupNoEatPref = findViewById(R.id.chipGroupNoEatPref);
        chipGroupNoTalkPref = findViewById(R.id.chipGroupNoTalkPref);

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addTalkAbout((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));

                getEatingPreferences();
                getTastesPreferences();
                getNotEatPreferences();
                getNotTalkPreferences();
                getInterestsPreferences();
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
            }
        });
    }

    private void getTastesPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.TASTE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tasteList = (List<String>) documentSnapshot.get("data");
                //setTastesDropdown(tasteList);
            }
        });
    }

    private void getNotEatPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_EAT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                notEatList = (List<String>) documentSnapshot.get("data");
                //setNotEatDropdown();
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
                //setNotTalkDropdown();
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
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                interestList = (List<String>) documentSnapshot.get("data");
                //setInterestDropdown();
                CommonUtils.hideProgress();
            }
        });

    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
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

    @Override
    protected void setTopBar() {

    }
}