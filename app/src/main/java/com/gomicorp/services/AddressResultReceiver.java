package com.gomicorp.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.callbacks.OnAddressResultListener;

/**
 * Created by CTO-HELLOSOFT on 6/3/2016.
 */
public class AddressResultReceiver extends ResultReceiver {
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public OnAddressResultListener listener;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        listener.onResult(resultData.getString(Config.RESULT_DATA));
    }
}
