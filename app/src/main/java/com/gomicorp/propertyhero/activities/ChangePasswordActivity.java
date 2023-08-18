package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    private TextInputLayout inputLayoutOldPwd, inputLayoutNewPwd, inputLayoutConfirm;
    private EditText inputOldPwd, inputNewPwd, inputConfirm;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        inputLayoutOldPwd = (TextInputLayout) findViewById(R.id.inputLayoutOldPwd);
        inputLayoutNewPwd = (TextInputLayout) findViewById(R.id.inputLayoutNewPwd);
        inputLayoutConfirm = (TextInputLayout) findViewById(R.id.inputLayoutConfirm);

        inputOldPwd = (EditText) findViewById(R.id.inputOldPwd);
        inputNewPwd = (EditText) findViewById(R.id.inputNewPwd);
        inputConfirm = (EditText) findViewById(R.id.inputConfirm);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_update));

        findViewById(R.id.btnSubmitChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChangePassword();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleChangePassword() {
        if (!InputValidation.oldPassword(this, inputLayoutOldPwd, inputOldPwd))
            return;

        if (!InputValidation.password(this, inputLayoutNewPwd, inputNewPwd, getString(R.string.text_err_new_pwd_empty), getString(R.string.text_err_pwd_length)))
            return;
        final String pwd = inputNewPwd.getText().toString().trim();

        if (!InputValidation.confirmPassword(this, inputLayoutConfirm, inputConfirm, pwd))
            return;

        progressDialog.show();
        String url = EndPoints.CHANGE_PWD
                .replace(UrlParams.ACCOUNT_ID, String.valueOf(AppController.getInstance().getPrefManager().getUserID()))
                .replace(UrlParams.PASSWORD, pwd);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    finish();
                    L.showToast(getString(R.string.text_update_success));
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

        AppController.getInstance().addToRequestQueue(request, TAG);
    }
}
