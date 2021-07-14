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
import madt.capstone_codingcomrades_yum.databinding.ActivityInterestsBinding;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class InterestActivity extends BaseActivity {
    private ActivityInterestsBinding binding;
    final static String[] topics = {"Art", "Movies", "Sports", "Gym", "Politics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interests);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        binding.interestTopics.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topics));
        binding.interestTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addInterestChip(topics[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.interestTopics.getSelectedItem().toString().isEmpty()) {
                    Toast.makeText(InterestActivity.this, "'I enjoy eating' field empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(InterestActivity.this, FoodTopicsActivity.class);
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
                getString(R.string.title_interests),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    private void addInterestChip(String topic) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipInterest, false);
        newChip.setText(topic);
        binding.chipInterest.addView(newChip);
    }
}
