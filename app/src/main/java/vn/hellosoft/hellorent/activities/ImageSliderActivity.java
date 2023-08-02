package vn.hellosoft.hellorent.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
