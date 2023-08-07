package com.gomicorp.propertyhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.ProductItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 6/20/2016.
 */
public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ClusterViewHolder> {

    private List<ProductItem> itemList;

    public ClusterAdapter(List<ProductItem> itemList) {
        this.itemList = itemList;
    }

    public void setItemList(List<ProductItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public ClusterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cluster, parent, false);
        return new ClusterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClusterViewHolder holder, int position) {

        int width = Utils.getScreenWidth() / 3;
        Picasso.with(holder.thumb.getContext())
                .load(itemList.get(position).getThumbnail())
                .resize(width, width)
                .centerCrop()
                .placeholder(R.drawable.emptyimg)
                .into(holder.thumb);

        holder.tvPrice.setText(itemList.get(position).getPrice() + "");
        holder.tvAddress.setText(itemList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    protected class ClusterViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView tvPrice, tvAddress;

        public ClusterViewHolder(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }
    }
}
