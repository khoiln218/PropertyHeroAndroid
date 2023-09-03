package com.gomicorp.propertyhero.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.ProductDetailsActivity;
import com.gomicorp.propertyhero.adapters.ProductCollectionAdapter;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.json.Utils;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.ui.DividerItemDecoration;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductViewFragment extends Fragment {
    private static final String TAG = FavoriteFragment.class.getSimpleName();

    private RecyclerView recyclerViewItems;
    private RelativeLayout resultLayout;
    private List<Product> productList;
    private ProductCollectionAdapter adapter;

    public ProductViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_product_view, container, false);

        recyclerViewItems = (RecyclerView) root.findViewById(R.id.recyclerViewItems);
        resultLayout = (RelativeLayout) root.findViewById(R.id.resultLayout);
        resultLayout.setVisibility(View.GONE);

        productList = new ArrayList<>();
        adapter = new ProductCollectionAdapter(productList);

        recyclerViewItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewItems.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewItems.setAdapter(adapter);
        recyclerViewItems.addOnItemTouchListener(new RecyclerTouchListner(getActivity(), recyclerViewItems, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                showSelectConfirm(productList.get(position).getId());
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

        fetchListUserViews_V2();
    }

    private void showSelectConfirm(final long id) {
        String[] items = getResources().getStringArray(R.array.selected_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                        intent.putExtra(Config.DATA_EXTRA, id);
                        startActivity(intent);
                        break;
                    case 1:
//                        AppController.getInstance().getPrefManager().removeProductView(id);
                        onStart();
                        break;
                    default:
                        break;
                }

                dialog.dismiss();
            }
        });

        builder.show();
    }
    private void fetchListUserViews_V2() {
        MultipartRequest reqGet = new MultipartRequest(EndPoints.URL_GET_RECENTLY, null, Utils.mimeType, pathBodyRequestInfo(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                productList.clear();
                productList.addAll(Parser.productList_Favorite(response));
                adapter.setProductList(productList);

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
