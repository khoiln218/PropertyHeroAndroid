package com.gomicorp.propertyhero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.gomicorp.app.AppController;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Marker;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 3/30/2016.
 */
public class AreaNearbyAdapter extends BaseAdapter {

    private List<Marker> areaList;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public AreaNearbyAdapter(Context context, List<Marker> areaList) {
        this.areaList = areaList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return areaList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.inflater.inflate(R.layout.item_grid_attraction, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.nivNameAttractionNearby);
        TextView textView = (TextView) convertView.findViewById(R.id.tvNameAttractionNearby);

        if (!areaList.get(position).getThumbnail().isEmpty() && areaList.get(position).getThumbnail() != "")
            imageView.setImageUrl(areaList.get(position).getThumbnail(), imageLoader);


        textView.setText(areaList.get(position).getName());

        return convertView;
    }

    public void setAttractionList(List<Marker> areaList) {
        this.areaList = areaList;
        notifyDataSetChanged();
    }
}
