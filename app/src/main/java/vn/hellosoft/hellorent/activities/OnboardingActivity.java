package vn.hellosoft.hellorent.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import vn.hellosoft.app.AppController;
import vn.hellosoft.hellorent.R;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        AppController.getInstance().getPrefManager().addFirstLaunch();
    }
}
