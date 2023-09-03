package com.gomicorp.propertyhero.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/12/2016.
 */
public class ProductCollectionAdapter extends RecyclerView.Adapter<ProductCollectionAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductCollectionAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_collection, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product obj = productList.get(position);

        holder.tvTitle.setText(obj.getTitle());
        holder.tvAddress.setText(obj.getAddresss());
        holder.tvContact.setText(obj.getContactName() + " (" + obj.getContactPhone() + ")");
        holder.tvContact.setVisibility(View.GONE);

        String imageUrl = obj.getThumbnail().split(", ")[0];
        if (TextUtils.isEmpty(imageUrl)) {
            holder.thumbnail.setImageResource(R.drawable.emptyimg);
        } else {
            Picasso.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.emptyimg)
                    .resize(96, 96)
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    protected class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView tvTitle, tvAddress, tvContact;

        public ProductViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvContact = (TextView) itemView.findViewById(R.id.tvContact);
        }
    }
}
