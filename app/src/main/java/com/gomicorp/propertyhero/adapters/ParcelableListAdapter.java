package com.gomicorp.propertyhero.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.Property;
import com.gomicorp.propertyhero.model.Province;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/6/2016.
 */
public class ParcelableListAdapter extends ArrayAdapter<Parcelable> {

    private LayoutInflater inflater;
    private int resource;
    private List<Parcelable> objects;
    private int dataType;

    public ParcelableListAdapter(Context context, int resource, List<Parcelable> objects, int dataType) {
        super(context, resource, objects);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.objects = objects;
        this.dataType = dataType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.inflater.inflate(resource, null);

        TextView textView = (TextView) convertView.findViewById(R.id.tvItemName);


        switch (dataType) {
            case Config.PROVINCE_TYPE:
                textView.setText(((Province) objects.get(position)).getName());
                break;
            case Config.DISTRICT_TYPE:
                textView.setText(((District) objects.get(position)).getName());
                break;
            case Config.PROPERTY_TYPE:
                textView.setText(((Property) objects.get(position)).getName());
                break;
            case Config.MARKER_TYPE:
                textView.setText(((Marker) objects.get(position)).getName());
                break;
            case Config.DIRECTION_TYPE:
                textView.setText(((Info) objects.get(position)).getName());
                break;
        }

        return convertView;
    }
}
