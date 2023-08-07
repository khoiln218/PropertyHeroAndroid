package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Property;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadPropertyListener {
    void onSuccess(List<Property> properties);

    void onError(VolleyError error);
}
