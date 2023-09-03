package com.gomicorp.propertyhero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ProductListAdapter;
import com.gomicorp.propertyhero.callbacks.OnLoadMoreListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProductListener;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.SearchInfo;
import com.gomicorp.ui.DividerItemDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListViewProductActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = ListViewProductActivity.class.getSimpleName();

    private SearchInfo searchInfo;
    private String propertyId;
    private int pageNo = 1;

    private RelativeLayout resultLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerProduct;

    private boolean isFinished = false;
    private List<Product> productList;
    private ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_product);

        Bundle data = getIntent().getBundleExtra(Config.DATA_EXTRA);
        if (data == null)
            finish();

        searchInfo = data.getParcelable(Config.PARCELABLE_DATA);
        propertyId = searchInfo.getPropertyID();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(data.getString(Config.STRING_DATA));

        resultLayout = (RelativeLayout) findViewById(R.id.resultLayout);
        resultLayout.setVisibility(View.GONE);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshProduct);
        recyclerProduct = (RecyclerView) findViewById(R.id.recyclerProduct);

        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(this);


        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        recyclerProduct.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerProduct.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productList.add(null);
        adapter = new ProductListAdapter(productList, recyclerProduct);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerProduct.addItemDecoration(headersDecor);
        recyclerProduct.setAdapter(adapter);

        adapter.setLoadMoreListener(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                headersDecor.invalidateHeaders();
            }
        });


        recyclerProduct.addOnItemTouchListener(new RecyclerTouchListner(this, recyclerProduct, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ListViewProductActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Config.DATA_EXTRA, productList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchProductList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_filter) {
            Intent intent = new Intent(this, FilterActivity.class);
            ActivityCompat.startActivityForResult(this, intent, Config.REQUEST_FILTER, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ProductRequest.cancelRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_FILTER && resultCode == Config.SUCCESS_RESULT) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        isFinished = false;
        pageNo = 1;

        productList.clear();
        adapter.addProductList(productList);

        fetchProductList();
    }

    @Override
    public void onLoadMore() {
        if (!adapter.isLoading() && !isFinished/*productList.size() < totalCertified + totalActivated*/) {
            productList.add(null);
            adapter.addProductList(productList);
            pageNo++;

            fetchProductList();
        }
    }

    private void fetchProductList() {
        ProductRequest.cancelRequest();
        if (adapter != null)
            adapter.setLoading();

        Map<String, String> filterSet = AppController.getInstance().getPrefManager().getFilterSet();
        if (propertyId == null)
            searchInfo.setPropertyID(filterSet.get(Config.KEY_PROPERTY));

        searchInfo.setMinPrice(filterSet.get(Config.KEY_MIN_PRICE));
        searchInfo.setMaxPrice(filterSet.get(Config.KEY_MAX_PRICE));
        searchInfo.setMinArea(filterSet.get(Config.KEY_MIN_AREA));
        searchInfo.setMaxArea(filterSet.get(Config.KEY_MAX_AREA));
        searchInfo.setBed(filterSet.get(Config.KEY_BED));
        searchInfo.setBath(filterSet.get(Config.KEY_BATH));
        searchInfo.setStatus(String.valueOf(Config.UNDEFINED));
        searchInfo.setPageNo(String.valueOf(pageNo));

        ProductRequest.search(searchInfo, new OnLoadProductListener() {
            @Override
            public void onSuccess(List<Product> products, int totalItems) {
                isFinished = totalItems < Config.LIMITED;

                if (productList.size() > 0)
                    productList.remove(productList.size() - 1);

                productList.addAll(products);
                adapter.addProductList(productList);

                updateUI();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProductList()");
                L.showToast(getString(R.string.request_time_out));

                updateUI();
            }
        });
    }

    private void updateUI() {
        refreshLayout.setRefreshing(false);

        if (productList.size() == 0)
            resultLayout.setVisibility(View.VISIBLE);
        else
            resultLayout.setVisibility(View.GONE);
    }
}
