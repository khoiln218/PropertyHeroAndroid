package com.gomicorp.propertyhero.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
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
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.callbacks.OnLoadInfoListener;
import com.gomicorp.propertyhero.callbacks.OnMultiSelectDialogListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.Feature;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.ui.ListViewDialog;
import com.gomicorp.ui.MultiSelectListViewDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProductStep3Fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CreateProductStep3Fragment.class.getSimpleName();

    private EditText inputDepositProduct, inputPriceProduct, inputFloorProduct, inputFloorCountProduct, inputSiteAreaProduct, inputGFAProduct,
            inputBedsProduct, inputBathsProduct, inputServiceFee, inputNumberOfPerson;
    private TextView tvFeature, tvDirection, tvFurniture;
    private Switch swtElevator, swtPets;

    private List<Info> directionList;

    private Product productInfo;

    public CreateProductStep3Fragment() {
        // Required empty public constructor
    }

    public static CreateProductStep3Fragment instance(Product product) {
        CreateProductStep3Fragment step3Fragment = new CreateProductStep3Fragment();
        Bundle args = new Bundle();
        args.putParcelable(Config.PARCELABLE_DATA, product);
        step3Fragment.setArguments(args);
        return step3Fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            this.productInfo = getArguments().getParcelable(Config.PARCELABLE_DATA);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_product_step_3, container, false);

        Utils.hideSoftKeyboard(requireActivity(), root.findViewById(R.id.layoutStep3));

        inputDepositProduct = (EditText) root.findViewById(R.id.inputDepositProduct);
        inputPriceProduct = (EditText) root.findViewById(R.id.inputPriceProduct);
        inputFloorProduct = (EditText) root.findViewById(R.id.inputFloorProduct);
        inputFloorCountProduct = (EditText) root.findViewById(R.id.inputFloorCountProduct);
        inputSiteAreaProduct = (EditText) root.findViewById(R.id.inputSiteAreaProduct);
        inputGFAProduct = (EditText) root.findViewById(R.id.inputGFAProduct);
        inputBedsProduct = (EditText) root.findViewById(R.id.inputBedsProduct);
        inputBathsProduct = (EditText) root.findViewById(R.id.inputBathsProduct);
        inputServiceFee = (EditText) root.findViewById(R.id.inputServiceFee);
        inputNumberOfPerson = (EditText) root.findViewById(R.id.inputNumberOfPerson);

        tvFeature = (TextView) root.findViewById(R.id.tvFeature);
        tvDirection = (TextView) root.findViewById(R.id.tvDirection);
        tvFurniture = (TextView) root.findViewById(R.id.tvFurniture);

        swtElevator = (Switch) root.findViewById(R.id.swtElevator);
        swtPets = (Switch) root.findViewById(R.id.swtPets);

        root.findViewById(R.id.btnSelectFeature).setOnClickListener(this);
        root.findViewById(R.id.btnSelectDirection).setOnClickListener(this);
        root.findViewById(R.id.btnSelectFurniture).setOnClickListener(this);
        root.findViewById(R.id.btnBackStep2).setOnClickListener(this);
        root.findViewById(R.id.btnNextStep3).setOnClickListener(this);

        fetchDirectionList();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (productInfo.getBuildingID() > 0) {
            inputFloorCountProduct.setText(productInfo.getFloorCount() + "");
            inputFloorCountProduct.setFocusableInTouchMode(false);
            inputFloorCountProduct.setFocusable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectFeature:
                showMultiSelectDialog(Config.FEATURE_TYPE, productInfo.getFeatures());
                break;
            case R.id.btnSelectDirection:
                showListViewDialog();
                break;
            case R.id.btnSelectFurniture:
                showMultiSelectDialog(Config.FURNITURE_TYPE, productInfo.getFurnitures());
                break;
            case R.id.btnBackStep2:
                ((CreateProductActivity) getActivity()).backStep();
                break;
            case R.id.btnNextStep3:
                handleNextStep();
                break;
        }
    }

    private void fetchDirectionList() {
        DataRequest.directionList(new OnLoadInfoListener() {
            @Override
            public void onSuccess(List<Info> infos) {
                directionList = infos;
                tvDirection.setText(directionList.get(0).getName());
                productInfo.setDirectionID(directionList.get(0).getId());
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchDirectionList()");
            }
        });
    }

    private void showMultiSelectDialog(final int dataType, List<Feature> listSelected) {
        MultiSelectListViewDialog selectDialog = MultiSelectListViewDialog.instance(dataType, (ArrayList<Feature>) listSelected);
        selectDialog.show(getFragmentManager(), "multi_select");
        selectDialog.listener = new OnMultiSelectDialogListener() {
            @Override
            public void onSelected(List<Feature> features) {
                if (dataType == Config.FEATURE_TYPE) {
                    productInfo.setFeatures(features);
                    tvFeature.setText(Utils.featureListToString(features, true));
                } else if (dataType == Config.FURNITURE_TYPE) {
                    productInfo.setFurnitures(features);
                    tvFurniture.setText(Utils.featureListToString(features, true));
                }
            }
        };
    }

    private void showListViewDialog() {
        ListViewDialog dialog = ListViewDialog.instance(Config.DIRECTION_TYPE, new ArrayList<Parcelable>(directionList));
        dialog.show(getFragmentManager(), "create_product");
        dialog.listener = new OnListViewDialogListener() {
            @Override
            public void onClick(Object object) {
                tvDirection.setText(((Info) object).getName());
                productInfo.setDirectionID(((Info) object).getId());
            }
        };
    }

    private void handleNextStep() {
        double price = Utils.toNumber(inputPriceProduct.getText().toString().trim());
        if (price == 0) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_price));
            InputValidation.requestFocus(getActivity(), inputPriceProduct);
            return;
        }

        double gfArea = Utils.toNumber(inputGFAProduct.getText().toString().trim());
        if (gfArea == 0) {
            L.showAlert(getActivity(), null, getString(R.string.text_err_gross_floor_area));
            InputValidation.requestFocus(getActivity(), inputGFAProduct);
            return;
        }

        this.productInfo.setDeposit(Utils.toNumber(inputDepositProduct.getText().toString().trim()));
        this.productInfo.setPrice(price);
        this.productInfo.setFloor((int) Utils.toNumber(inputFloorProduct.getText().toString().trim()));
        this.productInfo.setFloorCount((int) Utils.toNumber(inputFloorCountProduct.getText().toString().trim()));
        this.productInfo.setSiteArea(Utils.toNumber(inputSiteAreaProduct.getText().toString().trim()));
        this.productInfo.setGrossFloorArea(gfArea);
        this.productInfo.setBedroom((int) Utils.toNumber(inputBedsProduct.getText().toString().trim()));
        this.productInfo.setBathroom((int) Utils.toNumber(inputBathsProduct.getText().toString().trim()));
        this.productInfo.setServiceFee(Utils.toNumber(inputServiceFee.getText().toString().trim()));
        this.productInfo.setNumPerson((int) Utils.toNumber(inputNumberOfPerson.getText().toString().trim()));
        this.productInfo.setElevator((byte) (swtElevator.isChecked() ? 1 : 0));
        this.productInfo.setPets((byte) (swtPets.isChecked() ? 1 : 0));
        this.productInfo.setFeatureList(Utils.featureListToString(this.productInfo.getFeatures(), false));
        this.productInfo.setFurnitureList(Utils.featureListToString(this.productInfo.getFurnitures(), false));

        CreateProductActivity activity = (CreateProductActivity) getActivity();
        activity.productInfo = this.productInfo;
        activity.nextStep();
    }
}
