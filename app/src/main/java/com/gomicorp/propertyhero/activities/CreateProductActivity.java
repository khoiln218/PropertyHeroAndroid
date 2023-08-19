package com.gomicorp.propertyhero.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.app.PermissionHelper;
import com.gomicorp.helper.NetworkChangeReceiver;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ViewPagerAdapter;
import com.gomicorp.propertyhero.fragments.CreateProductStep1Fragment;
import com.gomicorp.propertyhero.fragments.CreateProductStep2Fragment;
import com.gomicorp.propertyhero.fragments.CreateProductStep3Fragment;
import com.gomicorp.propertyhero.fragments.CreateProductStep4Fragment;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.ui.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

public class CreateProductActivity extends AppCompatActivity {

    private static final String TAG = CreateProductActivity.class.getSimpleName();

    private static final int selectedColor = Color.parseColor("#FFC000");
    private static final int unSelectedColor = Color.parseColor("#E0E0E0");
    public Product productInfo;
    public List<String> imageList;
    private NetworkChangeReceiver networkChangeReceiver;
    private NonSwipeableViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionHelper.hasLocationPermissions(this);
            PermissionHelper.hasGalleryPermissions(this);
            PermissionHelper.hasCameraPermission(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        networkChangeReceiver = new NetworkChangeReceiver(this, (ViewGroup) findViewById(R.id.createProductLayout));
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        imageList = new ArrayList<>();
        productInfo = new Product(AppController.getInstance().getPrefManager().getUserID(), AppController.getInstance().getPrefManager().getFullName(), AppController.getInstance().getPrefManager().getPhoneNumber());


        viewPager = (NonSwipeableViewPager) findViewById(R.id.pagerCreateProduct);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CreateProductStep1Fragment.instance(productInfo), CreateProductStep1Fragment.class.getSimpleName());
        adapter.addFragment(CreateProductStep2Fragment.instance(imageList), CreateProductStep2Fragment.class.getSimpleName());
        adapter.addFragment(CreateProductStep3Fragment.instance(productInfo), CreateProductStep3Fragment.class.getSimpleName());
        adapter.addFragment(CreateProductStep4Fragment.instance(productInfo, imageList), CreateProductStep4Fragment.class.getSimpleName());
        viewPager.setAdapter(adapter);

        setupTabStep();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleFinishActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        if (requestCode == Config.REQUEST_CAMERA) {
            if (fragment instanceof CreateProductStep2Fragment)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void nextStep() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < adapter.getCount())
            viewPager.setCurrentItem(currentItem + 1, true);
    }

    public void backStep() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0)
            viewPager.setCurrentItem(currentItem - 1, true);
    }


    private void setupTabStep() {
        final View line1 = findViewById(R.id.line1);
        final View line2 = findViewById(R.id.line2);
        final View line3 = findViewById(R.id.line3);

        ((GradientDrawable) findViewById(R.id.step1).getBackground()).setColor(selectedColor);

        final GradientDrawable step2 = (GradientDrawable) findViewById(R.id.step2).getBackground();
        final GradientDrawable step3 = (GradientDrawable) findViewById(R.id.step3).getBackground();
        final GradientDrawable step4 = (GradientDrawable) findViewById(R.id.step4).getBackground();


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        unSelected(line1, step2);
                        unSelected(line2, step3);
                        unSelected(line3, step4);
                        break;
                    case 1:
                        isSelected(line1, step2);
                        unSelected(line2, step3);
                        unSelected(line3, step4);
                        break;
                    case 2:
                        isSelected(line1, step2);
                        isSelected(line2, step3);
                        unSelected(line3, step4);
                        break;
                    case 3:
                        isSelected(line3, step4);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void isSelected(View line, GradientDrawable step) {
        line.setBackgroundColor(selectedColor);
        step.setColor(selectedColor);
    }

    private void unSelected(View line, GradientDrawable step) {
        line.setBackgroundColor(unSelectedColor);
        step.setColor(unSelectedColor);
    }

    private void handleFinishActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_finish_create_product));
        builder.setMessage(getString(R.string.msg_finish_create_product));
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.btn_continues), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
