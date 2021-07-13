package madt.capstone_codingcomrades_yum.splash;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.createprofile.AboutMeActivity;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppSharedPreferences.getInstance().getString(SharedConstants.USER_UID).isEmpty()) {
                    // If User already logged in
                    Intent i = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    startActivity(i);
                } else {

                    // If user need to logged in
                    Intent i = new Intent(SplashActivity.this,
                            AboutMeActivity.class);
                    startActivity(i);
                }


                finish();

            }
        }, 3000);
        getFCMToken();
    }

    @Override
    protected void setTopBar() {

    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    yLog(ContentValues.TAG, "Fetching FCM registration token failed" + task.getException());
                    return;
                }
                Log.e("device token : ", task.getResult().toString());
                AppSharedPreferences.getInstance().setString(SharedConstants.DEVICE_TOKEN, task.getResult().toString());
            }
        });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

}