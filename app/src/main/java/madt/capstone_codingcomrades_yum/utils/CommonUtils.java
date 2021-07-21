package madt.capstone_codingcomrades_yum.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static Bitmap getBitmapImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    public static  String getTimeFromTimeStamp(String timeStamp) {
        long myLong = Long.parseLong(timeStamp);
        Date itemDate = new Date(myLong);
        return new SimpleDateFormat("HH:mm").format(itemDate);
    }
    public static String getDateFromTimeStamp(String dateStamp) {
        long myLong = Long.parseLong(dateStamp);
        Date itemDate = new Date(myLong);
        return new SimpleDateFormat("dd/MM/yyyy").format(itemDate);

    }

}
