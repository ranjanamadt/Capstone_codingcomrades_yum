package madt.capstone_codingcomrades_yum.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import madt.capstone_codingcomrades_yum.createprofile.FoodTopicsActivity;
import madt.capstone_codingcomrades_yum.createprofile.InterestActivity;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.splash.SplashActivity;

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    protected void yLog(String tag, String message) {
        Log.e(tag, message);
    }

    protected void yToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    protected static void ySnackbar(Activity activity, String message) {
        final Snackbar snackbar = Snackbar.make((activity).findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(20000);
        snackbar.setTextColor(Color.RED);
        snackbar.setActionTextColor(Color.BLACK);
        snackbar.setBackgroundTint(Color.LTGRAY);
        snackbar.setAction("Ok", v -> snackbar.dismiss());
        snackbar.show();
    }
    protected abstract void setTopBar();
}
