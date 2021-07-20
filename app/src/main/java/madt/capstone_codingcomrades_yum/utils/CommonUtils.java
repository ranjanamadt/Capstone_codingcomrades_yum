package madt.capstone_codingcomrades_yum.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;

public class CommonUtils {
    static ProgressDialog progress;

    public static void showProgress(Activity activity) {
        try {
            progress = new ProgressDialog(activity);
            progress.setMessage("Loading... ");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (!progress.isShowing())
                progress.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void hideProgress() {
        try {
            if (progress != null) {
                if (progress.isShowing()) {
                    progress.hide();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static  void logoutNow(Activity activity){
        AppSharedPreferences.getInstance().clearAll();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(activity,
                LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       activity.startActivity(i);
    }

}
