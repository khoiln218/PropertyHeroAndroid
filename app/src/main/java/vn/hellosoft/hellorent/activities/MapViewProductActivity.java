package vn.hellosoft.hellorent.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
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
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.adapters.ClusterAdapter;
import vn.hellosoft.hellorent.callbacks.OnLoadProductListener;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.hellorent.json.ProductRequest;
import vn.hellosoft.hellorent.model.Product;
import vn.hellosoft.hellorent.model.ProductItem;
import vn.hellosoft.hellorent.model.SearchInfo;
import vn.hellosoft.helper.L;
import vn.hellosoft.helper.Utils;

public class MapViewProductActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, ClusterManager.OnClusterClickListener<ProductItem>, ClusterManager.OnClusterItemClickListener<ProductItem> {

    private static final String TAG = MapViewProductActivity.class.getSimpleName();

    private GoogleMap googleMap;
    private ClusterManager<ProductItem> clusterManager;
    private ProductRender productRender;
    private ClusterTask clusterTask;
    private Marker marker;

    private LatLng latLng;
    private SearchInfo searchInfo;

    private String title;
    private String numItems;

    private CoordinatorLayout coordinatorMapView;
    private TextView tvNumItems;
    private ProgressBar mapLoading;

    private RecyclerView recyclerCluster;
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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title = data.getString(Config.STRING_DATA));

        latLng = data.getParcelable(Config.PARCELABLE_DATA);
        if (latLng == null)
            latLng = AppController.getInstance().getPrefManager().getLastLatLng();

        searchInfo = new SearchInfo(latLng.latitude, latLng.longitude, 0, data.getInt(Config.DATA_TYPE, Config.UNDEFINED));
        clusterTask = new ClusterTask();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

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

        recyclerCluster = (RecyclerView) findViewById(R.id.recyclerCluster);
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
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.DOWN) {
                    itemList.clear();
                    clusterAdapter.setItemList(itemList);
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
        if (requestCode == Config.REQUEST_FILTER && resultCode == RESULT_OK) {
            if (googleMap != null)
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            else
                finish();
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, AppController.getInstance().getPrefManager().getMapZoom()));

        clusterManager = new ClusterManager<>(this, this.googleMap);
        productRender = new ProductRender(this, this.googleMap, clusterManager);

        clusterManager.setRenderer(productRender);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        this.googleMap.setOnCameraMoveStartedListener(i -> mTimerIsRunning = true);
        this.googleMap.setOnCameraIdleListener(clusterManager);
        this.googleMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    public boolean onClusterClick(Cluster<ProductItem> cluster) {
        try {
            itemList = (List<ProductItem>) cluster.getItems();
            clusterAdapter.setItemList(itemList);
        } catch (Exception e) {
            return false;
        }

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

    private class ProductRender extends DefaultClusterRenderer<ProductItem> implements GoogleMap.OnCameraIdleListener {

        public ProductRender(Context context, GoogleMap map, ClusterManager<ProductItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ProductItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            Bitmap bitmap = Utils.getBitmap(MapViewProductActivity.this, R.drawable.ic_vector_product_item);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        protected void onClusterItemRendered(ProductItem clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ProductItem> cluster, MarkerOptions markerOptions) {
            Log.e(TAG, "onBeforeClusterItemRendered: " + new Gson().toJson(cluster));
            super.onBeforeClusterRendered(cluster, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<ProductItem> cluster) {
            return cluster.getSize() > 1;
        }

        @Override
        public void onCameraIdle() {
            if (MapViewProductActivity.this.googleMap == null) return;
            MapViewProductActivity.this.googleMap.clear();
            if (mTimerIsRunning) {
                clusterTask.cancel(true);
                clusterTask = new ClusterTask();
                clusterTask.execute(MapViewProductActivity.this.googleMap.getCameraPosition().zoom);
                mTimerIsRunning = false;
            }
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
        protected void onPostExecute(Float aFloat) {
            super.onPostExecute(aFloat);
            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            AppController.getInstance().getPrefManager().addLastLatLng(bounds.getCenter(), aFloat);

            if (aFloat > 7)
                fetchProductData(bounds.southwest, bounds.northeast);
        }
    }

    private void fetchProductData(LatLng southwest, LatLng northeast) {
        searchInfo.setStartLat(String.valueOf(southwest.latitude));
        searchInfo.setStartLng(String.valueOf(southwest.longitude));
        searchInfo.setEndLat(String.valueOf(northeast.latitude));
        searchInfo.setEndLng(String.valueOf(northeast.longitude));

        Map<String, String> filterSet = AppController.getInstance().getPrefManager().getFilterSet();
        if (searchInfo.getPropertyID() == null)
            searchInfo.setPropertyID(filterSet.get(Config.KEY_PROPERTY));

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
}