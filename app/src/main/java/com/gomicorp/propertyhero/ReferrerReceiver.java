package com.gomicorp.propertyhero;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;

import org.json.JSONObject;

/**
 * Created by CTO-HELLOSOFT on 5/27/2016.
 */
public class ReferrerReceiver extends BroadcastReceiver {

    private static final String TAG = ReferrerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action != null && TextUtils.equals(action, "com.android.vending.INSTALL_REFERRER")) {
            try {
                final String referrer = intent.getStringExtra("referrer");

                // Parse parameters
                String[] params = referrer.split("&");
                for (String p : params) {
                    if (p.startsWith("utm_term=")) {
                        final String term = p.substring("utm_term=".length());
                        insertGmob(term);
                        Log.i("ReferrerReceiver", term);

                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void insertGmob(String term) {
        String url = "http://www.admin.hellosoft.vn/api/Utilities/Gmob/ClickID=" + term + "/";
        JsonObjectRequest reqInsert = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(reqInsert, TAG);
    }
}
