package com.gomicorp.propertyhero.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gomicorp.app.AppController;
import com.gomicorp.propertyhero.R;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        AppController.getInstance().getPrefManager().addFirstLaunch();
    }
}
