package com.gomicorp.propertyhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Advertising;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/13/2016.
 */
public class RelocationAdapter extends RecyclerView.Adapter {

    private List<Advertising> advertisingList;

    public RelocationAdapter(List<Advertising> advertisingList) {
        this.advertisingList = advertisingList;
    }

    public void setAdvertisingList(List<Advertising> advertisingList) {
        this.advertisingList = advertisingList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return advertisingList.get(position) == null ? Config.VIEW_PROGRESS : Config.VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == Config.VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_relocation, parent, false);
            holder = new RelocationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            holder = new ViewHolderProgress(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RelocationViewHolder) {

            Picasso.with(holder.itemView.getContext())
                    .load(advertisingList.get(position).getThumbnail())
                    .into(((RelocationViewHolder) holder).imageView);

        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return advertisingList.size();
    }

    protected class RelocationViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public RelocationViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.linkThumb);
        }
    }
}
