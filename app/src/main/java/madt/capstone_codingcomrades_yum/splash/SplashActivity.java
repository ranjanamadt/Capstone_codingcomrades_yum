package madt.capstone_codingcomrades_yum.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppSharedPreferences.getInstance().setString(SharedConstants.TEST, "Implemented");
        yLog("Test share : ", AppSharedPreferences.getInstance().getString(SharedConstants.TEST));
        yToast(this,"toast");
        ySnackbar(this, "snackbar");

    }
}