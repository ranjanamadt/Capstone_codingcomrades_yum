package madt.capstone_codingcomrades_yum.core;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;

public abstract class BaseFragment extends Fragment {




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
