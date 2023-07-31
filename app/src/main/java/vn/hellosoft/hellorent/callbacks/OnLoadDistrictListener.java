package vn.hellosoft.hellorent.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.hellorent.model.District;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public interface OnLoadDistrictListener {
    void onSuccess(List<District> districts);

    void onError(VolleyError error);
}
