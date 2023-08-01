package vn.hellosoft.hellorent.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.activities.ProductDetailsActivity;
import vn.hellosoft.hellorent.adapters.ProductListAdapter;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.hellorent.extras.EndPoints;
import vn.hellosoft.hellorent.extras.UrlParams;
import vn.hellosoft.hellorent.json.Parser;
import vn.hellosoft.hellorent.model.Product;
import vn.hellosoft.helper.L;
import vn.hellosoft.ui.DividerItemDecoration;

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

            resultLayout.setVisibility(View.GONE);
        } else
            resultLayout.setVisibility(View.VISIBLE);

    }

    private void fetchListUserLikes() {
        String url = EndPoints.URL_LIST_USER_LIKE
                .replace(UrlParams.USER_ID, String.valueOf(AppController.getInstance().getPrefManager().getUserID()))
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                productList.clear();
                productList.addAll(Parser.productList(response));
                adapter.addProductList(productList);

                if (productList.size() == 0)
                    resultLayout.setVisibility(View.VISIBLE);
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

}
