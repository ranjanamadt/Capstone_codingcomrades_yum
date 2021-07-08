package madt.capstone_codingcomrades_yum.core;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity  extends AppCompatActivity {

   protected void yLog(String tag, String message){
        Log.e(tag,message);
    }
}
