package vn.hellosoft.propertyhero.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.helper.L;
import vn.hellosoft.helper.Utils;
import vn.hellosoft.propertyhero.R;
import vn.hellosoft.propertyhero.callbacks.OnListViewDialogListener;
import vn.hellosoft.propertyhero.callbacks.OnLoadPropertyListener;
import vn.hellosoft.propertyhero.json.DataRequest;
import vn.hellosoft.propertyhero.model.Property;
import vn.hellosoft.ui.ListViewDialog;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private TextView btnProperty;
    private EditText inputMinPrice, inputMaxPrice, inputMinArea, inputMaxArea;
    private RadioGroup groupBeds, groupBaths;

    private int propertyType;
    private Map<String, String> filterSet;

    private List<Property> propertyList;
    private Property property;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnProperty = (TextView) findViewById(R.id.btnProperty);
        inputMinPrice = (EditText) findViewById(R.id.inputMinPrice);
        inputMaxPrice = (EditText) findViewById(R.id.inputMaxPrice);
        inputMinArea = (EditText) findViewById(R.id.inputMinArea);
        inputMaxArea = (EditText) findViewById(R.id.inputMaxArea);
        groupBeds = (RadioGroup) findViewById(R.id.groupBeds);
        groupBaths = (RadioGroup) findViewById(R.id.groupBaths);

        filterSet = AppController.getInstance().getPrefManager().getFilterSet();
        progressDialog = L.progressDialog(this, null, getString(R.string.text_loading));

        propertyList = new ArrayList<>();
        propertyList.add(new Property(0, getString(R.string.text_view_all), 0));

        propertyType = getIntent().getIntExtra(Config.DATA_EXTRA, Config.UNDEFINED);

        btnProperty.setOnClickListener(this);
        findViewById(R.id.btnUpdateFilter).setOnClickListener(this);
        setupUI();

        fetchPropertyData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_reload) {
            filterSet = AppController.getInstance().getPrefManager().defaultFilterSet();
            property = null;
            setupUI();
            updateUI();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProperty:
                if (propertyList != null)
                    showListViewDialog();
                break;
            case R.id.btnUpdateFilter:
                submitUpdateFilter();
                break;
            default:
                break;
        }
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.PROPERTY_TYPE, new ArrayList<Parcelable>(propertyList));
        dialog.show(getSupportFragmentManager(), "create_product");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                property = (Property) object;
                updateUI();
            }
        };
    }

    private void fetchPropertyData() {
        progressDialog.show();
        DataRequest.propertyList(new OnLoadPropertyListener() {
            @Override
            public void onSuccess(List<Property> properties) {
                if (propertyType != Config.UNDEFINED) {
                    for (Property obj : properties) {
                        if (obj.getType() == propertyType)
                            propertyList.add(obj);
                    }
                } else
                    propertyList.addAll(properties);

                updateUI();
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchCategoryData()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void setupUI() {

        inputMinPrice.setText(filterSet.get(Config.KEY_MIN_PRICE));
        inputMaxPrice.setText(filterSet.get(Config.KEY_MAX_PRICE));

        inputMinArea.setText(filterSet.get(Config.KEY_MIN_AREA));
        inputMaxArea.setText(filterSet.get(Config.KEY_MAX_AREA));

        switch (filterSet.get(Config.KEY_BED) == null ? 0 : Integer.parseInt(filterSet.get(Config.KEY_BED))) {
            case 1:
                groupBeds.check(R.id.bed1);
                break;
            case 2:
                groupBeds.check(R.id.bed2);
                break;
            case 3:
                groupBeds.check(R.id.bed3);
                break;
            case 4:
                groupBeds.check(R.id.bed4);
                break;
            case 5:
                groupBeds.check(R.id.bed5);
                break;
            default:
                groupBeds.check(R.id.bedAny);
                break;
        }

        switch (filterSet.get(Config.KEY_BATH) == null ? 0 : Integer.parseInt(filterSet.get(Config.KEY_BATH))) {
            case 1:
                groupBaths.check(R.id.bath1);
                break;
            case 2:
                groupBaths.check(R.id.bath2);
                break;
            case 3:
                groupBaths.check(R.id.bath3);
                break;
            case 4:
                groupBaths.check(R.id.bath4);
                break;
            case 5:
                groupBaths.check(R.id.bath5);
                break;
            default:
                groupBaths.check(R.id.bathAny);
                break;
        }
    }

    private void updateUI() {
        if (property == null) {
            int cateID = filterSet.get(Config.KEY_PROPERTY) == null ? 0 : Integer.parseInt(filterSet.get(Config.KEY_PROPERTY));
            for (Property obj : propertyList) {
                if (obj.getType() == cateID) {
                    property = obj;
                    break;
                }
            }

            if (property == null)
                property = propertyList.get(0);
        }

        btnProperty.setText(property.getName());
    }

    private int getSelectedBed() {
        int selectedId = groupBeds.getCheckedRadioButtonId();
        String text = ((RadioButton) findViewById(selectedId)).getText().toString();
        try {
            text = text.substring(0, text.length() - 1);
            return Integer.parseInt(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private int getSelectedBath() {
        int selectedId = groupBaths.getCheckedRadioButtonId();
        String text = ((RadioButton) findViewById(selectedId)).getText().toString();
        try {
            text = text.substring(0, text.length() - 1);
            return Integer.parseInt(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private void submitUpdateFilter() {
        if (property == null) {
            L.showAlert(this, null, getString(R.string.text_err_property));
            return;
        }

        double minPrice = Utils.toNumber(inputMinPrice.getText().toString());
        double maxPrice = Utils.toNumber(inputMaxPrice.getText().toString());

        if (minPrice > maxPrice) {
            L.showAlert(this, null, getString(R.string.text_err_filter_price));
            return;
        }

        double minArea = Utils.toNumber(inputMinArea.getText().toString());
        double maxArea = Utils.toNumber(inputMaxArea.getText().toString());

        if (minArea > maxArea) {
            L.showAlert(this, null, getString(R.string.text_err_filter_area));
            return;
        }

        int bed = getSelectedBed();
        int bath = getSelectedBath();

        filterSet.put(Config.KEY_PROPERTY, String.valueOf(property.getId()));
        filterSet.put(Config.KEY_MIN_PRICE, minPrice > 0 ? String.valueOf(minPrice) : null);
        filterSet.put(Config.KEY_MAX_PRICE, maxPrice > 0 ? String.valueOf(maxPrice) : null);
        filterSet.put(Config.KEY_MIN_AREA, minArea > 0 ? String.valueOf(minArea) : null);
        filterSet.put(Config.KEY_MAX_AREA, maxArea > 0 ? String.valueOf(maxArea) : null);
        filterSet.put(Config.KEY_BED, String.valueOf(bed));
        filterSet.put(Config.KEY_BATH, String.valueOf(bath));

        AppController.getInstance().getPrefManager().addFilterSet(filterSet);

        setResult(Config.SUCCESS_RESULT);
        finish();
    }
}