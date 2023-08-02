package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Account;

/**
 * Created by CTO-HELLOSOFT on 4/7/2016.
 */
public interface OnAccountRequestListener {
    void onSuccess(List<Account> accounts);

    void onError(VolleyError error);
}
