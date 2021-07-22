package madt.capstone_codingcomrades_yum.splash;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import madt.capstone_codingcomrades_yum.HomeActivity;
import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.createprofile.AboutMeActivity;
import madt.capstone_codingcomrades_yum.createprofile.FinishProfileActivity;
import madt.capstone_codingcomrades_yum.createprofile.FoodTopicsActivity;
import madt.capstone_codingcomrades_yum.createprofile.InterestActivity;
import madt.capstone_codingcomrades_yum.createprofile.TasteActivity;
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
                Intent i;
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // If User already logged in

                    i = new Intent(SplashActivity.this,
                            LoginActivity.class);

                } else {

                    // If user need to logged in
                    if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.FINISH_PROFILE_DONE)) {
                        i = new Intent(SplashActivity.this,
                                AboutMeActivity.class);
                    }else {
                        i = new Intent(SplashActivity.this,
                                HomeActivity.class);
                    }
                   /* if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.ABOUT_DONE)) {
                        i = new Intent(SplashActivity.this,
                                AboutMeActivity.class);
                    } else if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.TASTE_DONE)) {
                        i = new Intent(SplashActivity.this,
                                TasteActivity.class);
                    } else if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.INTEREST_DONE)) {
                        i = new Intent(SplashActivity.this,
                                InterestActivity.class);
                    } else if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.FOOD_TOPIC_DONE)) {
                        i = new Intent(SplashActivity.this,
                                FoodTopicsActivity.class);
                    }else if (!AppSharedPreferences.getInstance().getBoolean(SharedConstants.FINISH_PROFILE_DONE)) {
                        i = new Intent(SplashActivity.this,
                                FinishProfileActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this,
                                HomeActivity.class);
                    }*/
                }

                startActivity(i);
                finish();

            }
        }, 3000);
        getFCMToken();
    }

    @Override
    protected void setTopBar() {

    }

    public static void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                   Log.e(ContentValues.TAG, "Fetching FCM registration token failed" + task.getException());
                    return;
                }
                Log.e("device token : ", task.getResult().toString());
                AppSharedPreferences.getInstance().setString(SharedConstants.DEVICE_TOKEN, task.getResult().toString());
            }
        });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

}