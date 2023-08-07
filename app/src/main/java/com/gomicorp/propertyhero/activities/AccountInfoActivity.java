package com.gomicorp.propertyhero.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadDistrictListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.model.Account;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.gomicorp.ui.ListViewDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AccountInfoActivity.class.getSimpleName();

    private EditText inputFullName, inputPhoneNumber, inputAddress;
    private TextView tvBirthDate, tvProvince, tvDistrict;
    private Spinner spnGender;

    private Account account;

    private Date birthDate;
    private List<Province> provinceList;
    private Province province;
    private List<District> districtList;
    private District district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.layoutUpdateInfo));

        inputFullName = (EditText) findViewById(R.id.inputFullName);
        inputPhoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);
        inputAddress = (EditText) findViewById(R.id.inputAddress);

        tvBirthDate = (TextView) findViewById(R.id.tvBirthDate);
        tvProvince = (TextView) findViewById(R.id.tvProvince);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);

        spnGender = (Spinner) findViewById(R.id.spnGender);

        setupSpinnerGender();
        fetchAccountInfo();

        findViewById(R.id.btnBirthDate).setOnClickListener(this);
        findViewById(R.id.btnProvince).setOnClickListener(this);
        findViewById(R.id.btnDistrict).setOnClickListener(this);
        findViewById(R.id.btnSubmitUpdateInfo).setOnClickListener(this);
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
            case R.id.btnBirthDate:
                showDatePicker();
                break;
            case R.id.btnProvince:
                if (provinceList != null)
                    showListViewDialog(Config.PROVINCE_TYPE);
                break;
            case R.id.btnDistrict:
                fetchDistrictData(province.getId(), true);
                break;
            case R.id.btnSubmitUpdateInfo:
                handleUpdateInfo();
                break;
        }
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

    private void fetchAccountInfo() {
        AccountRequest.getDetails(AppController.getInstance().getPrefManager().getUserID(), new OnAccountRequestListener() {
            @Override
            public void onSuccess(List<Account> accounts) {
                if (accounts.size() > 0) {
                    account = accounts.get(0);
                    fetchProvinceData();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchAccountInfo()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void fetchProvinceData() {
        DataRequest.provinceList(new OnLoadProvinceListener() {
            @Override
            public void onSuccess(List<Province> provinces) {
                provinceList = provinces;
                fetchDistrictData(account.getProvinceID(), false);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProvinceData()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void fetchDistrictData(int provinceID, final boolean isDialog) {
        DataRequest.districtList(provinceID, new OnLoadDistrictListener() {
            @Override
            public void onSuccess(List<District> districts) {
                districtList = districts;
                if (isDialog)
                    showListViewDialog(Config.DISTRICT_TYPE);
                else
                    updateUI();
            }

            @Override
            public void onError(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
                Log.e(TAG, "Error at fetchProvinceData()");
            }
        });
    }

    private void setupSpinnerGender() {
        CharSequence[] array = getResources().getStringArray(R.array.gender);
        String[] genderList = new String[array.length];
        for (int i = 0; i < array.length; i++)
            genderList[i] = array[i].toString();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnGender.setAdapter(adapter);
    }

    private void updateUI() {
        for (Province obj : provinceList)
            if (obj.getId() == account.getProvinceID())
                province = obj;

        for (District obj : districtList)
            if (obj.getId() == account.getDistrictID())
                district = obj;

        if (province == null || district == null)
            finish();

        inputFullName.setText(account.getFullName());
        inputPhoneNumber.setText(account.getPhoneNumber());
        inputAddress.setText(account.getAddress());
        spnGender.setSelection(account.getGender());
        tvBirthDate.setText(Utils.dateToString(account.getBirthDate()));

        tvProvince.setText(province.getName());
        tvDistrict.setText(district.getName());

        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(account.getBirthDate());
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthDate = newDate.getTime();

                tvBirthDate.setText(Utils.dateToString(birthDate));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showListViewDialog(final int dataType) {
        ArrayList<Parcelable> arrayList = new ArrayList<>();
        if (dataType == Config.PROVINCE_TYPE)
            arrayList.addAll(provinceList);
        else if (dataType == Config.DISTRICT_TYPE)
            arrayList.addAll(districtList);

        ListViewDialog dialog = ListViewDialog.instance(dataType, arrayList);
        dialog.show(getSupportFragmentManager(), "update_info");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                switch (dataType) {
                    case Config.PROVINCE_TYPE:
                        province = (Province) object;
                        tvProvince.setText(province.getName());
                        district = null;
                        break;
                    case Config.DISTRICT_TYPE:
                        district = (District) object;
                        break;
                }

                tvDistrict.setText(district == null ? getString(R.string.text_hint_district) : district.getName());
            }
        };
    }

    private void handleUpdateInfo() {
        String fullName = inputFullName.getText().toString();
        if (fullName.isEmpty()) {
            L.showAlert(this, null, getString(R.string.text_err_full_name));
            InputValidation.requestFocus(this, inputFullName);
            return;
        }

        String phoneNumber = inputPhoneNumber.getText().toString();
        if (!Utils.isValidPhoneNumber(phoneNumber)) {
            L.showAlert(this, null, getString(R.string.text_err_phone));
            InputValidation.requestFocus(this, inputPhoneNumber);
            return;
        }

        if (district == null) {
            L.showAlert(this, null, getString(R.string.text_err_district));
            return;
        }

        final ProgressDialog progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_update));
        progressDialog.show();

        account.setFullName(fullName);
        account.setGender(spnGender.getSelectedItemPosition());
        account.setBirthDate(birthDate != null ? birthDate : account.getBirthDate());
        account.setPhoneNumber(phoneNumber);
        account.setAddress(inputAddress.getText().toString());
        account.setProvinceID(province.getId());
        account.setDistrictID(district.getId());

        MultipartRequest reqUpdate = new MultipartRequest(EndPoints.UPDATE_INFO, null, com.gomicorp.propertyhero.json.Utils.mimeType, infoPathBody(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    Toast.makeText(AccountInfoActivity.this, getString(R.string.text_update_success), Toast.LENGTH_LONG).show();
                } else
                    L.showToast(getString(R.string.err_request_api));

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at handleUpdateInfo()");
                L.showToast(getString(R.string.request_time_out));
                progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(reqUpdate, TAG);

    }

    private byte[] infoPathBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "AccountID", String.valueOf(account.getId()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "FullName", com.gomicorp.propertyhero.json.Utils.iso88951_To_utf8(account.getFullName()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "Gender", String.valueOf(account.getGender()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "BirthDate", com.gomicorp.helper.Utils.dateToString(account.getBirthDate()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "PhoneNumber", account.getPhoneNumber());
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "Email", account.getEmail());
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "Address", com.gomicorp.propertyhero.json.Utils.iso88951_To_utf8(account.getAddress()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "CountryID", String.valueOf(account.getCountryID()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "ProvinceID", String.valueOf(account.getProvinceID()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "DistrictID", String.valueOf(account.getDistrictID()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "IDCode", account.getIdCode());
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "IssuedDate", com.gomicorp.helper.Utils.dateToString(account.getIssuedDate()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "IssuedPlace", com.gomicorp.propertyhero.json.Utils.iso88951_To_utf8(account.getIssuedPlace()));

            dos.writeBytes(com.gomicorp.propertyhero.json.Utils.twoHyphens + com.gomicorp.propertyhero.json.Utils.boundary + com.gomicorp.propertyhero.json.Utils.twoHyphens + com.gomicorp.propertyhero.json.Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
