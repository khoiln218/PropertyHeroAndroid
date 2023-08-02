package vn.hellosoft.propertyhero.fragments;


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

import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.Config;
import vn.hellosoft.propertyhero.R;
import vn.hellosoft.propertyhero.activities.CreateProductActivity;
import vn.hellosoft.propertyhero.callbacks.OnListViewDialogListener;
import vn.hellosoft.propertyhero.callbacks.OnLoadInfoListener;
import vn.hellosoft.propertyhero.callbacks.OnMultiSelectDialogListener;
import vn.hellosoft.propertyhero.json.DataRequest;
import vn.hellosoft.propertyhero.model.Feature;
import vn.hellosoft.propertyhero.model.Info;
import vn.hellosoft.propertyhero.model.Product;
import vn.hellosoft.helper.InputValidation;
import vn.hellosoft.helper.L;
import vn.hellosoft.helper.Utils;
import vn.hellosoft.ui.ListViewDialog;
import vn.hellosoft.ui.MultiSelectListViewDialog;

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

        vn.hellosoft.helper.Utils.hideSoftKeyboard(getActivity(), root.findViewById(R.id.layoutStep3));

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
