package vn.hellosoft.propertyhero.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.propertyhero.model.Product;

/**
 * Created by CTO-HELLOSOFT on 5/10/2016.
 */
public interface OnLoadProductListener {

    void onSuccess(List<Product> products, int totalItems);

    void onError(VolleyError error);
}
