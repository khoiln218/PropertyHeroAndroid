package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ManagementAdapter;
import com.gomicorp.propertyhero.callbacks.OnLoadMoreListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProductListener;
import com.gomicorp.propertyhero.callbacks.OnProductConfigListener;
import com.gomicorp.propertyhero.callbacks.OnRecyclerItemClickListener;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.gomicorp.ui.ProductConfigDialog;

import java.util.ArrayList;
import java.util.List;

public class ManagementProductActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = ManagementProductActivity.class.getSimpleName();

    private TextView tvSelectStatus;
    private EditText inputKeyword;
    private TextView tvTotalItems;
    private SwipeRefreshLayout refreshManagement;
    private RecyclerView recyclerManagement;

    private String[] statusText;
    private int[] statusValue;
    private String[] statusTitle;

    private List<Info> listStatus;
    private Info selectedSattus;

    private String title;
    private int pageNo = 1;
    private int totalRows;
    private List<Product> productList;
    private ManagementAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.managementLayout));

        tvSelectStatus = (TextView) findViewById(R.id.tvSelectStatus);
        inputKeyword = (EditText) findViewById(R.id.inputKeyword);
        tvTotalItems = (TextView) findViewById(R.id.tvTotalItems);
        refreshManagement = (SwipeRefreshLayout) findViewById(R.id.refreshManagement);
        recyclerManagement = (RecyclerView) findViewById(R.id.recyclerManagement);

        refreshManagement.setRefreshing(true);
        refreshManagement.setOnRefreshListener(this);

        initRecyclerView();
        prepareManagementStatusValue();

        tvSelectStatus.setOnClickListener(this);
        inputKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE)
                    onRefresh();

                return false;
            }
        });

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_update));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_create_product:
                if (AppController.getInstance().getPrefManager().getPhoneNumber() == null)
                    L.launchUpdatePhone(this, false);
                else
                    startActivity(new Intent(this, CreateProductActivity.class));
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelectStatus:
                showSelectedStatus();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshManagement.setRefreshing(true);

        productList.clear();
        adapter.addProductList(productList);
        tvTotalItems.setText(title);

        pageNo = 1;
        totalRows = 0;
        fetchProductData();
    }

    @Override
    public void onLoadMore() {
        if (!adapter.isLoading() && productList.size() < totalRows) {
            productList.add(null);
            adapter.addProductList(productList);
            pageNo++;

            fetchProductData();
        }
    }

    private void initRecyclerView() {
        recyclerManagement.setItemAnimator(new DefaultItemAnimator());
        recyclerManagement.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productList.add(null);
        adapter = new ManagementAdapter(productList, recyclerManagement, new OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                showProductConfigDialog(productList.get(position));
            }
        });

        recyclerManagement.setAdapter(adapter);
        adapter.setLoadMoreListener(this);
    }

    private void prepareManagementStatusValue() {
        statusText = getResources().getStringArray(R.array.management_status);
        statusValue = getResources().getIntArray(R.array.management_status_value);
        statusTitle = getResources().getStringArray(R.array.management_status_title);

        listStatus = new ArrayList<>();
        for (int i = 0; i < statusText.length; i++)
            listStatus.add(new Info(statusValue[i], statusText[i], statusTitle[i]));

        updateSelectStatus();
    }

    private void showSelectedStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(statusText, listStatus.indexOf(selectedSattus), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSattus = listStatus.get(which);
                updateSelectStatus();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateSelectStatus() {
        if (selectedSattus == null)
            selectedSattus = listStatus.get(0);

        title = selectedSattus.getContent();
        tvSelectStatus.setText(selectedSattus.getName());

        onRefresh();
    }

    private void fetchProductData() {
        ProductRequest.cancelRequest();
        if (adapter != null)
            adapter.setLoading();

        String keyword = inputKeyword.getText().toString().trim();
        ProductRequest.searchByAccount(selectedSattus.getId(), keyword, pageNo, new OnLoadProductListener() {
            @Override
            public void onSuccess(List<Product> products, int totalItems) {

                tvTotalItems.setText(title.replace("...", totalItems + ""));

                if (productList.size() > 0)
                    productList.remove(productList.size() - 1);

                totalRows = totalItems;
                productList.addAll(products);
                adapter.addProductList(productList);

                refreshManagement.setRefreshing(false);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchProductData()");
            }
        });
    }

    private void showProductConfigDialog(final Product product) {
        final ProductConfigDialog dialog = ProductConfigDialog.instance(product);
        dialog.show(getSupportFragmentManager(), "product_config");
        dialog.listener = new OnProductConfigListener() {
            @Override
            public void onClick(int status) {
                switch (status) {
                    case Config.PRODUCT_DELETE:
                        requestUpdateStatus(product.getId(), status, getString(R.string.msg_delete_success));
                        break;
                    case Config.PRODUCT_ACTIVATED:
                        requestUpdateStatus(product.getId(), status, getString(R.string.msg_re_certified_success));
                        break;
                    case Config.PRODUCT_COMPLETED:
                        requestUpdateStatus(product.getId(), status, getString(R.string.text_update_success));
                        break;
                    case Config.PRODUCT_END_CERTIFIED:
                        requestUpdateStatus(product.getId(), status, getString(R.string.text_update_success));
                        break;
                    default:
                        break;
                }

                dialog.dismiss();
            }
        };
    }

    private void requestUpdateStatus(long id, int status, final String msgSuccess) {
        progressDialog.show();
        ProductRequest.updateStatus(id, status, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess())
                    L.showToast(msgSuccess);
                else
                    L.showToast(getString(R.string.err_request_api));

                onRefresh();
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at requestDeleteProduct()");
                L.showToast(getString(R.string.request_time_out));
                progressDialog.dismiss();
            }
        });
    }
}
