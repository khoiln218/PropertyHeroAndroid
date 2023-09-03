package com.gomicorp.propertyhero.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.ProductDetailsActivity;
import com.gomicorp.propertyhero.adapters.ProductListAdapter;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.json.Utils;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.ui.DividerItemDecoration;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private static final String TAG = FavoriteFragment.class.getSimpleName();

    private RelativeLayout resultLayout;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private ProductListAdapter adapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        resultLayout = (RelativeLayout) root.findViewById(R.id.resultLayout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerFavorite);

        productList = new ArrayList<>();
        adapter = new ProductListAdapter(productList, recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getActivity(), recyclerView, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                intent.putExtra(Config.DATA_EXTRA, productList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppController.getInstance().getPrefManager().getUserID() != 0) {
            productList.clear();
            productList.add(null);
            adapter.addProductList(productList);
            fetchListUserLikes();

        } else
            resultLayout.setVisibility(View.VISIBLE);
    }

    private void fetchListUserLikes() {
        if (Config.USE_V2) {
            fetchListUserLikes_V2();
            return;
        }

        String url = EndPoints.URL_LIST_USER_LIKE
                .replace(UrlParams.USER_ID, String.valueOf(AppController.getInstance().getPrefManager().getUserID()))
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                productList.clear();
                productList.addAll(Parser.productList(response));
                adapter.addProductList(productList);

                if (productList.size() == 0)
                    resultLayout.setVisibility(View.VISIBLE);
                else resultLayout.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
                Log.e(TAG, "Error at fetchListUserLikes()");
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    private void fetchListUserLikes_V2() {
        MultipartRequest reqGet = new MultipartRequest(EndPoints.URL_GET_FAVORITE, null, Utils.mimeType, pathBodyRequestInfo(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                productList.clear();
                productList.addAll(Parser.productList_Favorite(response));
                adapter.addProductList(productList);

                if (productList.size() == 0)
                    resultLayout.setVisibility(View.VISIBLE);
                else resultLayout.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
                Log.e(TAG, "Error at fetchListUserLikes()");
            }
        });

        AppController.getInstance().addToRequestQueue(reqGet, TAG);
    }

    private static byte[] pathBodyRequestInfo() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
