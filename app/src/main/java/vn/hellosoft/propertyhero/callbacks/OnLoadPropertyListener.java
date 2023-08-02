package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Property;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadPropertyListener {
    void onSuccess(List<Property> properties);

    void onError(VolleyError error);
}
