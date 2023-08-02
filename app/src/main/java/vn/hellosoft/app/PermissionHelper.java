package vn.hellosoft.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/24/2016.
 */
public class PermissionHelper {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final String CAMERA_PERMS = Manifest.permission.CAMERA;
    private static final String PHONE_PERMS = Manifest.permission.CALL_PHONE;
    private static final String ACCOUNTS_PERMS = Manifest.permission.GET_ACCOUNTS;

    public static void initPermissions(Activity activity) {
        List<String> missingPermissions = new ArrayList<>();

        for (String perm : INITIAL_PERMS) {
            if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(perm))
                missingPermissions.add(perm);
        }

        if (missingPermissions.size() > 0) {
            String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            activity.requestPermissions(permissions, Config.PERMS_REQUEST);

        }
    }

    public static void hasCameraPermission(Activity activity) {
        if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(CAMERA_PERMS)) {
            activity.requestPermissions(new String[]{CAMERA_PERMS}, Config.PERMS_REQUEST);
        }
    }

    public static void hasAccountsPermission(Activity activity) {
        if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(ACCOUNTS_PERMS)) {
            activity.requestPermissions(new String[]{ACCOUNTS_PERMS}, Config.PERMS_REQUEST);
        }
    }

    public static void hasPhonePermission(Activity activity) {
        if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(PHONE_PERMS)) {
            activity.requestPermissions(new String[]{PHONE_PERMS}, Config.PERMS_REQUEST);
        }
    }
}

