package vn.hellosoft.hellorent.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.extras.EndPoints;
import vn.hellosoft.hellorent.extras.UrlParams;
import vn.hellosoft.hellorent.json.Parser;
import vn.hellosoft.hellorent.model.Info;
import vn.hellosoft.helper.L;

public class InfoViewActivity extends AppCompatActivity {

    private static final String TAG = InfoViewActivity.class.getSimpleName();

    private String title;
    private RelativeLayout progressLayout;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_view);

        int type = getIntent().getIntExtra(Config.DATA_EXTRA, -1);

        if (type < 0)
            finish();

        title = "...";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(title);

        progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
        tvContent = (TextView) findViewById(R.id.tvContent);

        progressLayout.setVisibility(View.VISIBLE);

        fetchInfoData(type);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchInfoData(int type) {
        String url = EndPoints.URL_GET_INFO
                .replace(UrlParams.TYPE, String.valueOf(type))
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Info> infoList = Parser.infoList(response);
                if (infoList.size() > 0) {
                    getSupportActionBar().setTitle(title.replace("...", infoList.get(0).getName()));
                    tvContent.setText(Html.fromHtml(infoList.get(0).getContent()));
                    progressLayout.setVisibility(View.GONE);
                } else
                    L.showToast(getString(R.string.err_request_api));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in fetchInfoData()");
                L.showToast(getString(R.string.request_time_out));
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }
}
