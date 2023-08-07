package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Account;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/7/2016.
 */
public interface OnAccountRequestListener {
    void onSuccess(List<Account> accounts);

    void onError(VolleyError error);
}
