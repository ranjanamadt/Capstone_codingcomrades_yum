package madt.capstone_codingcomrades_yum.core;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {

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
}
