package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Feature;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public interface OnLoadFeatureListener {
    void onSuccess(List<Feature> features);

    void onError(VolleyError error);
}
