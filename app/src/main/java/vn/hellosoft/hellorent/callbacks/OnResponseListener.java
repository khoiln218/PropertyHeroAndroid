package vn.hellosoft.hellorent.callbacks;

import com.android.volley.VolleyError;

import vn.hellosoft.hellorent.model.ResponseInfo;

/**
 * Created by CTO-HELLOSOFT on 4/8/2016.
 */
public interface OnResponseListener {
    void onSuccess(ResponseInfo info);

    void onError(VolleyError error);
}
