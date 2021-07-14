package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.util.Arrays;
import java.util.Calendar;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityAboutMeBinding;


public class AboutMeActivity extends BaseActivity {
    private ActivityAboutMeBinding binding;
    public static String firstName = "", lastName = "", gender  = "", sePref  = "", dob  = "";

    final static String[] genders = {"Male","Female", "Non-binary"};
    final static String[] preferences = {"Male","Female", "Genderqueer/Non-Binary", "Prefer not to disclose"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_me);

        binding.sexPrefSp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, preferences));
        binding.genderSp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders));

        binding.btnConfirmAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.firstNameET.getText().toString().isEmpty()){
                    Toast.makeText(AboutMeActivity.this, "First name field empty", Toast.LENGTH_SHORT).show();
                }else if(binding.lastNameET.getText().toString().isEmpty()){
                    Toast.makeText(AboutMeActivity.this, "Last name field empty", Toast.LENGTH_SHORT).show();
                }else if(binding.dobET.getText().toString().isEmpty()){
                    Toast.makeText(AboutMeActivity.this, "Date of Birth field empty", Toast.LENGTH_SHORT).show();
                }else {
                    firstName = binding.firstNameET.getText().toString();
                    lastName = binding.lastNameET.getText().toString();
                    dob = binding.dobET.getText().toString();
                    gender = binding.genderSp.getSelectedItem().toString();
                    sePref = binding.sexPrefSp.getSelectedItem().toString();
                    Intent i = new Intent(AboutMeActivity.this, TasteActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!firstName.equals("")){
            binding.firstNameET.setText(firstName);
        }
        if(!lastName.equals("")){
            binding.lastNameET.setText(lastName);
        }
        if(!dob.equals("")){
            binding.dobET.setText(dob);
        }
        if(!gender.equals("")){
            binding.genderSp.setSelection(Arrays.asList(genders).indexOf(gender));
        }else{
            binding.genderSp.setSelection(0);
        }
        if(!gender.equals("")){
            binding.sexPrefSp.setSelection(Arrays.asList(preferences).indexOf(sePref));
        }else{
            binding.sexPrefSp.setSelection(0);
        }
    }

    @Override
    protected void setTopBar() {

    }



}