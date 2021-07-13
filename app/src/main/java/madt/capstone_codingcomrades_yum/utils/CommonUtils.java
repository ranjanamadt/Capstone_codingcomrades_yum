package madt.capstone_codingcomrades_yum.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class CommonUtils {
    static ProgressDialog progress;

    public static void showProgress(Activity activity) {
        progress = new ProgressDialog(activity);
        progress.setMessage("Loading... ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!progress.isShowing())
            progress.show();
    }

    public static void hideProgress() {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.hide();
            }
        }
    }


}
