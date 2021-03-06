package madt.capstone_codingcomrades_yum.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;



public class YumApp extends Application {
    public static final String TAG = YumApp.class
            .getSimpleName();

    private static YumApp mInstance;

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public static SharedPreferences getGlobalPrefs() {
        return mContext.getSharedPreferences(SharedConstants.SHARED_NAME, MODE_PRIVATE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

}
