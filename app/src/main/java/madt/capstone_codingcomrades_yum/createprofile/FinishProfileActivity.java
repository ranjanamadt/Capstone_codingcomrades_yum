package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.HomeActivity;
import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityFinishProfileBinding;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FinishProfileActivity extends BaseActivity {
    private ActivityFinishProfileBinding binding;
    private FirebaseCRUD crudClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_profile);

        binding.btnConfirmFinishProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinishProfileActivity.this,
                        HomeActivity.class);
                startActivity(i);
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", AboutMeActivity.firstName);
                user.put("lastName", AboutMeActivity.lastName);
                user.put("gender", AboutMeActivity.gender);
                user.put("sePref", AboutMeActivity.sePref);
                user.put("dob", AboutMeActivity.dob);
                crudClass.create("users", user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String savedUserID = documentReference.getId();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ySnackbar(FinishProfileActivity.this,getString(R.string.error_saving_user));
                    }
                });
            }
        });/**/
    }

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

}