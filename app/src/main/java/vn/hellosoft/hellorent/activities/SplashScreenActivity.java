package vn.hellosoft.hellorent.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import vn.hellosoft.app.Config;
import vn.hellosoft.app.GoogleApiHelper;
import vn.hellosoft.app.PermissionHelper;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.callbacks.OnAddressResultListener;
import vn.hellosoft.services.AddressResultReceiver;
import vn.hellosoft.services.FetchAddressIntentService;
import vn.hellosoft.services.RegistrationIntentService;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    private GoogleApiHelper googleApiHelper;
    private LocationListener listener;
    private LatLng latLng;

    private AddressResultReceiver addressReceiver;

    private boolean isRegisterReceiver;
    private BroadcastReceiver registrationReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PermissionHelper.initPermissions(this);

        if (!checkPlayServices())
            launchUpdateGooglePlay();

        googleApiHelper = new GoogleApiHelper(this);
        googleApiHelper.checkLocationSettings();

        registrationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE))
                    launchActivity();
            }
        };

        addressReceiver = new AddressResultReceiver(new Handler());
        addressReceiver.listener = new OnAddressResultListener() {
            @Override
            public void onResult(String address) {
                startRegistration(address);
            }
        };
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Config.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }

        return true;
    }

    private void launchUpdateGooglePlay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.err_title_google_play));
        builder.setMessage(getString(R.string.err_msg_google_play));
        builder.setPositiveButton(getString(R.string.btn_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
                }
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    private void registerReceiver() {
        if (!isRegisterReceiver) {
            LocalBroadcastManager.getInstance(this).registerReceiver(registrationReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
            isRegisterReceiver = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Config.PERMS_REQUEST)
            getLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
        registerReceiver();
    }

    private void getLocation() {
        listener = location -> {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            startAddressIntent();
        };
        googleApiHelper.registerListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        googleApiHelper.removeListener(listener);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationReceiver);
        isRegisterReceiver = false;
    }

    private void startAddressIntent() {
        if (latLng == null)
            latLng = new LatLng(0, 0);

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Config.RECEIVER, addressReceiver);
        intent.putExtra(Config.PARCELABLE_DATA, latLng);
        startService(intent);
    }

    private void startRegistration(String address) {
        Intent intent = new Intent(SplashScreenActivity.this, RegistrationIntentService.class);
        intent.putExtra(Config.KEY, "Register");
        intent.putExtra(Config.ADDRESS_DATA, address);
        intent.putExtra(Config.PARCELABLE_DATA, latLng);
        startService(intent);
    }

    private void launchActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
