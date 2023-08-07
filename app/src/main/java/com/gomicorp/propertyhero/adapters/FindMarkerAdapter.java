package com.gomicorp.propertyhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/5/2016.
 */
public class FindMarkerAdapter extends RecyclerView.Adapter {

    private List<Marker> markerList;
    private int type;

    public FindMarkerAdapter(List<Marker> markerList, int type) {
        this.markerList = markerList;
        this.type = type;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return markerList.get(position) == null ? Config.VIEW_PROGRESS : Config.VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == Config.VIEW_PROGRESS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ViewHolderProgress(view);
        } else if (type == Config.MARKER_ATTR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_attraction, parent, false);
            viewHolder = new AttractionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_marker, parent, false);
            viewHolder = new MarkerViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AttractionViewHolder) {

            Marker marker = markerList.get(position);
            ((AttractionViewHolder) holder).tvName.setText(marker.getName());

            if (!Utils.isNullOrEmpty(marker.getThumbnail())) {
                Picasso.with(holder.itemView.getContext())
                        .load(marker.getThumbnail())
                        .placeholder(R.drawable.emptyimg)
                        .into(((AttractionViewHolder) holder).thumb);
            }

        } else if (holder instanceof MarkerViewHolder) {

            Marker marker = markerList.get(position);
            ((MarkerViewHolder) holder).tvName.setText(marker.getName());
            ((MarkerViewHolder) holder).tvAddress.setText(marker.getAddress());

            if (!Utils.isNullOrEmpty(marker.getThumbnail())) {
                Picasso.with(holder.itemView.getContext())
                        .load(marker.getThumbnail())
                        .placeholder(R.drawable.emptyimg)
                        .into(((MarkerViewHolder) holder).thumb);
            }
        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    protected class AttractionViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView tvName;

        public AttractionViewHolder(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    protected class MarkerViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView tvName, tvAddress;

        public MarkerViewHolder(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }
    }
}
