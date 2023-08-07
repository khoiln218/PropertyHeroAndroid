package com.gomicorp.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FetchAddressIntentService extends IntentService {

    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    private ResultReceiver receiver;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errMsg = "";

        receiver = intent.getParcelableExtra(Config.RECEIVER);
        if (receiver == null)
            return;

        LatLng location = intent.getParcelableExtra(Config.PARCELABLE_DATA);
        if (location == null) {
            errMsg = getString(R.string.no_location_data_provided);
            deliverResultToReceiver(Config.FAILURE_RESULT, errMsg);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addrs = null;

        try {
            addrs = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            errMsg = getString(R.string.service_not_available);
        } catch (IllegalArgumentException illegalArgumentException) {
            errMsg = getString(R.string.invalid_location);
        }

        if (addrs == null || addrs.size() == 0) {
            if (errMsg.isEmpty())
                errMsg = "...";

            deliverResultToReceiver(Config.FAILURE_RESULT, errMsg);
        } else {
            Address address = addrs.get(0);

            List<String> addrFragments = new ArrayList<>();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addrFragments.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(Config.SUCCESS_RESULT, TextUtils.join(", ", addrFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(Config.RESULT_DATA, msg);
        receiver.send(resultCode, bundle);
    }
}
