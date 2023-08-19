package com.gomicorp.app;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/24/2016.
 */
public class PermissionHelper {

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final String[] GALLERY_PERM = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String CAMERA_PERMS = Manifest.permission.CAMERA;

    public static void hasLocationPermissions(AppCompatActivity activity) {
        List<String> missingPermissions = new ArrayList<>();

        for (String perm : LOCATION_PERMS) {
            if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(perm))
                missingPermissions.add(perm);
        }

        if (missingPermissions.size() > 0) {
            String[] permissions = missingPermissions.toArray(new String[0]);
            activity.requestPermissions(permissions, Config.PERMS_REQUEST);

        }
    }

    public static void hasGalleryPermissions(AppCompatActivity activity) {
        List<String> missingPermissions = new ArrayList<>();

        for (String perm : GALLERY_PERM) {
            if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(perm))
                missingPermissions.add(perm);
        }

        if (missingPermissions.size() > 0) {
            String[] permissions = missingPermissions.toArray(new String[0]);
            activity.requestPermissions(permissions, Config.PERMS_REQUEST);

        }
    }

    public static void hasCameraPermission(AppCompatActivity activity) {
        if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(CAMERA_PERMS)) {
            activity.requestPermissions(new String[]{CAMERA_PERMS}, Config.PERMS_REQUEST);
        }
    }
}

