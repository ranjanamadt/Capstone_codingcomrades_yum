package madt.capstone_codingcomrades_yum.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.LoginScreenBinding;

public class LoginActivity extends BaseActivity {
    private LoginScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_screen);
        binding.phoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,
                        LoginWithPhoneNumberActivity.class);
                startActivity(i);
            }
        });
        binding.fbLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Facebook login code
            }
        });

    }

    @Override
    protected void setTopBar() {

    }
}