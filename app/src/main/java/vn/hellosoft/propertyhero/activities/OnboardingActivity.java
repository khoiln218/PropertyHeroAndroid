package vn.hellosoft.propertyhero.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.hellosoft.app.AppController;
import vn.hellosoft.propertyhero.R;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        AppController.getInstance().getPrefManager().addFirstLaunch();
    }
}
