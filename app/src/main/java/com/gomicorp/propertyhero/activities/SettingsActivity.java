package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.ui.ListViewDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private List<Province> provinceList = new ArrayList<>();

    private TextView tvProvince, tvVersion, tvCopyright;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvProvince = (TextView) findViewById(R.id.tvProvince);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvCopyright = (TextView) findViewById(R.id.tvCopyright);

        tvVersion.setText(getString(R.string.text_version) + Utils.getVersionName());
        tvCopyright.setText(getString(R.string.text_copyright).replace("...", Calendar.getInstance().get(Calendar.YEAR) + ""));

        progressDialog = L.progressDialog(this, null, getString(R.string.text_loading));
        fetchProvinceData();


        findViewById(R.id.btnProvince).setOnClickListener(this);
        findViewById(R.id.btnAbout).setOnClickListener(this);

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
            case R.id.btnProvince:
                if (provinceList.size() > 0)
                    showListViewDialog();
                break;
            case R.id.btnAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
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
                    if (obj.getId() == AppController.getInstance().getPrefManager().getDefaultProvince())
                        tvProvince.setText(obj.getName());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProvinceData()");
                L.showToast(getString(R.string.request_time_out));
                progressDialog.dismiss();
            }
        });
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.PROVINCE_TYPE, new ArrayList<Parcelable>(provinceList));
        dialog.show(getSupportFragmentManager(), "update_info");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                AppController.getInstance().getPrefManager().addDefaultProvince(((Province) object).getId());
                tvProvince.setText(((Province) object).getName());
            }
        };
    }

}
