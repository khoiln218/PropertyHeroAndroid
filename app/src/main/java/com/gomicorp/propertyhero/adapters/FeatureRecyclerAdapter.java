package com.gomicorp.propertyhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Feature;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/11/2016.
 */
public class FeatureRecyclerAdapter extends RecyclerView.Adapter {

    private List<Feature> featureList;

    public FeatureRecyclerAdapter(List<Feature> featureList) {
        this.featureList = featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return featureList.get(position) == null ? Config.VIEW_PROGRESS : Config.VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == Config.VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_feature, parent, false);
            viewHolder = new ViewHolderFeature(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ViewHolderProgress(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderFeature) {
            Picasso.with(holder.itemView.getContext())
                    .load(featureList.get(position).getThumb())
                    .placeholder(R.drawable.emptyimg)
                    .into(((ViewHolderFeature) holder).thumb);
            ((ViewHolderFeature) holder).tvName.setText(featureList.get(position).getName());
        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    protected class ViewHolderFeature extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView tvName;

        public ViewHolderFeature(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
