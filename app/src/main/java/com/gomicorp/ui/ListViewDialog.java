package com.gomicorp.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ParcelableListAdapter;
import com.gomicorp.propertyhero.callbacks.OnListViewDialogListener;
import com.gomicorp.propertyhero.json.DataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/4/2016.
 */
public class ListViewDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = ListViewDialog.class.getSimpleName();
    public OnListViewDialogListener listener;
    private ListView lvDialog;
    private int dataType;
    private List<Parcelable> parcelableList;
    private ParcelableListAdapter adapter;

    public ListViewDialog() {
    }

    public static ListViewDialog instance(int dataType, ArrayList<Parcelable> parcelableList) {
        ListViewDialog listViewDialog = new ListViewDialog();
        Bundle args = new Bundle();
        args.putInt(Config.DATA_TYPE, dataType);
        args.putParcelableArrayList(Config.PARCELABLE_DATA, parcelableList);
        listViewDialog.setArguments(args);
        return listViewDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setContentView(R.layout.dialog_list_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle = (TextView) dialog.findViewById(R.id.titleDialog);
        lvDialog = (ListView) dialog.findViewById(R.id.lvDialog);

        dataType = getArguments().getInt(Config.DATA_TYPE, -1);
        parcelableList = getArguments().getParcelableArrayList(Config.PARCELABLE_DATA);

        if (dataType < 0 || parcelableList.size() == 0)
            dismiss();

        switch (dataType) {
            case Config.PROVINCE_TYPE:
                tvTitle.setText(getString(R.string.text_province));
                break;
            case Config.DISTRICT_TYPE:
                tvTitle.setText(getString(R.string.text_district));
                break;
            case Config.PROPERTY_TYPE:
                tvTitle.setText(getString(R.string.text_property));
                break;
            case Config.MARKER_TYPE:
                tvTitle.setText(getString(R.string.text_building));
                break;
            case Config.DIRECTION_TYPE:
                tvTitle.setText(getString(R.string.title_house_direction));
                break;
        }

        adapter = new ParcelableListAdapter(getActivity(), R.layout.item_list_view, parcelableList, dataType);
        lvDialog.setAdapter(adapter);
        lvDialog.setOnItemClickListener(this);

        dialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataRequest.cancelRequest();
                dismiss();
            }
        });

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        listener.onClick(parcelableList.get(position));

        DataRequest.cancelRequest();
        dismiss();
    }
}
