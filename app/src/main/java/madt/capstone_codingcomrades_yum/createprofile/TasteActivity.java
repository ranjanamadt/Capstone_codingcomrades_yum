package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.chip.Chip;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityTastesBinding;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class TasteActivity extends BaseActivity  {
    private ActivityTastesBinding binding;

    final static String[] topics = {"Sushi","Ramen", "Halal", "Dessert", "Coffee", "Italian", "Ceviche"};
    final static String[] preferences = {"Salty", "Sweet", "Sour"};
    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tastes);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();

        binding.spnEatingPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topics));
        binding.spnTastesPreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                preferences));


        binding.spnEatingPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(++check > 1)
                addEatingChip(topics[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
  binding.spnTastesPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addTastesChip(preferences[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.spnEatingPreferences.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(TasteActivity.this, "'I enjoy eating' field empty", Toast.LENGTH_SHORT).show();
                }else if(binding.spnTastesPreferences.getSelectedItem().toString().isEmpty()){
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

    }
 private void addTastesChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastes, false);
        newChip.setText(topic);
        binding.chipGroupTastes.addView(newChip);

    }


}