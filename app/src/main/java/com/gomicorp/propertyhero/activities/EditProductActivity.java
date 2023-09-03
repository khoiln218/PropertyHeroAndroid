package com.gomicorp.propertyhero.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.android.volley.VolleyError;
import com.gomicorp.app.Config;
import com.gomicorp.app.GoogleApiHelper;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAddressResultListener;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadDistrictListener;
import com.gomicorp.propertyhero.callbacks.OnLoadInfoListener;
import com.gomicorp.propertyhero.callbacks.OnLoadMarkerListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProductListener;
import com.gomicorp.propertyhero.callbacks.OnLoadPropertyListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.callbacks.OnMultiSelectDialogListener;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Feature;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.Property;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.gomicorp.services.AddressResultReceiver;
import com.gomicorp.ui.ListViewDialog;
import com.gomicorp.ui.MultiSelectListViewDialog;
import com.gomicorp.ui.WorkaroundMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraChangeListener {

    private static final String TAG = EditProductActivity.class.getSimpleName();
    private Product product;

    private List<Property> propertyList;
    private Property property;
    private List<Province> provinceList;
    private List<District> districtList;
    private List<Marker> markerList;
    private Marker building;
    private List<Info> directionList;

    private Timer timer;
    private LatLng latLng;
    private GoogleApiHelper googleApiHelp;
    private GoogleMap googleMap;
    private AddressResultReceiver addressResult;

    private ProgressDialog progressDialog;

    private RelativeLayout progressLayout;
    private LinearLayout buildingLayout;
    private TextView tvSelectProperty, tvProvince, tvDistrict, tvBuilding, tvDirection, tvFeature, tvFurniture, tvTitleLength, tvContentLength;
    private Button btnLocation;
    private Switch swtElevator, swtPets;
    private EditText inputEditAddress, inputEditDeposit, inputEditPrice, inputEditFloor, inputEditFloorCount, inputEditSiteArea, inputEditGFArea, inputEditBeds, inputEditBaths,
            inputEditServiceFee, inputNumberOfPerson, inputTitle, inputContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        long productID = getIntent().getLongExtra(Config.DATA_EXTRA, 0);
        if (productID == 0)
            finish();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_product).replace("...", productID + ""));

        Utils.hideSoftKeyboard(this, findViewById(R.id.editProductLayout));

        progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);

        int height = (int) (Utils.getScreenWidth() / 1.3);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        findViewById(R.id.layoutLocation).setLayoutParams(layoutParams);

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                ((NestedScrollView) findViewById(R.id.scrollEditProduct)).requestDisallowInterceptTouchEvent(true);
            }
        });

        initUI();
        requestProductData(productID);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_update));

        googleApiHelp = new GoogleApiHelper(this);

        propertyList = new ArrayList<>();
        provinceList = new ArrayList<>();
        districtList = new ArrayList<>();
        markerList = new ArrayList<>();
        directionList = new ArrayList<>();

        addressResult = new AddressResultReceiver(new Handler());
        addressResult.listener = new OnAddressResultListener() {
            @Override
            public void onResult(String address) {
                ((TextView) findViewById(R.id.tvLocation)).setText(address);
            }
        };
    }

    private void initUI() {
        buildingLayout = (LinearLayout) findViewById(R.id.buildingLayout);

        tvSelectProperty = (TextView) findViewById(R.id.tvSelectProperty);
        tvProvince = (TextView) findViewById(R.id.tvProvince);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);
        tvBuilding = (TextView) findViewById(R.id.tvBuilding);
        tvDirection = (TextView) findViewById(R.id.tvDirection);
        tvFeature = (TextView) findViewById(R.id.tvFeature);
        tvFurniture = (TextView) findViewById(R.id.tvFurniture);
        tvTitleLength = (TextView) findViewById(R.id.tvTitleLength);
        tvContentLength = (TextView) findViewById(R.id.tvContentLength);

        btnLocation = (Button) findViewById(R.id.btnLocation);
        swtElevator = (Switch) findViewById(R.id.swtElevator);
        swtPets = (Switch) findViewById(R.id.swtPets);

        inputEditAddress = (EditText) findViewById(R.id.inputEditAddress);
        inputEditDeposit = (EditText) findViewById(R.id.inputEditDeposit);
        inputEditPrice = (EditText) findViewById(R.id.inputEditPrice);
        inputEditFloor = (EditText) findViewById(R.id.inputEditFloor);
        inputEditFloorCount = (EditText) findViewById(R.id.inputEditFloorCount);
        inputEditSiteArea = (EditText) findViewById(R.id.inputEditSiteArea);
        inputEditGFArea = (EditText) findViewById(R.id.inputEditGFArea);
        inputEditBeds = (EditText) findViewById(R.id.inputEditBeds);
        inputEditBaths = (EditText) findViewById(R.id.inputEditBaths);
        inputEditServiceFee = (EditText) findViewById(R.id.inputEditServiceFee);
        inputNumberOfPerson = (EditText) findViewById(R.id.inputNumberOfPerson);
        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputContent = (EditText) findViewById(R.id.inputContent);

        btnLocation.setOnClickListener(this);
        tvSelectProperty.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvDistrict.setOnClickListener(this);
        tvBuilding.setOnClickListener(this);
        tvFeature.setOnClickListener(this);
        tvFurniture.setOnClickListener(this);
        findViewById(R.id.btnSelectDirection).setOnClickListener(this);

        inputEditAddress.addTextChangedListener(new InputTextWatcher(inputEditAddress));
        inputTitle.addTextChangedListener(new InputTextWatcher(inputTitle));
        inputContent.addTextChangedListener(new InputTextWatcher(inputContent));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_choose:
                if (submitEditProduct())
                    requestUpdateInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        this.googleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (googleMap != null) {
            LatLngBounds bounds = this.googleMap.getProjection().getVisibleRegion().latLngBounds;
            latLng = bounds.getCenter();

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.startAddressService(EditProductActivity.this, addressResult, latLng);
                }
            }, Config.TIMER_DELAY);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        googleApiHelp.checkLocationSettings();
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLocation:
                if ((latLng = Utils.getLocationFromAddress(inputEditAddress.getText().toString())) != null)
                    updateMap(latLng);
                break;
            case R.id.tvSelectProperty:
                requestPropertyData();
                break;
            case R.id.tvProvince:
                showListViewDialog(Config.PROVINCE_TYPE);
                break;
            case R.id.tvDistrict:
                requestDistrictData(true);
                break;
            case R.id.tvBuilding:
                if (product.getDistrictID() != 0)
                    requestBuildingData();
                else
                    L.showAlert(this, null, getString(R.string.err_msg_district));
                break;
            case R.id.tvFeature:
                showMultiSelectDialog(Config.FEATURE_TYPE, product.getFeatures());
                break;
            case R.id.tvFurniture:
                showMultiSelectDialog(Config.FURNITURE_TYPE, product.getFurnitures());
                break;
            case R.id.btnSelectDirection:
                requestDirectionData();
                break;
            default:
                break;
        }
    }

    //TODO Request Data
    private void requestProductData(long productID) {
        ProductRequest.getProduct(productID, 1, new OnLoadProductListener() {
            @Override
            public void onSuccess(List<Product> products, int totalItems) {
                if (products.size() > 0) {
                    product = products.get(0);

                    requestProvinceData();
                } else
                    L.showToast(getString(R.string.err_request_api));
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestProductData()");
            }
        });
    }

    private void requestPropertyData() {
        if (propertyList.size() > 0) {
            showListViewDialog(Config.PROPERTY_TYPE);
        } else {
            DataRequest.propertyList(new OnLoadPropertyListener() {
                @Override
                public void onSuccess(List<Property> properties) {
                    if (properties.size() > 0) {
                        propertyList = properties;
                        showListViewDialog(Config.PROPERTY_TYPE);
                    } else
                        L.showToast(getString(R.string.err_request_api));
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Error at requestPropertyData()");
                }
            });
        }
    }

    private void requestProvinceData() {
        DataRequest.provinceList(new OnLoadProvinceListener() {
            @Override
            public void onSuccess(List<Province> provinces) {
                if (provinces.size() > 0) {
                    provinceList = provinces;
                    requestDistrictData(false);
                } else
                    L.showToast(getString(R.string.err_request_api));
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestProvinceData()");
            }
        });
    }

    private void requestDistrictData(final boolean showDialog) {
        DataRequest.districtList(product.getProvinceID(), new OnLoadDistrictListener() {
            @Override
            public void onSuccess(List<District> districts) {
                if (districts.size() > 0) {
                    districtList = districts;

                    if (showDialog)
                        showListViewDialog(Config.DISTRICT_TYPE);
                    else
                        setupUI();
                } else
                    L.showToast(getString(R.string.err_request_api));
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestDistrictData()");
            }
        });
    }

    private void requestBuildingData() {
        DataRequest.buildingByDistrict(product.getDistrictID(), new OnLoadMarkerListener() {
            @Override
            public void onSuccess(List<Marker> markers) {
                if (markers.size() > 0) {
                    markerList = markers;
                    showListViewDialog(Config.MARKER_TYPE);
                } else
                    L.showToast(getString(R.string.msg_building_empty));
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestBuildingData()");
            }
        });
    }

    private void requestDirectionData() {
        directionList.clear();
        DataRequest.directionList(new OnLoadInfoListener() {
            @Override
            public void onSuccess(List<Info> infos) {
                directionList.addAll(infos);
                showListViewDialog(Config.DIRECTION_TYPE);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestDirectionData()");
            }
        });
    }

    //TODO Ui
    private void setupUI() {
        tvSelectProperty.setText(product.getPropertyName());
        tvDirection.setText(product.getDirectionName());
        tvTitleLength.setText(Config.TITLE_TEXT.replace("...", product.getTitle().length() + ""));
        tvContentLength.setText(Config.CONTENT_TEXT.replace("...", product.getContent().length() + ""));

        if (product.getBuildingID() > 0)
            showBuildingLayout(new Marker(product.getBuildingID(), product.getBuildingName(), product.getAddresss(), product.getLatitude(), product.getLongitude()));
        else
            showBuildingLayout(null);

        for (Province obj : provinceList) {
            if (obj.getId() == product.getProvinceID())
                tvProvince.setText(obj.getName());
        }

        for (District obj : districtList) {
            if (obj.getId() == product.getDistrictID())
                tvDistrict.setText(obj.getName());
        }

        tvFeature.setText(Utils.featureListToString(product.getFeatures(), true));
        tvFurniture.setText(Utils.featureListToString(product.getFurnitures(), true));

        swtElevator.setChecked(product.getElevator() > 0);
        swtPets.setChecked(product.getPets() > 0);

        inputEditAddress.setText(String.valueOf(product.getAddresss()));
        inputEditDeposit.setText(product.getDeposit() == 0 ? "" : String.valueOf(product.getDeposit()));
        inputEditPrice.setText(product.getPrice() == 0 ? "" : String.valueOf(product.getPrice()));
        inputEditFloor.setText(product.getFloor() == 0 ? "" : String.valueOf(product.getFloor()));
        inputEditFloorCount.setText(product.getFloorCount() == 0 ? "" : String.valueOf(product.getFloorCount()));
        inputEditSiteArea.setText(product.getSiteArea() == 0 ? "" : String.valueOf(product.getSiteArea()));
        inputEditGFArea.setText(product.getGrossFloorArea() == 0 ? "" : String.valueOf(product.getGrossFloorArea()));
        inputEditBeds.setText(product.getBedroom() == 0 ? "" : String.valueOf(product.getBedroom()));
        inputEditBaths.setText(product.getBathroom() == 0 ? "" : String.valueOf(product.getBathroom()));
        inputEditServiceFee.setText(product.getServiceFee() == 0 ? "" : String.valueOf(product.getServiceFee()));
        inputNumberOfPerson.setText(product.getNumPerson() == 0 ? "" : String.valueOf(product.getNumPerson()));
        inputTitle.setText(product.getTitle());
        inputContent.setText(product.getContent());

        progressLayout.setVisibility(View.GONE);
    }

    private void updateMap(LatLng latLng) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.MAP_ZOOM));
        }
    }

    private void showBuildingLayout(Marker building) {
        if (building != null) {
            inputEditAddress.setFocusableInTouchMode(false);
            inputEditAddress.setFocusable(false);
            inputEditFloorCount.setFocusableInTouchMode(false);
            inputEditFloorCount.setFocusable(false);
            btnLocation.setEnabled(false);

            buildingLayout.setVisibility(View.VISIBLE);
            tvBuilding.setText(building.getName());
            inputEditAddress.setText(building.getAddress());
            inputEditFloorCount.setText(building.getFloorCount() + "");
            updateMap(new LatLng(building.getLatitude(), building.getLongitude()));
        } else {
            inputEditAddress.setFocusableInTouchMode(true);
            inputEditAddress.setFocusable(true);
            inputEditFloorCount.setFocusableInTouchMode(true);
            inputEditFloorCount.setFocusable(true);
            btnLocation.setEnabled(true);

            buildingLayout.setVisibility(View.GONE);
            tvBuilding.setText(getString(R.string.text_building));
            inputEditAddress.setText(product.getAddresss());
            inputEditFloorCount.setText(product.getFloorCount() + "");
            updateMap(new LatLng(product.getLatitude(), product.getLongitude()));
        }
    }

    private void showListViewDialog(final int dataType) {
        ArrayList<Parcelable> arrayList = new ArrayList<>();

        switch (dataType) {
            case Config.PROPERTY_TYPE:
                arrayList.addAll(propertyList);
                break;
            case Config.PROVINCE_TYPE:
                arrayList.addAll(provinceList);
                break;
            case Config.DISTRICT_TYPE:
                arrayList.addAll(districtList);
                break;
            case Config.MARKER_TYPE:
                arrayList.addAll(markerList);
                break;
            case Config.DIRECTION_TYPE:
                arrayList.addAll(directionList);
                break;

        }

        if (arrayList.size() == 0)
            return;

        ListViewDialog dialog = ListViewDialog.instance(dataType, arrayList);
        dialog.show(getSupportFragmentManager(), "create_product");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                switch (dataType) {
                    case Config.PROPERTY_TYPE:
                        property = (Property) object;
                        product.setPropertyID(property.getId());
                        tvSelectProperty.setText(property.getName());
                        break;
                    case Config.PROVINCE_TYPE:
                        product.setProvinceID(((Province) object).getId());
                        tvProvince.setText(((Province) object).getName());
                        product.setDistrictID(0);
                        tvDistrict.setText(getString(R.string.text_hint_district));
                        break;
                    case Config.DISTRICT_TYPE:
                        product.setDistrictID(((District) object).getId());
                        tvDistrict.setText(((District) object).getName());
                        break;
                    case Config.MARKER_TYPE:
                        building = (Marker) object;
                        showBuildingLayout(building);
                        break;
                    case Config.DIRECTION_TYPE:
                        product.setDirectionID(((Info) object).getId());
                        tvDirection.setText(((Info) object).getName());
                        break;
                }
            }
        };
    }

    private void showMultiSelectDialog(final int dataType, List<Feature> listSelected) {
        MultiSelectListViewDialog selectDialog = MultiSelectListViewDialog.instance(dataType, (ArrayList<Feature>) listSelected);
        selectDialog.show(getSupportFragmentManager(), "multi_select");
        selectDialog.listener = new OnMultiSelectDialogListener() {
            @Override
            public void onSelected(List<Feature> features) {
                if (dataType == Config.FEATURE_TYPE) {
                    product.setFeatures(features);
                    tvFeature.setText(Utils.featureListToString(features, true));
                } else if (dataType == Config.FURNITURE_TYPE) {
                    product.setFurnitures(features);
                    tvFurniture.setText(Utils.featureListToString(features, true));
                }
            }
        };
    }

    private boolean submitEditProduct() {
        if (product.getDistrictID() == 0) {
            L.showAlert(this, null, getString(R.string.text_err_district));
            return false;
        }

        if (Utils.isNullOrEmpty(inputEditAddress.getText().toString())) {
            L.showAlert(this, null, getString(R.string.text_err_address));
            InputValidation.requestFocus(this, inputEditAddress);
            return false;
        }

        if (latLng == null) {
            L.showAlert(this, null, getString(R.string.text_err_location));
            return false;
        }

        if (Utils.toNumber(inputEditPrice.getText().toString().trim()) == 0) {
            L.showAlert(this, null, getString(R.string.text_err_price));
            InputValidation.requestFocus(this, inputEditPrice);
            return false;
        }

        if (Utils.toNumber(inputEditGFArea.getText().toString().trim()) == 0) {
            L.showAlert(this, null, getString(R.string.text_err_gross_floor_area));
            InputValidation.requestFocus(this, inputEditGFArea);
            return false;
        }

        String title = inputTitle.getText().toString();
        if (title.length() < Config.MIN_TITLE || title.length() > Config.MAX_TITLE) {
            L.showAlert(this, null, getString(R.string.text_err_title_product));
            InputValidation.requestFocus(this, inputTitle);
            return false;
        }

        String content = inputContent.getText().toString();
        if (content.length() < Config.MIN_CONTENT || content.length() > Config.MAX_CONTENT) {
            L.showAlert(this, null, getString(R.string.text_err_content_product));
            InputValidation.requestFocus(this, inputContent);
            return false;
        }

        return true;
    }

    protected void requestUpdateInfo() {
        progressDialog.show();

        product.setAddresss(inputEditAddress.getText().toString());
        product.setBuildingID(building != null ? building.getId() : 0);
        product.setLatitude(building != null ? building.getLatitude() : latLng.latitude);
        product.setLongitude(building != null ? building.getLongitude() : latLng.longitude);
        product.setDeposit(Utils.toNumber(inputEditDeposit.getText().toString()));
        product.setPrice(Utils.toNumber(inputEditPrice.getText().toString()));
        product.setFloor((int) Utils.toNumber(inputEditFloor.getText().toString()));
        product.setFloorCount((int) Utils.toNumber(inputEditFloorCount.getText().toString()));
        product.setGrossFloorArea(Utils.toNumber(inputEditGFArea.getText().toString()));
        product.setBedroom((int) Utils.toNumber(inputEditBeds.getText().toString()));
        product.setBathroom((int) Utils.toNumber(inputEditBaths.getText().toString()));
        product.setServiceFee(Utils.toNumber(inputEditServiceFee.getText().toString()));
        product.setFeatureList(Utils.featureListToString(product.getFeatures(), false));
        product.setFurnitureList(Utils.featureListToString(product.getFurnitures(), false));
        product.setElevator((byte) (swtElevator.isChecked() ? 1 : 0));
        product.setPets((byte) (swtPets.isChecked() ? 1 : 0));
        product.setTitle(inputTitle.getText().toString());
        product.setContent(inputContent.getText().toString());

        ProductRequest.updateInfo(product, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess()) {
                    L.showToast(getString(R.string.text_update_success));
                    finish();
                } else
                    L.showToast(getString(R.string.err_request_api));
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestUpdateInfo()");
            }
        });
    }

    private class InputTextWatcher implements TextWatcher {

        private View view;

        public InputTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.inputEditAddress:
                    if (product.getBuildingID() == 0 || building == null) {
                        if (((TextView) view).length() > 0)
                            btnLocation.setEnabled(true);
                        else
                            btnLocation.setEnabled(false);
                    }
                    break;
                case R.id.inputTitle:
                    int title = ((TextView) view).length();
                    tvTitleLength.setText(Config.TITLE_TEXT.replace("...", title + ""));
                    break;
                case R.id.inputContent:
                    int content = ((TextView) view).length();
                    tvContentLength.setText(Config.CONTENT_TEXT.replace("...", content + ""));
                    break;
            }
        }
    }
}
