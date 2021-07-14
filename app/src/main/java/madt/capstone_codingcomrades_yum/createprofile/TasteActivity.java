package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityTastesBinding;


public class TasteActivity extends BaseActivity {
    private ActivityTastesBinding binding;

    final static String[] topics = {"Sushi","Ramen", "Halal", "Dessert", "Coffee", "Italian", "Ceviche"};
    final static String[] preferences = {"Salty", "Sweet", "Sour"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tastes);
        binding.interestTopics.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topics));
        binding.spinnerPreference.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, preferences));
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.interestTopics.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(TasteActivity.this, "'I enjoy eating' field empty", Toast.LENGTH_SHORT).show();
                }else if(binding.spinnerPreference.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(TasteActivity.this, "'Preferences in taste' field empty", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(TasteActivity.this, InterestActivity.class);
                    startActivity(i);
                }


            }
        });
    }

    @Override
    protected void setTopBar() {

    }

}