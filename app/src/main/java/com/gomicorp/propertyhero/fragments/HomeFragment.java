package com.gomicorp.propertyhero.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.app.GoogleApiHelper;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.CreateProductActivity;
import com.gomicorp.propertyhero.activities.FindAreaActivity;
import com.gomicorp.propertyhero.activities.LoginActivity;
import com.gomicorp.propertyhero.adapters.AreaNearbyAdapter;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.model.Advertising;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.ui.ChildAnimation;
import com.gomicorp.ui.ExpandableHeightGridView;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GoogleApiHelper googleApi;
    private LocationListener listener;
    private LatLng latLng;

    private List<Advertising> advList;
    private SliderLayout imageSlider;

    private List<Marker> areaList;
    private AreaNearbyAdapter areaNearbyAdapter;
    private ExpandableHeightGridView grvAttractionNearby;

    private List<Marker> universityList;
    private AreaNearbyAdapter universityAdapter;
    private ExpandableHeightGridView grvUniversity;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        imageSlider = (SliderLayout) root.findViewById(R.id.imageSlider);
        grvAttractionNearby = (ExpandableHeightGridView) root.findViewById(R.id.grvAttractionNearby);
        grvUniversity = (ExpandableHeightGridView) root.findViewById(R.id.grvUniversity);

        imageSlider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getScreenWidth() / 2));

        setupAttractionNearby();
        setupUniversity();

        root.findViewById(R.id.btnViewAll).setOnClickListener(this);
        root.findViewById(R.id.btnApartment).setOnClickListener(this);
        root.findViewById(R.id.btnRoom).setOnClickListener(this);
        root.findViewById(R.id.btnFindArea).setOnClickListener(this);

        root.findViewById(R.id.fabAddInfo).setVisibility(Config.DISABLE_CREATE ? View.GONE : View.VISIBLE);
        root.findViewById(R.id.fabAddInfo).setOnClickListener(this);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latLng = AppController.getInstance().getPrefManager().getLastLatLng();
        googleApi = new GoogleApiHelper(requireActivity());
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (latLng != null && location.getLatitude() == latLng.latitude && location.getLongitude() == latLng.longitude)
                    return;

                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                fetchAttractionNearby();
            }
        };

        fetchImageSliderData();
        fetchAttractionNearby();
        fetchUniversity();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApi.registerListener(listener);
        googleApi.checkLocationSettings();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApi.removeListener(listener);
        imageSlider.stopAutoCycle();
        AppController.getInstance().cancelPedingRequesrs(TAG);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewAll:
                Utils.launchMapView(getActivity(), getString(R.string.text_view_all), latLng, Config.UNDEFINED);
                break;
            case R.id.btnApartment:
                Utils.launchMapView(getActivity(), getString(R.string.text_apartment), latLng, Config.PROPERTY_APARTMENT);
                break;
            case R.id.btnRoom:
                Utils.launchMapView(getActivity(), getString(R.string.text_room), latLng, Config.PROPERTY_ROOM);
                break;
            case R.id.btnFindArea:
                startActivity(new Intent(getActivity(), FindAreaActivity.class));
                break;
            case R.id.fabAddInfo:
                if (AppController.getInstance().getPrefManager().getUserID() == 0) {
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.STRING_DATA, CreateProductActivity.class.getSimpleName());
                    intentLogin.putExtra(Config.DATA_EXTRA, bundle);
                    startActivity(intentLogin);
                } else if (AppController.getInstance().getPrefManager().getPhoneNumber() == null) {
                    L.launchUpdatePhone(getActivity(), false);
                } else {
                    startActivity(new Intent(getActivity(), CreateProductActivity.class));
                }
                break;
        }
    }

    private void fetchImageSliderData() {
        advList = new ArrayList<>();
        JsonObjectRequest reqAdv = new JsonObjectRequest(Request.Method.GET, EndPoints.URL_LIST_ADV_MAIN, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                advList.addAll(Parser.advList(response));
                addImageSlider();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at fetchImageSliderData()");
            }
        });

        AppController.getInstance().addToRequestQueue(reqAdv, TAG);
    }

    private void addImageSlider() {
        imageSlider.removeAllSliders();
        for (Advertising adv : advList) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .description(null)
                    .image(adv.getThumbnail())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);

            imageSlider.addSlider(textSliderView);
        }

        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new ChildAnimation());
        imageSlider.setDuration(4000);
    }

    private void setupAttractionNearby() {
        areaList = new ArrayList<>();
        areaNearbyAdapter = new AreaNearbyAdapter(getActivity(), areaList);
        grvAttractionNearby.setExpanded(false);
        grvAttractionNearby.setAdapter(areaNearbyAdapter);
        grvAttractionNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.showSelectDistance(getActivity(), areaList.get(position));
            }
        });
    }

    private void setupUniversity() {
        universityList = new ArrayList<>();
        universityAdapter = new AreaNearbyAdapter(getActivity(), universityList);
        grvUniversity.setExpanded(false);
        grvUniversity.setAdapter(universityAdapter);
        grvUniversity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.showSelectDistance(getActivity(), universityList.get(position));
            }
        });
    }

    private void fetchAttractionNearby() {
        String url = EndPoints.URL_ATTRACTION_BY_LOCATION
                .replace(UrlParams.LAT, String.valueOf(latLng.latitude))
                .replace(UrlParams.LNG, String.valueOf(latLng.longitude))
                .replace(UrlParams.NUM_ITEMS, "3")
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqAttractionNearby = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (Config.DEBUG)
                    Log.e("fetchAttractionNearby", "onResponse: " + new Gson().toJson(response));
                areaList = Parser.markerList(response);
                areaNearbyAdapter.setAttractionList(areaList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at fetchAttractionNearby()");
            }
        });

        AppController.getInstance().addToRequestQueue(reqAttractionNearby, TAG);
    }

    private void fetchUniversity() {
        String url = EndPoints.URL_UNIVERSITY_KOREA
                .replace(UrlParams.NUM_ITEMS, "3")
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqUniversity = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                universityList = Parser.markerList(response);
                universityAdapter.setAttractionList(universityList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at fetchUniversity()");
            }
        });

        AppController.getInstance().addToRequestQueue(reqUniversity, TAG);
    }
}
