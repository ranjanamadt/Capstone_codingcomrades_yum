package madt.capstone_codingcomrades_yum.utils;

import android.app.Activity;
import android.app.ProgressDialog;

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


}
