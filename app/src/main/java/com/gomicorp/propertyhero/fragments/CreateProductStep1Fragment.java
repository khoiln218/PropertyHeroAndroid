package com.gomicorp.propertyhero.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.app.GoogleApiHelper;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.CreateProductActivity;
import com.gomicorp.propertyhero.callbacks.OnAddressResultListener;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadDistrictListener;
import com.gomicorp.propertyhero.callbacks.OnLoadMarkerListener;
import com.gomicorp.propertyhero.callbacks.OnLoadPropertyListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.Property;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.services.AddressResultReceiver;
import com.gomicorp.ui.ListViewDialog;
import com.gomicorp.ui.WorkaroundMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProductStep1Fragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraChangeListener {

    private static final String TAG = CreateProductStep1Fragment.class.getSimpleName();
    private View root;

    private Timer timer;
    private WorkaroundMapFragment mapFragment;
    private GoogleMap googleMap;
    private GoogleApiHelper googleApiHelper;
    private LatLng latLng;
    private AddressResultReceiver addressResult;

    private Product productInfo;

    private List<Property> propertyList;
    private Property property;
    private List<Province> provinceList;
    private List<District> districtList;
    private List<Marker> buildingList;
    private Marker building;

    private ProgressDialog progressDialog;

    private NestedScrollView scrollView;
    private LinearLayout buildingLayout;
    private TextView tvSelectProperty, tvProvinceCreateProduct, tvDistrictCreateProduct, tvBuilding;
    private EditText inputProductAddress;
    private Button btnLocation, btnNextStep2;

    public CreateProductStep1Fragment() {
        // Required empty public constructor
    }

    public static CreateProductStep1Fragment instance(Product product) {
        CreateProductStep1Fragment step1 = new CreateProductStep1Fragment();
        Bundle args = new Bundle();
        args.putParcelable(Config.PARCELABLE_DATA, product);
        step1.setArguments(args);
        return step1;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            productInfo = getArguments().getParcelable(Config.PARCELABLE_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null)
                parent.removeView(root);
        }
        try {
            root = inflater.inflate(R.layout.fragment_create_product_step_1, container, false);
        } catch (InflateException e) {
        }


        Utils.hideSoftKeyboard(getActivity(), root.findViewById(R.id.layoutStep1));
        int height = (int) (Utils.getScreenWidth() / 1.5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        root.findViewById(R.id.layoutLocation).setLayoutParams(layoutParams);

        scrollView = (NestedScrollView) root.findViewById(R.id.scrollStep1);
        buildingLayout = (LinearLayout) root.findViewById(R.id.buildingLayout);

        tvSelectProperty = (TextView) root.findViewById(R.id.tvSelectProperty);
        tvProvinceCreateProduct = (TextView) root.findViewById(R.id.tvProvinceCreateProduct);
        tvDistrictCreateProduct = (TextView) root.findViewById(R.id.tvDistrictCreateProduct);
        tvBuilding = (TextView) root.findViewById(R.id.tvBuilding);

        inputProductAddress = (EditText) root.findViewById(R.id.inputProductAddress);
        btnLocation = (Button) root.findViewById(R.id.btnLocation);
        btnNextStep2 = (Button) root.findViewById(R.id.btnNextStep2);

        tvSelectProperty.setOnClickListener(this);
        tvProvinceCreateProduct.setOnClickListener(this);
        tvDistrictCreateProduct.setOnClickListener(this);
        tvBuilding.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnNextStep2.setOnClickListener(this);

        progressDialog = L.progressDialog(getActivity(), null, getString(R.string.text_loading));

        addressResult = new AddressResultReceiver(new Handler());
        addressResult.listener = new OnAddressResultListener() {
            @Override
            public void onResult(String address) {
                ((TextView) root.findViewById(R.id.tvLocation)).setText(address);
            }
        };

        inputProductAddress.addTextChangedListener(new InputTextWatcher(inputProductAddress));
        inputProductAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    if ((latLng = Utils.getLocationFromAddress(inputProductAddress.getText().toString())) != null)
                        updateMap(latLng);
                }
                return false;
            }
        });

        mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.mapCreateProduct);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiHelper = new GoogleApiHelper(getActivity());
        if (propertyList == null || propertyList.size() == 0) {
            progressDialog.show();
            fetchPropertyData();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Utils.startAddressService(getActivity(), addressResult, latLng);
                }
            }, Config.TIMER_DELAY);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        googleApiHelper.checkLocationSettings();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelectProperty:
                if (propertyList != null)
                    showListViewDialog(Config.PROPERTY_TYPE);
                break;
            case R.id.tvProvinceCreateProduct:
                if (provinceList != null)
                    showListViewDialog(Config.PROVINCE_TYPE);
                break;
            case R.id.tvDistrictCreateProduct:
                fetchDistrictData();
                break;
            case R.id.tvBuilding:
                if (productInfo.getDistrictID() != 0)
                    fetchBuildingList();
                else
                    L.showAlert(getActivity(), null, getString(R.string.err_msg_district));
                break;
            case R.id.btnLocation:
                if ((latLng = Utils.getLocationFromAddress(inputProductAddress.getText().toString())) != null)
                    updateMap(latLng);
                break;
            case R.id.btnNextStep2:
                handleNextStep();
                break;
        }
    }

    private void fetchPropertyData() {
        DataRequest.propertyList(new OnLoadPropertyListener() {
            @Override
            public void onSuccess(List<Property> properties) {
                propertyList = properties;
                property = propertyList.get(0);

                tvSelectProperty.setText(property.getName());

                fetchProvinceData();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchCategoryList()");
            }
        });
    }

    private void fetchProvinceData() {
        DataRequest.provinceList(new OnLoadProvinceListener() {
            @Override
            public void onSuccess(List<Province> provinces) {
                provinceList = provinces;
                for (Province obj : provinces) {
                    if (obj.getId() == AppController.getInstance().getPrefManager().getDefaultProvince()) {
                        productInfo.setProvinceID(obj.getId());
                        tvProvinceCreateProduct.setText(obj.getName());
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProvinceList()");
            }
        });
    }

    private void fetchDistrictData() {
        progressDialog.show();
        DataRequest.districtList(productInfo.getProvinceID(), new OnLoadDistrictListener() {
            @Override
            public void onSuccess(List<District> districts) {
                districtList = districts;
                showListViewDialog(Config.DISTRICT_TYPE);
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchCategoryList()");
            }
        });
    }

    private void fetchBuildingList() {
        DataRequest.buildingByDistrict(productInfo.getDistrictID(), new OnLoadMarkerListener() {
            @Override
            public void onSuccess(List<Marker> markers) {
                if (markers.size() > 0) {
                    buildingList = markers;
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
                arrayList.addAll(buildingList);
                break;
            default:
                break;
        }

        if (arrayList.size() == 0)
            return;

        ListViewDialog dialog = ListViewDialog.instance(dataType, arrayList);
        dialog.show(getFragmentManager(), "create_product");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                switch (dataType) {
                    case Config.PROPERTY_TYPE:
                        property = (Property) object;
                        tvSelectProperty.setText(property.getName());
                        updateBuildingLayout(null);
                        break;
                    case Config.PROVINCE_TYPE:
                        productInfo.setProvinceID(((Province) object).getId());
                        tvProvinceCreateProduct.setText(((Province) object).getName());

                        tvDistrictCreateProduct.setText(getString(R.string.text_district));
                        productInfo.setDistrictID(0);
                        break;
                    case Config.DISTRICT_TYPE:
                        tvDistrictCreateProduct.setText(((District) object).getName());
                        productInfo.setDistrictID(((District) object).getId());
                        break;
                    case Config.MARKER_TYPE:
                        building = (Marker) object;
                        updateBuildingLayout(building);
                        break;
                }
            }
        };
    }

    private void updateBuildingLayout(Marker building) {
        if (building != null) {
            inputProductAddress.setFocusableInTouchMode(false);
            inputProductAddress.setFocusable(false);
            btnLocation.setEnabled(false);

            buildingLayout.setVisibility(View.VISIBLE);
            tvBuilding.setText(building.getName());
            inputProductAddress.setText(building.getAddress());

            updateMap(new LatLng(building.getLatitude(), building.getLongitude()));
        } else {
            inputProductAddress.setFocusableInTouchMode(true);
            inputProductAddress.setFocusable(true);
            btnLocation.setEnabled(true);

            buildingLayout.setVisibility(View.GONE);
            tvBuilding.setText(getString(R.string.text_building));
            inputProductAddress.setText("");

            updateMap(new LatLng(0, 0));
        }
    }

    private void updateMap(LatLng latLng) {
        if (googleMap != null) {
            MapsInitializer.initialize(this.getActivity());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.MAP_ZOOM));
        }
    }

    private void handleNextStep() {
        if (property == null) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_property));
            return;
        }

        if (productInfo.getProvinceID() == 0) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_province));
            return;
        }

        if (productInfo.getDistrictID() == 0) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_district));
            return;
        }

        String address = inputProductAddress.getText().toString();
        if (Utils.isNullOrEmpty(address)) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_address));
            InputValidation.requestFocus(getActivity(), inputProductAddress);
            return;
        }


        if (latLng == null || (latLng.latitude == 0 && latLng.longitude == 0)) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_location));
            return;
        }

        productInfo.setPropertyID(property.getId());
        productInfo.setAddresss(address);
        productInfo.setBuildingID(building != null ? building.getId() : 0);
        productInfo.setLatitude(building != null ? building.getLatitude() : latLng.latitude);
        productInfo.setLongitude(building != null ? building.getLongitude() : latLng.longitude);
        productInfo.setFloorCount(building != null ? building.getFloorCount() : 0);

        CreateProductActivity parent = (CreateProductActivity) getActivity();
        parent.productInfo = this.productInfo;
        parent.nextStep();
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
            if (view.getId() == R.id.inputProductAddress) {
                if (building == null) {
                    if (((TextView) view).length() > 0)
                        btnLocation.setEnabled(true);
                    else
                        btnLocation.setEnabled(false);
                }
            }
        }
    }
}
