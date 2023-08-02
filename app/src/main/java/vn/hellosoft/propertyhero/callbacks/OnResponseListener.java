package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import vn.hellosoft.propertyhero.model.ResponseInfo;

/**
 * Created by CTO-HELLOSOFT on 4/8/2016.
 */
public interface OnResponseListener {
    void onSuccess(ResponseInfo info);

    void onError(VolleyError error);
}
