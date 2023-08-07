package com.gomicorp.propertyhero.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gomicorp.propertyhero.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FACEBOOK_URL = "https://www.facebook.com/hellorent.vn";
    private static final String FACEBOOK_PAGE_ID = "hellorent.vn";
    private static final String GOOGLE_URL = "https://plus.google.com/u/0/communities/114405969678787462416";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.btnFacebook).setOnClickListener(this);
        findViewById(R.id.btnGoogle).setOnClickListener(this);
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
            case R.id.btnFacebook:
                try {
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL();
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnGoogle:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_URL));
                    intent.setPackage("com.google.android.apps.plus");
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public String getFacebookPageURL() {
        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
