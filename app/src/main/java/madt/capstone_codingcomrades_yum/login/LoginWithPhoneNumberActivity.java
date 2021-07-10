package madt.capstone_codingcomrades_yum.login;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityLoginWithPhoneNumberBinding;

public class LoginWithPhoneNumberActivity extends BaseActivity {

    ActivityLoginWithPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_login_with_phone_number);
        binding.editTextTextPersonName.setText("test");

    }
}