package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Info;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/19/2016.
 */
public interface OnLoadInfoListener {

    void onSuccess(List<Info> infos);

    void onError(VolleyError error);
}
