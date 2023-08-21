package com.gomicorp.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/4/2016.
 */
public class GoogleApiHelper implements LocationListener {

    private final Activity context;
    private final LocationRequest locationRequest;
    private final FusedLocationProviderClient fusedLocationClient;

    private final List<LocationListener> listeners = new ArrayList<>();

    public GoogleApiHelper(Activity context) {
        this.context = context;
        locationRequest = new LocationRequest.Builder(Config.UPDATE_INTERVAL).build();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        for (LocationListener listener : listeners) {
            listener.onLocationChanged(location);
        }
    }

    public void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(locationSettingsResponse -> startLocationUpdates());
    }

    public void registerListener(LocationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LocationListener listener) {
        listeners.remove(listener);
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this::onLocationChanged);
    }
}
