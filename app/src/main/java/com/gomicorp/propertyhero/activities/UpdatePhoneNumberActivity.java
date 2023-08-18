package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class UpdatePhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = UpdatePhoneNumberActivity.class.getSimpleName();

    private TextInputLayout inputLayoutPhone;
    private EditText inputPhone;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutUpdatePhone);
        inputPhone = (EditText) findViewById(R.id.inputUpdatePhone);

        findViewById(R.id.btnSubmitUpdatePhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdatePhone();
            }
        });

        inputPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE)
                    handleUpdatePhone();
                return false;
            }
        });

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_update));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleUpdatePhone() {
        if (!InputValidation.phoneNumber(this, inputLayoutPhone, inputPhone))
            return;

        progressDialog.show();
        final String phone = inputPhone.getText().toString().trim();
        long accID = AppController.getInstance().getPrefManager().getUserID();
        String url = EndPoints.UPDATE_PHONE
                .replace(UrlParams.USER_ID, String.valueOf(accID))
                .replace(UrlParams.ACCOUNT_ID, String.valueOf(accID))
                .replace(UrlParams.PHONE_NUMBER, phone);

        JsonObjectRequest reqUpdate = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    AppController.getInstance().getPrefManager().addUserInfo(0, null, null, phone, 0, null);
                    L.showToast(getString(R.string.text_update_success));

                    startActivity(new Intent(UpdatePhoneNumberActivity.this, CreateProductActivity.class));
                    finish();
                } else
                    L.showToast(getString(R.string.err_request_api));

                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
                progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(reqUpdate, TAG);
    }
}
