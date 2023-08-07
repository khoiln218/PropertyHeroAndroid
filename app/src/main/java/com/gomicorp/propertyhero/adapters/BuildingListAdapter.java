package com.gomicorp.propertyhero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Marker;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public class BuildingListAdapter extends ArrayAdapter<Marker> {

    private LayoutInflater inflater;
    private int resource;
    private List<Marker> objects;

    public BuildingListAdapter(Context context, int resource, List<Marker> objects) {
        super(context, resource, objects);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.inflater.inflate(resource, null);

        ((TextView) convertView.findViewById(R.id.tvItemName)).setText(objects.get(position).getName());

        return convertView;
    }
}
