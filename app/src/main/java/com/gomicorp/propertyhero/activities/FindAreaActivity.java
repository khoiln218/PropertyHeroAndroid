package com.gomicorp.propertyhero.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FindAreaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FindAreaActivity.class.getSimpleName();

    private EditText inputKeyword;
    private TextView tvSelectProvince;
    private RecyclerView recyclerFindArea;

    private List<Province> provinceList;
    private Province province;
    private List<Marker> markerList;
    private FindMarkerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_area);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.findAreaLayout));

        inputKeyword = (EditText) findViewById(R.id.inputKeyword);
        tvSelectProvince = (TextView) findViewById(R.id.tvSelectProvince);
        recyclerFindArea = (RecyclerView) findViewById(R.id.recyclerFindArea);

        tvSelectProvince.setOnClickListener(this);
        inputKeyword.addTextChangedListener(new InputTextWatcher(inputKeyword));
        inputKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    findAreaByKeyword();
                    return true;
                }
                return false;
            }
        });

        fetchProvinceData();

        markerList = new ArrayList<>();
        adapter = new FindMarkerAdapter(markerList, Config.MARKER_ATTR);
        recyclerFindArea.setLayoutManager(new LinearLayoutManager(this));
        recyclerFindArea.setItemAnimator(new DefaultItemAnimator());
        recyclerFindArea.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerFindArea.setAdapter(adapter);
        recyclerFindArea.addOnItemTouchListener(new RecyclerTouchListner(this, recyclerFindArea, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                L.showSelectDistance(FindAreaActivity.this, markerList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.PROVINCE_TYPE, new ArrayList<Parcelable>(provinceList));
        dialog.show(getSupportFragmentManager(), "find_area");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                province = (Province) object;
                tvSelectProvince.setText(province.getName());
                findAreaByKeyword();
            }
        };
    }

    private void findAreaByKeyword() {
        markerList.clear();
        markerList.add(null);
        adapter.setMarkerList(markerList);

        DataRequest.findByKeyword(inputKeyword.getText().toString(), province.getId(), Config.MARKER_ATTR, new OnLoadMarkerListener() {
            @Override
            public void onSuccess(List<Marker> markers) {
                markerList.clear();
                markerList.addAll(markers);
                adapter.setMarkerList(markerList);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at findAreaByKeyword()");
                L.showToast(getString(R.string.request_time_out));
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!inputKeyword.getText().toString().trim().isEmpty())
                                    findAreaByKeyword();
                            }
                        });
                    }
                }, Config.TIMER_DELAY);
            }
        }
    }
}
