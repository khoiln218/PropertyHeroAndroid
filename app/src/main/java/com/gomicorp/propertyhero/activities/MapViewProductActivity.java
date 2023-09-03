package com.gomicorp.propertyhero.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ClusterAdapter;
import com.gomicorp.propertyhero.callbacks.OnLoadProductListener;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.ProductItem;
import com.gomicorp.propertyhero.model.SearchInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("NonConstantResourceId")
public class MapViewProductActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, ClusterManager.OnClusterClickListener<ProductItem>, ClusterManager.OnClusterItemClickListener<ProductItem> {
    private static final String TAG = MapViewProductActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private ClusterManager<ProductItem> clusterManager;
    private ProductRender productRender;
    private ClusterTask clusterTask;
    private Marker marker;
    private LatLng latLng;
    private SearchInfo searchInfo;
    private String propertyId;
    private String title;
    private String numItems;
    private CoordinatorLayout coordinatorMapView;
    private TextView tvNumItems;
    private ProgressBar mapLoading;
    private List<ProductItem> itemList;
    private ClusterAdapter clusterAdapter;
    private boolean mTimerIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_product);

        Bundle data = getIntent().getBundleExtra(Config.DATA_EXTRA);
        if (data == null)
            finish();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title = data != null ? data.getString(Config.STRING_DATA) : null);
        }

        latLng = data != null ? data.getParcelable(Config.PARCELABLE_DATA) : null;
        if (latLng == null)
            latLng = AppController.getInstance().getPrefManager().getLastLatLng();

        int propertyType = data != null ? data.getInt(Config.DATA_TYPE, Config.UNDEFINED) : 0;

        searchInfo = new SearchInfo(latLng.latitude, latLng.longitude, 0, propertyType);
        clusterTask = new ClusterTask();

        switch (propertyType) {
            case Config.PROPERTY_APARTMENT:
                propertyId = "1010";
                break;
            case Config.PROPERTY_ROOM:
                propertyId = "1050";
                break;
            default:
                propertyId = "1000";
                break;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        coordinatorMapView = (CoordinatorLayout) findViewById(R.id.coordinatorMapView);
        tvNumItems = (TextView) findViewById(R.id.tvNumItems);
        mapLoading = (ProgressBar) findViewById(R.id.mapLoading);

        numItems = "..." + getString(R.string.unit_item);
        tvNumItems.setText(numItems);

        setupRecyclerCluster();

        findViewById(R.id.findMarker).setOnClickListener(this);
        findViewById(R.id.btnListView).setOnClickListener(this);
    }

    private void setupRecyclerCluster() {
        itemList = new ArrayList<>();
        clusterAdapter = new ClusterAdapter(itemList);

        RecyclerView recyclerCluster = (RecyclerView) findViewById(R.id.recyclerCluster);
        recyclerCluster.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCluster.setItemAnimator(new DefaultItemAnimator());
        recyclerCluster.setAdapter(clusterAdapter);
        recyclerCluster.addOnItemTouchListener(new RecyclerTouchListner(this, recyclerCluster, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                launchProductDetails(itemList.get(position).getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.DOWN) {
                    itemList.clear();
                    clusterAdapter.setItemList(itemList);
                    if (marker != null)
                        marker.setAlpha((float) 1);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerCluster);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ProductRequest.cancelRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_filter:
                ActivityCompat.startActivityForResult(this, new Intent(this, FilterActivity.class), Config.REQUEST_FILTER, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findMarker:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btnListView:
                Intent intent = new Intent(this, ListViewProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.STRING_DATA, title);
                bundle.putParcelable(Config.PARCELABLE_DATA, searchInfo);
                intent.putExtra(Config.DATA_EXTRA, bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_FILTER && resultCode == Config.SUCCESS_RESULT) {
            if (this.googleMap != null)
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            else
                finish();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.googleMap.setMyLocationEnabled(true);
        clusterManager = new ClusterManager<>(this, this.googleMap);
        productRender = new ProductRender(this, this.googleMap, clusterManager);

        clusterManager.setRenderer(productRender);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

        this.googleMap.setOnCameraMoveStartedListener(productRender);
        this.googleMap.setOnCameraIdleListener(productRender);

        float zoom = AppController.getInstance().getPrefManager().getMapZoom();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public boolean onClusterClick(Cluster<ProductItem> cluster) {
        itemList.clear();
        itemList.addAll(cluster.getItems());
        clusterAdapter.setItemList(itemList);

        if (marker != null)
            marker.setAlpha((float) 1);

        marker = productRender.getMarker(cluster);
        if (marker != null)
            marker.setAlpha((float) 0.5);

        return true;
    }

    @Override
    public boolean onClusterItemClick(ProductItem productItem) {
        if (marker != null)
            marker.setAlpha((float) 1);

        itemList.clear();
        clusterAdapter.setItemList(itemList);

        launchProductDetails(productItem.getId());
        return false;
    }

    private void fetchProductData(LatLng southwest, LatLng northeast) {
        searchInfo.setStartLat(String.valueOf(southwest.latitude));
        searchInfo.setStartLng(String.valueOf(southwest.longitude));
        searchInfo.setEndLat(String.valueOf(northeast.latitude));
        searchInfo.setEndLng(String.valueOf(northeast.longitude));
        searchInfo.setPropertyID(propertyId);

        Map<String, String> filterSet = AppController.getInstance().getPrefManager().getFilterSet();

        searchInfo.setMinPrice(filterSet.get(Config.KEY_MIN_PRICE));
        searchInfo.setMaxPrice(filterSet.get(Config.KEY_MAX_PRICE));
        searchInfo.setMinArea(filterSet.get(Config.KEY_MIN_AREA));
        searchInfo.setMaxArea(filterSet.get(Config.KEY_MAX_AREA));
        searchInfo.setBed(filterSet.get(Config.KEY_BED));
        searchInfo.setBath(filterSet.get(Config.KEY_BATH));
        searchInfo.setStatus(String.valueOf(Config.UNDEFINED));

        ProductRequest.search(searchInfo, new OnLoadProductListener() {
            @Override
            public void onSuccess(List<Product> products, int totalItems) {
                tvNumItems.setText(numItems.replace("...", String.valueOf(totalItems)));
                addItems(products);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProductList()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void addItems(List<Product> products) {
        clusterManager.clearItems();
        if (products.size() == 0) {
            Snackbar snackbar = Snackbar.make(coordinatorMapView, getString(R.string.text_no_product), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        for (Product obj : products)
            clusterManager.addItem(new ProductItem(obj.getId(), obj.getThumbnail(), obj.getPrice(), obj.getAddresss(), obj.getLatitude(), obj.getLongitude()));

        clusterManager.cluster();

        if (mapLoading.getVisibility() == View.VISIBLE)
            mapLoading.setVisibility(View.GONE);
    }

    private void launchProductDetails(long id) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(Config.DATA_EXTRA, id);
        startActivity(intent);
    }

    private class ProductRender extends DefaultClusterRenderer<ProductItem> implements GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

        public ProductRender(Context context, GoogleMap map, ClusterManager<ProductItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull ProductItem item, @NonNull MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            Bitmap bitmap = Utils.getBitmap(MapViewProductActivity.this, R.drawable.ic_vector_product_item);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        protected void onClusterItemRendered(@NonNull ProductItem clusterItem, @NonNull Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(@NonNull Cluster<ProductItem> cluster, @NonNull MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<ProductItem> cluster) {
            return cluster.getSize() > 1;
        }

        @Override
        public void onCameraIdle() {
            if (mTimerIsRunning) {
                GoogleMap googleMap = MapViewProductActivity.this.googleMap;
                if (googleMap == null) return;

                float zoom = googleMap.getCameraPosition().zoom;
                googleMap.clear();

                clusterTask.cancel(true);
                clusterTask = new ClusterTask();
                clusterTask.execute(zoom);
                mTimerIsRunning = false;
            }
        }

        @Override
        public void onCameraMoveStarted(int i) {
            mTimerIsRunning = true;
        }
    }

    private class ClusterTask extends AsyncTask<Float, Void, Float> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mapLoading.getVisibility() == View.GONE)
                mapLoading.setVisibility(View.VISIBLE);

            tvNumItems.setText(numItems);
        }

        @Override
        protected Float doInBackground(Float... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(Float zoom) {
            super.onPostExecute(zoom);
            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            AppController.getInstance().getPrefManager().addLastLatLng(bounds.getCenter(), zoom);

            if (zoom > 7)
                fetchProductData(bounds.southwest, bounds.northeast);
        }
    }
}