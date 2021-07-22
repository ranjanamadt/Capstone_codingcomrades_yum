package madt.capstone_codingcomrades_yum.fcm;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.networking.VolleySingleton;

public class SendPushHelper {
    public static void sendPush(Activity activity,String deviceToken,String title, String message){
        JSONObject pushObject = new JSONObject();

        try {
            //pushObject.put("to", "fY6lse8WSnOXeq-V1n7ZOC:APA91bF6cswtiufUffyLHeqQtXorbUBCCcefQG_AvGBtIevgDl3qBrKiVZl8lbHgosKa1gd2uTW0tvgrX5n-x-qfTQl7Lfn22_iiP0V-tDMQQUqjh3HgbC-urRxXAy9F0C6qzYSAz_g_");
            pushObject.put("to", deviceToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", message);

            pushObject.put("notification", notiObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                pushObject,

                response -> Log.e("response", "done"),
                error -> Log.e("error", error.getMessage() + "//")) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String serverKey ="AAAAlcLTjIE:APA91bG4A5dOPjMKQiKCz2wrefa7rDO7A2SHRbx1vpykbmJA2S313iCy2zpQcOo3fZ2gpmNCrbR7EgII0Yvbkpubr4jMc7dA2GJMkEbcr5cHZ3L8cJITRk4aLUZGhrceZe-ZUWgmViFJ";
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key="+serverKey);
                return params;
            }
        };
        VolleySingleton.getInstance(activity.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
