package vn.hellosoft.hellorent.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.hellorent.model.Info;

/**
 * Created by CTO-HELLOSOFT on 4/19/2016.
 */
public interface OnLoadInfoListener {

    void onSuccess(List<Info> infos);

    void onError(VolleyError error);
}
