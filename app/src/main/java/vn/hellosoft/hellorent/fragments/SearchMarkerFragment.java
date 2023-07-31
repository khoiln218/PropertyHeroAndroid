package vn.hellosoft.hellorent.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.adapters.FindMarkerAdapter;
import vn.hellosoft.hellorent.callbacks.OnListViewDialogListener;
import vn.hellosoft.hellorent.callbacks.OnLoadMarkerListener;
import vn.hellosoft.hellorent.callbacks.OnLoadProvinceListener;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.hellorent.json.DataRequest;
import vn.hellosoft.hellorent.model.Marker;
import vn.hellosoft.hellorent.model.Province;
import vn.hellosoft.helper.Utils;
import vn.hellosoft.ui.DividerItemDecoration;
import vn.hellosoft.ui.ListViewDialog;

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
        recyclerFindArea.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
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
        switch (v.getId()) {
            case R.id.tvSelectProvince:
                if (provinceList != null)
                    showListViewDialog();
                break;
        }
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.PROVINCE_TYPE, new ArrayList<Parcelable>(provinceList));
        dialog.show(getFragmentManager(), "find_area");
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!inputKeyword.getText().toString().trim().isEmpty())
                                    searchByKeyword();
                            }
                        });
                    }
                }, Config.TIMER_DELAY);
            }
        }
    }
}
