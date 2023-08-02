package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Marker;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public interface OnLoadMarkerListener {

    void onSuccess(List<Marker> markers);

    void onError(VolleyError error);
}
