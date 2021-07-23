package madt.capstone_codingcomrades_yum.matcheslisting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.MatchDetailBinding;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;

public class MatchDetail extends BaseActivity {
    private MatchDetailBinding binding;
    protected LoginUserDetail mLoginDetail;
    User matchUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.match_detail);
        mLoginDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);

        if (getIntent().hasExtra(FSConstants.MATCHES_DETAIL.OTHER_USER_ID)) {
            FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, getIntent().getStringExtra(FSConstants.MATCHES_DETAIL.OTHER_USER_ID)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    matchUser = new User(document);
                    binding.mdUserName.setText(matchUser.getFullName());
                    binding.mdAge.setText(String.valueOf(matchUser.getAge()));
                    binding.mdBio.setText( matchUser.getAboutMe() );
                    addNotEat(Arrays.asList(matchUser.getNot_eat()));
                    addEnjoyEating(Arrays.asList(matchUser.getEnjoy_eating()));
                    addInterests(Arrays.asList(matchUser.getInterest()));
                    addNotTalk(Arrays.asList(matchUser.getNot_talk()));

                    if(matchUser.getProfileImage() != null){
                        binding.imageBtn.setImageBitmap(matchUser.getProfileBitmapImage());
                    }
                    setTopBar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        }
        binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(FSConstants.MATCHES_DETAIL.ACTION, "Accept");
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        binding.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(FSConstants.MATCHES_DETAIL.ACTION, "Reject");
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MatchDetail.this);
                builder.setTitle(getString(R.string.report_user_dialog_title).replace("##Username##" , matchUser.getFullName()));
                builder.setMessage(getString(R.string.report_user_dialog_message));

                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.report_user_action), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> reportList = mLoginDetail.getReport_list();
                        reportList.add(matchUser.getUuid());

                        Map<String, Object> mapReportList = new HashMap<>();
                        mapReportList.put(FSConstants.USER.REPORT_LIST, reportList);

                        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS,mLoginDetail.getUuid(),mapReportList).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mLoginDetail.setReport_list(reportList);
                                AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(mLoginDetail).toString());

                                ySnackbar(MatchDetail.this, getString(R.string.report_confirmation));
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });
    }

    private void addEnjoyEating(List<Object> enjoyEatingList) {
        for (Object enjoyEat : enjoyEatingList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipLikes, false);
            newChip.setText(enjoyEat.toString());
            binding.chipLikes.addView(newChip);
        }
    }

    private void addInterests(List<Object> tasteList) {
        for (Object taste : tasteList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipTopics, false);
            newChip.setText(taste.toString());
            binding.chipTopics.addView(newChip);
        }
    }


    private void addNotEat(List<Object> notEatList) {
        for (Object notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipDontEat, false);
            newChip.setText(notEat.toString());
            binding.chipDontEat.addView(newChip);
        }

    }

    private void addNotTalk(List<Object> notTalkList) {
        for (Object notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipDontTalk, false);
            newChip.setText(notTalk.toString());
            binding.chipDontTalk.addView(newChip);

        }
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                matchUser.getFullName(),
                true,
                false,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }
}
