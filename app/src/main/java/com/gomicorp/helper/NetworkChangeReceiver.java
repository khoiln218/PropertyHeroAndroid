package com.gomicorp.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gomicorp.propertyhero.R;

/**
 * Created by CTO-HELLOSOFT on 5/4/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private AppCompatActivity activity;
    private ViewGroup rootView;

    public NetworkChangeReceiver(AppCompatActivity activity, ViewGroup rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNetworkAvailable(context))
            rootView.removeView(activity.findViewById(R.id.networkLayout));
        else {
            FrameLayout networkLayout = (FrameLayout) activity.findViewById(R.id.networkLayout);
            if (networkLayout == null) {
                View view = activity.getLayoutInflater().inflate(R.layout.layout_no_connection, rootView, false);
                rootView.addView(view);
            }
        }
    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable())
                return true;
        }

        return false;
    }
}
