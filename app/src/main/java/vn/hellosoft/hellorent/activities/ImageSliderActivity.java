package vn.hellosoft.hellorent.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.adapters.ImageSlideAdapter;

public class ImageSliderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        Bundle data = getIntent().getBundleExtra(Config.DATA_EXTRA);
        if (data == null)
            finish();

        int status = data.getInt(Config.STATUS_DATA, 0);
        int position = data.getInt(Config.RESULT_DATA, 0);
        List<String> imageList = data.getStringArrayList(Config.STRING_DATA);

        ViewPager pager = (ViewPager) findViewById(R.id.pagerImageSlider);
        ImageSlideAdapter adapter = new ImageSlideAdapter(this, imageList, status, true, new ImageSlideAdapter.OnPagerItemSelected() {
            @Override
            public void pagerItemSelected() {
                finish();
            }
        });

        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
    }
}
