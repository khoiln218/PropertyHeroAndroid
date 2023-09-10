package com.gomicorp.propertyhero.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.FindMarkerAdapter;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadMarkerListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.ui.DividerItemDecoration;
import com.gomicorp.ui.ListViewDialog;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchMarkerFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchMarkerFragment.class.getSimpleName();

    private LinearLayout findAreaLayout;
    private EditText inputKeyword;
    private TextView tvSelectProvince;
    private RecyclerView recyclerFindArea;

    private List<Province> provinceList;
    private Province province;
    private List<Marker> markerList;
    private FindMarkerAdapter adapter;

    public SearchMarkerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search_marker, container, false);

        findAreaLayout = (LinearLayout) root.findViewById(R.id.findAreaLayout);
        inputKeyword = (EditText) root.findViewById(R.id.inputKeyword);
        tvSelectProvince = (TextView) root.findViewById(R.id.tvSelectProvince);
        recyclerFindArea = (RecyclerView) root.findViewById(R.id.recyclerFindArea);

        tvSelectProvince.setOnClickListener(this);
        inputKeyword.addTextChangedListener(new InputTextWatcher(inputKeyword));
        inputKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    searchByKeyword();
                    return true;
                }

                return false;
            }
        });

        fetchProvinceData();

        markerList = new ArrayList<>();
        adapter = new FindMarkerAdapter(markerList, Config.MARKER_BUILDING);
        recyclerFindArea.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerFindArea.setItemAnimator(new DefaultItemAnimator());
        recyclerFindArea.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
        recyclerFindArea.setAdapter(adapter);
        recyclerFindArea.addOnItemTouchListener(new RecyclerTouchListner(getContext(), recyclerFindArea, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                Marker marker = markerList.get(position);
                Utils.launchMapView(getActivity(), marker.getName(), new LatLng(marker.getLatitude(), marker.getLongitude()), Config.UNDEFINED);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.hideSoftKeyboard(getActivity(), findAreaLayout);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvSelectProvince) {
            if (provinceList != null)
                showListViewDialog();
        }
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.PROVINCE_TYPE, new ArrayList<Parcelable>(provinceList));
        dialog.show(getChildFragmentManager(), "find_area");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                province = (Province) object;
                tvSelectProvince.setText(province.getName());
                searchByKeyword();
            }
        };
    }

    private void searchByKeyword() {
        markerList.clear();
        markerList.add(null);
        adapter.setMarkerList(markerList);

        DataRequest.findByKeyword(inputKeyword.getText().toString(), province.getId(), Config.UNDEFINED, new OnLoadMarkerListener() {
            @Override
            public void onSuccess(List<Marker> markers) {
                markerList.clear();
                markerList.addAll(markers);
                adapter.setMarkerList(markerList);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at findAreaByKeyword()");
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
                        province = obj;
                        tvSelectProvince.setText(province.getName());
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProvinceData");
            }
        });
    }

    protected class InputTextWatcher implements TextWatcher {

        private View view;
        private Timer timer;

        public InputTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (timer != null)
                timer.cancel();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (view.getId() == R.id.inputKeyword) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null)
                            getActivity().runOnUiThread(() -> {
                                if (!inputKeyword.getText().toString().trim().isEmpty())
                                    searchByKeyword();
                            });
                    }
                }, Config.TIMER_DELAY);
            }
        }
    }
}
