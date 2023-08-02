package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Province;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadProvinceListener {
    void onSuccess(List<Province> provinces);

    void onError(VolleyError error);
}
