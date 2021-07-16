package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import madt.capstone_codingcomrades_yum.HomeActivity;
import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityFinishProfileBinding;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FinishProfileActivity extends BaseActivity {
    private ActivityFinishProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_profile);
        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user id :", documentSnapshot.getId() + " ");

                String username = documentSnapshot.getString(FSConstants.USER.FIRST_NAME) + " " + documentSnapshot.getString(FSConstants.USER.LAST_NAME);

                binding.tvUserName.setText(username);
                binding.tvGender.setText(documentSnapshot.getString(FSConstants.USER.GENDER));
                binding.tvAge.setText(documentSnapshot.getString(FSConstants.USER.DOB));

                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));

                if (!LoginActivity.profile_image.isEmpty())
                    Picasso.get().load(LoginActivity.profile_image).into(binding.profileImage);

               // getEnjoyEating(FirebaseAuth.getInstance().getUid());
                CommonUtils.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });

        binding.btnConfirmFinishProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinishProfileActivity.this,
                        HomeActivity.class);
                startActivity(i);
            }
        });
    }

/*    private void getEnjoyEating(String userId) {
        FirebaseCRUD.getInstance().getAll(FSConstants.Collections.PREFERENCES).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                    List<String> enjoyEatingList = new ArrayList<String>();
                    String document_userId = "";
                    String document_prefType = "";
                    String document_prefName = "";
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        document_userId = documentSnapshot.getString(FSConstants.PREFERENCE.USER_UID);
                        document_prefType = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_TYPE);

                        if (document_userId.equals(userId) && document_prefType.equals(FSConstants.PREFERENCE_TYPE.ENJOY_EATING)) {
                            document_prefName = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_NAME);
                            if (document_prefName != null && document_prefName.length() > 0) {
                                enjoyEatingList = Arrays.asList(document_prefName.split(","));
                            }
                        }

                    }

                    yLog("user id :", userId);
                    yLog("document_userId :", document_userId);
                    yLog("document_prefType :", document_prefType);
                    yLog("document_prefName :", document_prefName);
                    addEnjoyEating(enjoyEatingList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });
    }

    private void getTaste(String userId) {
        FirebaseCRUD.getInstance().getAll(FSConstants.Collections.PREFERENCES).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                    List<String> tasteList = new ArrayList<String>();
                    String document_userId = "";
                    String document_prefType = "";
                    String document_prefName = "";
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        document_userId = documentSnapshot.getString(FSConstants.PREFERENCE.USER_UID);
                        document_prefType = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_TYPE);

                        if (document_userId.equals(userId) && document_prefType.equals(FSConstants.PREFERENCE_TYPE.TASTE)) {
                            document_prefName = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_NAME);
                            if (document_prefName != null && document_prefName.length() > 0) {
                                tasteList = Arrays.asList(document_prefName.split(","));
                            }
                        }

                    }

                    yLog("user id :", userId);
                    yLog("document_userId :", document_userId);
                    yLog("document_prefType :", document_prefType);
                    yLog("document_prefName :", document_prefName);
                    addTaste(tasteList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });
    }

    private void getNotEat(String userId) {
        FirebaseCRUD.getInstance().getAll(FSConstants.Collections.PREFERENCES).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                    List<String> notEatList = new ArrayList<String>();
                    String document_userId = "";
                    String document_prefType = "";
                    String document_prefName = "";
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        document_userId = documentSnapshot.getString(FSConstants.PREFERENCE.USER_UID);
                        document_prefType = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_TYPE);

                        if (document_userId.equals(userId) && document_prefType.equals(FSConstants.PREFERENCE_TYPE.NOT_EAT)) {
                            document_prefName = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_NAME);
                            if (document_prefName != null && document_prefName.length() > 0) {
                                notEatList = Arrays.asList(document_prefName.split(","));
                            }
                        }

                    }

                    yLog("user id :", userId);
                    yLog("document_userId :", document_userId);
                    yLog("document_prefType :", document_prefType);
                    yLog("document_prefName :", document_prefName);
                    addNotEat(notEatList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });
    }

    private void getNotTalk(String userId) {
        FirebaseCRUD.getInstance().getAll(FSConstants.Collections.PREFERENCES).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                    List<String> notTalkList = new ArrayList<String>();
                    String document_userId = "";
                    String document_prefType = "";
                    String document_prefName = "";
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        document_userId = documentSnapshot.getString(FSConstants.PREFERENCE.USER_UID);
                        document_prefType = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_TYPE);

                        if (document_userId.equals(userId) && document_prefType.equals(FSConstants.PREFERENCE_TYPE.NOT_TALK)) {
                            document_prefName = documentSnapshot.getString(FSConstants.PREFERENCE.PREFERENCE_NAME);
                            if (document_prefName != null && document_prefName.length() > 0) {
                                notTalkList = Arrays.asList(document_prefName.split(","));
                            }
                        }

                    }

                    yLog("user id :", userId);
                    yLog("document_userId :", document_userId);
                    yLog("document_prefType :", document_prefType);
                    yLog("document_prefName :", document_prefName);
                    addNotTalk(notTalkList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });
    }*/

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
                getString(R.string.title_finish_profile),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
        for (String enjoyEat : enjoyEatingList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipGroupEnjoyEat, false);
            newChip.setText(enjoyEat);
            binding.chipGroupEnjoyEat.addView(newChip);
        }

       // getTaste(FirebaseAuth.getInstance().getUid());
    }

    private void addTaste(List<String> tasteList) {
        for (String taste : tasteList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipGroupDownToEat, false);
            newChip.setText(taste);
            binding.chipGroupDownToEat.addView(newChip);
        }

        //getNotEat(FirebaseAuth.getInstance().getUid());
    }

    /*private void addTalkAbout(List<String> talkAbout) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipg, false);
        newChip.setText(topic);
        binding.chipGroupNoFood.addView(newChip);
    }*/

    private void addNotEat(List<String> notEatList) {
        for (String notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipGroupNotEat, false);
            newChip.setText(notEat);
            binding.chipGroupNotEat.addView(newChip);
        }

       // getNotTalk(FirebaseAuth.getInstance().getUid());
    }

    private void addNotTalk(List<String> notTalkList) {
        for (String notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipGroupNotTalk, false);
            newChip.setText(notTalk);
            binding.chipGroupNotTalk.addView(newChip);

            newChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupNotTalk.removeView(v);
                }
            });
        }
    }

}