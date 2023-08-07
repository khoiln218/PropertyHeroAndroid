package com.gomicorp.propertyhero.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.gomicorp.app.Config;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.CreateProductActivity;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProductStep4Fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CreateProductStep4Fragment.class.getSimpleName();

    private TextView tvTitleLength, tvContentLength;
    private EditText inputTitle, inputContent, inputNode;

    private Product productInfo;
    private List<String> imageList;

    private Button btnSubmitCreateProduct;
    private ProgressDialog progressDialog;

    public CreateProductStep4Fragment() {
        // Required empty public constructor
    }

    public static CreateProductStep4Fragment instance(Product productInfo, List<String> imageList) {
        CreateProductStep4Fragment step4Fragment = new CreateProductStep4Fragment();
        Bundle args = new Bundle();
        args.putParcelable(Config.PARCELABLE_DATA, productInfo);
        args.putStringArrayList(Config.IMAGE_LIST, (ArrayList<String>) imageList);
        step4Fragment.setArguments(args);

        return step4Fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.productInfo = getArguments().getParcelable(Config.PARCELABLE_DATA);
            this.imageList = getArguments().getStringArrayList(Config.IMAGE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_product_step_4, container, false);

        Utils.hideSoftKeyboard(getActivity(), root.findViewById(R.id.layoutStep4));

        tvTitleLength = (TextView) root.findViewById(R.id.tvTitleLength);
        tvContentLength = (TextView) root.findViewById(R.id.tvContentLength);

        inputTitle = (EditText) root.findViewById(R.id.inputTitle);
        inputContent = (EditText) root.findViewById(R.id.inputContent);
        inputNode = (EditText) root.findViewById(R.id.inputNode);

        inputTitle.addTextChangedListener(new InputTextWatcher(inputTitle));
        inputContent.addTextChangedListener(new InputTextWatcher(inputContent));

        btnSubmitCreateProduct = (Button) root.findViewById(R.id.btnSubmitCreateProduct);

        root.findViewById(R.id.btnBackStep3).setOnClickListener(this);
        btnSubmitCreateProduct.setOnClickListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = L.progressDialog(getContext(), null, getString(R.string.text_msg_create_product));

        tvTitleLength.setText(Config.TITLE_TEXT.replace("...", "0"));
        tvContentLength.setText(Config.CONTENT_TEXT.replace("...", "0"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackStep3:
                ((CreateProductActivity) getActivity()).backStep();
                break;
            case R.id.btnSubmitCreateProduct:
                submitCreateProduct();
                break;
            default:
                break;
        }
    }

    private void submitCreateProduct() {
        btnSubmitCreateProduct.setEnabled(false);
        String title = inputTitle.getText().toString();
        String content = inputContent.getText().toString();

        if (title.length() < Config.MIN_TITLE || title.length() > Config.MAX_TITLE) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_title_product));
            InputValidation.requestFocus(getActivity(), inputTitle);
            btnSubmitCreateProduct.setEnabled(true);
            return;
        }

        if (content.length() < Config.MIN_CONTENT || content.length() > Config.MAX_CONTENT) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_content_product));
            InputValidation.requestFocus(getActivity(), inputContent);
            btnSubmitCreateProduct.setEnabled(true);
            return;
        }

        progressDialog.show();
        this.productInfo.setTitle(title);
        this.productInfo.setContent(content);
        this.productInfo.setNote(inputNode.getText().toString());

        ProductRequest.create(this.productInfo, this.imageList, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess()) {
                    L.showToast(getString(R.string.create_product_success));
                    getActivity().finish();
                } else
                    L.showToast(getString(R.string.err_request_api));

                progressDialog.dismiss();
                btnSubmitCreateProduct.setEnabled(true);
            }

            @Override
            public void onError(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
                progressDialog.dismiss();
                btnSubmitCreateProduct.setEnabled(true);
                Log.e(TAG, "Error at submitCreateProduct()");
            }
        });
    }

    private class InputTextWatcher implements TextWatcher {

        private View view;

        public InputTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.inputTitle:
                    int title = ((TextView) view).length();
                    tvTitleLength.setText(Config.TITLE_TEXT.replace("...", title + ""));
                    break;
                case R.id.inputContent:
                    int content = ((TextView) view).length();
                    tvContentLength.setText(Config.CONTENT_TEXT.replace("...", content + ""));
                    break;
            }
        }
    }
}
