package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.District;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadDistrictListener {
    void onSuccess(List<District> districts);

    void onError(VolleyError error);
}
