package vn.hellosoft.hellorent.callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import vn.hellosoft.hellorent.model.Product;

/**
 * Created by CTO-HELLOSOFT on 5/10/2016.
 */
public interface OnLoadProductListener {

    void onSuccess(List<Product> products, int totalItems);

    void onError(VolleyError error);
}
