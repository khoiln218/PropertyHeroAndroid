package com.gomicorp.propertyhero.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.json.Utils;
import com.gomicorp.propertyhero.model.GiftCard;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GiftCardActivity extends AppCompatActivity {

    private static final String TAG = GiftCardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final GiftCard obj = getIntent().getParcelableExtra(Config.DATA_EXTRA);
        if (obj == null)
            finish();

        final ImageView imageView = (ImageView) findViewById(R.id.imgCard);
        final TextView btnUpdate = (TextView) findViewById(R.id.btnUseCard);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnUpdate.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(this)
                .load(obj.getPictureCard())
                .placeholder(R.drawable.emptyimg)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        btnUpdate.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                requestUseCard(obj.getId(), obj.getName());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        AppController.getInstance().cancelPedingRequesrs(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestUseCard(int giftID, final String name) {

        MultipartRequest request = new MultipartRequest(EndPoints.URL_UPDATE_ACC_GIFT, null, Utils.mimeType, cardBodyPath(giftID), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    Toast.makeText(getApplicationContext(), "Bạn đã sử dụng " + name, Toast.LENGTH_LONG).show();
                    finish();
                } else
                    L.showToast(getString(R.string.err_request_api));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at requestUseCard()");
                L.showToast(getString(R.string.request_time_out));
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    private byte[] cardBodyPath(int giftID) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            Utils.buildTextPart(dos, "GiftID", String.valueOf(giftID));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
