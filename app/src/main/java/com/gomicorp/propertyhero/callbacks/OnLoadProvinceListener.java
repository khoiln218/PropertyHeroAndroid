package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Province;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadProvinceListener {
    void onSuccess(List<Province> provinces);

    void onError(VolleyError error);
}
