package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Marker;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public interface OnLoadMarkerListener {

    void onSuccess(List<Marker> markers);

    void onError(VolleyError error);
}
