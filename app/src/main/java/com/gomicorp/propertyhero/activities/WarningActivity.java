package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.model.ResponseInfo;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WarningActivity extends AppCompatActivity {

    private static final String TAG = WarningActivity.class.getSimpleName();

    private RadioGroup groupWarning;
    private EditText inputContent;
    private Button btnSubmitWarning;

    private long productID;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        productID = getIntent().getLongExtra(Config.PRODUCT_ID, 0);
        if (productID == 0)
            finish();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.warningLayout));

        groupWarning = (RadioGroup) findViewById(R.id.groupWarning);
        inputContent = (EditText) findViewById(R.id.inputContent);
        btnSubmitWarning = (Button) findViewById(R.id.btnSubmitWarning);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_loading));

        groupWarning.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!btnSubmitWarning.isEnabled())
                    btnSubmitWarning.setEnabled(true);
            }
        });

        btnSubmitWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWarning();
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

    private void sendWarning() {
        progressDialog.show();
        MultipartRequest reqWarning = new MultipartRequest(EndPoints.URL_SEND_WARTNING, null, com.gomicorp.propertyhero.json.Utils.mimeType, pathBodyWarning(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null) {
                    if (info.isSuccess()) {
                        L.showToast(getString(R.string.text_warning_success));
                        finish();
                    } else
                        L.showToast(getString(R.string.text_warning_error));
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

        AppController.getInstance().addToRequestQueue(reqWarning, TAG);
    }

    private byte[] pathBodyWarning() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "ProductID", String.valueOf(productID));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "WarningType", String.valueOf(getWarningType()));
            com.gomicorp.propertyhero.json.Utils.buildTextPart(dos, "Content", com.gomicorp.propertyhero.json.Utils.iso88951_To_utf8(inputContent.getText().toString()));

            dos.writeBytes(com.gomicorp.propertyhero.json.Utils.twoHyphens + com.gomicorp.propertyhero.json.Utils.boundary + com.gomicorp.propertyhero.json.Utils.twoHyphens + com.gomicorp.propertyhero.json.Utils.lineEnd);

            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private int getWarningType() {
        if (groupWarning.getCheckedRadioButtonId() == R.id.radWaringComplete)
            return Config.WARNING_COMPLETED;
        else
            return Config.WARNING_INFO;
    }
}
