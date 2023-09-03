package com.gomicorp.propertyhero.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadDistrictListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.ui.ListViewDialog;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLocationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchLocationFragment.class.getSimpleName();

    private RelativeLayout btnSelectProvince, btnSelectDistrict;
    private TextView tvSelectProvince, tvSelectDistrict;
    private Button btnLocationSearch;

    private List<Province> provinceList;
    private Province province;

    private List<District> districtList;
    private District district;

    private ProgressDialog progressDialog;

    public SearchLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search_location, container, false);

        btnSelectProvince = (RelativeLayout) root.findViewById(R.id.btnSelectProvince);
        tvSelectProvince = (TextView) root.findViewById(R.id.tvSelectProvince);

        btnSelectDistrict = (RelativeLayout) root.findViewById(R.id.btnSelectDistrict);
        tvSelectDistrict = (TextView) root.findViewById(R.id.tvSelectDistrict);

        btnLocationSearch = (Button) root.findViewById(R.id.btnLocationSearch);

        btnSelectProvince.setOnClickListener(this);
        btnSelectDistrict.setOnClickListener(this);
        btnLocationSearch.setOnClickListener(this);

        progressDialog = L.progressDialog(getActivity(), null, getString(R.string.text_loading));
        fetchProvinceData();

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectProvince:
                if (provinceList != null)
                    showLocationDialog(Config.PROVINCE_TYPE);
                break;
            case R.id.btnSelectDistrict:
                fetchDistrictData();
                break;
            case R.id.btnLocationSearch:
                Utils.launchMapView(getActivity(), district.getName() + ", " + province.getName(), new LatLng(district.getLatitude(), district.getLongitude()), Config.UNDEFINED);
                break;
        }
    }

    private void fetchProvinceData() {
        progressDialog.show();
        DataRequest.provinceList(new OnLoadProvinceListener() {
            @Override
            public void onSuccess(List<Province> provinces) {
                provinceList = provinces;
                for (Province obj : provinces) {
                    if (obj.getId() == AppController.getInstance().getPrefManager().getDefaultProvince()) {
                        province = obj;
                        tvSelectProvince.setText(province.getName());
                    }
                }
                progressDialog.dismiss();
                updateUI();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProvinceData()");
            }
        });
    }

    private void fetchDistrictData() {
        progressDialog.show();
        if (districtList != null)
            districtList.clear();

        DataRequest.districtList(province.getId(), new OnLoadDistrictListener() {
            @Override
            public void onSuccess(List<District> districts) {
                districtList = districts;
                progressDialog.dismiss();
                showLocationDialog(Config.DISTRICT_TYPE);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchDistrictData()");
            }
        });
    }

    private void showLocationDialog(final int dataType) {

        ArrayList<Parcelable> parcelables = new ArrayList<>();
        if (dataType == Config.PROVINCE_TYPE)
            parcelables.addAll(provinceList);
        else if (dataType == Config.DISTRICT_TYPE)
            parcelables.addAll(districtList);

        ListViewDialog dialog = ListViewDialog.instance(dataType, parcelables);
        dialog.show(getChildFragmentManager(), "search_location");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                if (dataType == Config.PROVINCE_TYPE) {
                    province = (Province) object;
                    district = null;
                } else if (dataType == Config.DISTRICT_TYPE)
                    district = (District) object;

                updateUI();
            }
        };
    }

    private void updateUI() {
        if (province != null)
            tvSelectProvince.setText(province.getName());

        tvSelectDistrict.setText(district != null ? district.getName() : getString(R.string.text_district));

        if (district != null && province != null)
            btnLocationSearch.setEnabled(true);
    }
}
