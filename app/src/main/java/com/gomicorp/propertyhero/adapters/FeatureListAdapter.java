package com.gomicorp.propertyhero.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public class FeatureListAdapter extends ArrayAdapter<Feature> {

    private LayoutInflater inflater;
    private int resource;
    private List<Feature> objects;
    private SparseBooleanArray selectedItems;

    public FeatureListAdapter(Context context, int resource, List<Feature> objects) {
        super(context, resource, objects);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.objects = objects;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.inflater.inflate(resource, null);

        final ImageView imgSelect = (ImageView) convertView.findViewById(R.id.itemSelect);
        TextView textView = (TextView) convertView.findViewById(R.id.tvItemName);

        if (selectedItems.get(position))
            imgSelect.setVisibility(View.VISIBLE);
        else
            imgSelect.setVisibility(View.INVISIBLE);

        textView.setText(objects.get(position).getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !selectedItems.get(position);
                if (isSelected) {
                    selectedItems.put(position, isSelected);
                    imgSelect.setVisibility(View.VISIBLE);
                } else {
                    selectedItems.delete(position);
                    imgSelect.setVisibility(View.INVISIBLE);
                }
            }
        });

        return convertView;
    }

    public void dataSetChanged(List<Feature> objects, List<Feature> selectList) {
        this.objects = objects;
        for (Feature obj : selectList) {
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getId() == obj.getId())
                    selectedItems.put(i, true);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Feature> getCheckedItems() {
        ArrayList<Feature> arrayList = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            if (selectedItems.get(i))
                arrayList.add(objects.get(i));
        }
        return arrayList;
    }
}
