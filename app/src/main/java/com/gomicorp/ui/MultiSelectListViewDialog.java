package com.gomicorp.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.android.volley.VolleyError;
import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.FeatureListAdapter;
import com.gomicorp.propertyhero.callbacks.OnLoadFeatureListener;
import com.gomicorp.propertyhero.callbacks.OnMultiSelectDialogListener;
import com.gomicorp.propertyhero.json.DataRequest;
import com.gomicorp.propertyhero.model.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public class MultiSelectListViewDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = MultiSelectListViewDialog.class.getSimpleName();
    public OnMultiSelectDialogListener listener;
    private int dataType;
    private ListView listView;
    private List<Feature> featureList;
    private FeatureListAdapter featureAdapter;
    private ArrayList<Feature> selectedItems;

    public MultiSelectListViewDialog() {
    }

    public static MultiSelectListViewDialog instance(int dataType, ArrayList<Feature> listSelected) {
        MultiSelectListViewDialog dialog = new MultiSelectListViewDialog();
        Bundle args = new Bundle();
        args.putInt(Config.DATA_TYPE, dataType);
        args.putParcelableArrayList(Config.PARCELABLE_DATA, listSelected);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setContentView(R.layout.dialog_multi_select);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dataType = getArguments().getInt(Config.DATA_TYPE, -1);
        selectedItems = getArguments().getParcelableArrayList(Config.PARCELABLE_DATA);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.titleDialog);
        switch (dataType) {
            case Config.FEATURE_TYPE:
                tvTitle.setText(getString(R.string.text_feature));
                fetchFeatureList();
                break;
            case Config.FURNITURE_TYPE:
                tvTitle.setText(getString(R.string.text_furniture));
                fetchFurnitureList();
                break;
            default:
                closeDialog();
                break;
        }

        listView = (ListView) dialog.findViewById(R.id.lvMultiSelect);
        featureList = new ArrayList<>();
        featureAdapter = new FeatureListAdapter(getActivity(), R.layout.item_multi_select, featureList);
        listView.setAdapter(featureAdapter);

        dialog.findViewById(R.id.closeDialog).setOnClickListener(this);
        dialog.findViewById(R.id.submitSelect).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (size.y * 0.8));
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDialog:
                closeDialog();
                break;
            case R.id.submitSelect:
                listener.onSelected(featureAdapter.getCheckedItems());
                closeDialog();
                break;
        }
    }

    private void fetchFeatureList() {
        DataRequest.featureList(new OnLoadFeatureListener() {
            @Override
            public void onSuccess(List<Feature> features) {
                featureList.addAll(features);
                featureAdapter.dataSetChanged(featureList, selectedItems);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchFeatureList()");
            }
        });
    }

    private void fetchFurnitureList() {
        DataRequest.furnitureList(new OnLoadFeatureListener() {
            @Override
            public void onSuccess(List<Feature> features) {
                featureList.addAll(features);
                featureAdapter.dataSetChanged(featureList, selectedItems);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at fetchFurnitureList()");
            }
        });
    }

    private void closeDialog() {
        DataRequest.cancelRequest();
        dismiss();
    }
}
