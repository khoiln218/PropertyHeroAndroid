package com.gomicorp.propertyhero.callbacks;

import com.android.volley.VolleyError;
import com.gomicorp.propertyhero.model.Product;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/10/2016.
 */
public interface OnLoadProductListener {

    void onSuccess(List<Product> products, int totalItems);

    void onError(VolleyError error);
}
