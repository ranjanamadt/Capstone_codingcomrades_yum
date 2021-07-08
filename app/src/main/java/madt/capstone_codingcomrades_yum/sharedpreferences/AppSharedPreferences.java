package madt.capstone_codingcomrades_yum.sharedpreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import madt.capstone_codingcomrades_yum.core.YumApp;

public class AppSharedPreferences {

    public static AppSharedPreferences appSharedPreferences;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;

    private AppSharedPreferences() {

    }

    @SuppressLint("CommitPrefEdits")
    public static AppSharedPreferences getInstance() {
        if (appSharedPreferences == null) {
            appSharedPreferences = new AppSharedPreferences();
        }
        if (editor == null) {
            editor = YumApp.mContext.getSharedPreferences(SharedConstants.SHARED_NAME, Context.MODE_PRIVATE).edit();
        }
        if (prefs == null) {
            prefs = YumApp.getGlobalPrefs();
        }
        return appSharedPreferences;
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return prefs.getString(key, "");
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return prefs.getInt(key, 0);
    }


    public void setBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public void clearKey(String keyName) {
        prefs.edit().remove(keyName).apply();
    }

}
