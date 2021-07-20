package madt.capstone_codingcomrades_yum.matcheslisting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.databinding.MatchDetailBinding;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;

public class MatchDetail extends Activity {
    private MatchDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.match_detail);

        if (getIntent().hasExtra(FSConstants.MATCHES_DETAIL.OTHER_USER_ID)) {
            FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, getIntent().getStringExtra(FSConstants.MATCHES_DETAIL.OTHER_USER_ID)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    User matchUser = new User(document);
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
                // TODO : REPORT DIALOG
                AlertDialog.Builder builder = new AlertDialog.Builder(MatchDetail.this);
                LayoutInflater layoutInflater = LayoutInflater.from(MatchDetail.this);
//                View view = layoutInflater.inflate(R.layout.dialog_create_category, null);
//                builder.setView(view);
//
//                final AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                EditText categoryNameET = view.findViewById(R.id.categoryNameET);
//                Button btnCreate = view.findViewById(R.id.btnCreateCategory);
//
//                btnCreate.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String categoryName = categoryNameET.getText().toString().trim();
//                        if (categoryName.isEmpty()) {
//                            alertBox("Please enter value for Category name");
//                            return;
//                        }
//                        if (categoryList.contains(categoryName)) {
//                            alertBox("Category name already exist!");
//                            return;
//                        }
//
//                        noteAppViewModel.insertCategory(new Category(categoryName));
//                        alertDialog.dismiss();
//                    }
//                });
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
}
