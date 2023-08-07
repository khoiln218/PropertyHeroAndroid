package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Feature;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public interface OnLoadFeatureListener {
    void onSuccess(List<Feature> features);

    void onError(VolleyError error);
}
