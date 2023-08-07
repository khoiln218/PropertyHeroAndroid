package com.gomicorp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.json.Utils;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    /**
     * Subscribe to a topic
     */
    public static void subscribeToTopic(String topic) {
//        GcmPubSub pubSub = GcmPubSub.getInstance(AppController.getInstance().getApplicationContext());
//        InstanceID instanceID = InstanceID.getInstance(AppController.getInstance().getApplicationContext());
//        String token = null;
//        try {
//            token = instanceID.getToken(AppController.getInstance().getApplicationContext().getString(R.string.gcm_defaultSenderId),
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            if (token != null) {
//                pubSub.subscribe(token, "/topics/" + topic, null);
//                Log.e(TAG, "Subscribed to topic: " + topic);
//            } else {
//                Log.e(TAG, "error: gcm registration id is null");
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
//        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String key = intent.getStringExtra(Config.KEY);
            switch (key) {
                case Config.SUBSCRIBE:
                    // subscribe to a topic
                    String topic = intent.getStringExtra(Config.TOPIC);
                    subscribeToTopic(topic);
                    break;
                case Config.UNSUBSCRIBE:
                    break;
                default:
                    String address = intent.getStringExtra(Config.ADDRESS_DATA);
                    LatLng latLng = intent.getParcelableExtra(Config.PARCELABLE_DATA);
                    registerGCM(address, latLng.latitude, latLng.longitude);
            }
        }
    }

    private void registerGCM(String address, double lat, double lng) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        try {
//            InstanceID instanceID = InstanceID.getInstance(this);
//            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//
//            AppController.getInstance().getPrefManager().addToken(token);
//
//            // sending the registration id to our server
//            sendRegistrationToServer(token, address, lat, lng);
//
//            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to complete token refresh", e);
//
//            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
//        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token, String address, double lat, double lng) {
        MultipartRequest reqDevice = new MultipartRequest(EndPoints.URL_CREATE_DEVICE, null, Utils.mimeType, getDevicePathBody(token, address, lat, lng), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    Intent registrationComplete = new Intent(Config.SENT_TOKEN_TO_SERVER);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at sendRegistrationToServer");
            }
        });

        AppController.getInstance().addToRequestQueue(reqDevice, TAG);
    }

    private byte[] getDevicePathBody(String token, String address, double lat, double lng) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "Token", token);
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            Utils.buildTextPart(dos, "DeviceType", Config.DEVICE_TYPE);
            Utils.buildTextPart(dos, "Version", com.gomicorp.helper.Utils.getVersionName());
            Utils.buildTextPart(dos, "Address", Utils.iso88951_To_utf8(address));
            Utils.buildTextPart(dos, "Latitude", String.valueOf(lat));
            Utils.buildTextPart(dos, "Longitude", String.valueOf(lng));


            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
