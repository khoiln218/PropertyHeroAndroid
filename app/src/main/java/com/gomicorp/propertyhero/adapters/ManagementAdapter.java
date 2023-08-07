package com.gomicorp.propertyhero.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnLoadMoreListener;
import com.gomicorp.propertyhero.callbacks.OnRecyclerItemClickListener;
import com.gomicorp.propertyhero.model.Product;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/30/2016.
 */
public class ManagementAdapter extends RecyclerView.Adapter {

    private List<Product> productList;

    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading = true;

    private OnLoadMoreListener loadMoreListener;
    private OnRecyclerItemClickListener clickListener;

    public ManagementAdapter(List<Product> productList, RecyclerView recyclerView, OnRecyclerItemClickListener clickListener) {
        this.productList = productList;
        this.clickListener = clickListener;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (totalItemCount <= (lastVisibleItem + visibleThreshold))
                        if (loadMoreListener != null)
                            loadMoreListener.onLoadMore();

                }
            });
        }
    }

    public void addProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading() {
        loading = true;
    }

    @Override
    public int getItemViewType(int position) {
        return productList.get(position) != null ? Config.VIEW_ITEM : Config.VIEW_PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == Config.VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_management, parent, false);
            viewHolder = new ManagementViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ViewHolderProgress(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ManagementViewHolder) {
            Product product = productList.get(holder.getAdapterPosition());

            if (product.getStatus() == Config.PRODUCT_ACTIVATED)
                ((ManagementViewHolder) holder).markLayout.setVisibility(View.GONE);
            else {
                ((ManagementViewHolder) holder).markLayout.setVisibility(View.VISIBLE);
                setImageMark(((ManagementViewHolder) holder).mark, product.getStatus());
            }

            int width = com.gomicorp.helper.Utils.getScreenWidth() / 3;
            Picasso.with(holder.itemView.getContext())
                    .load(product.getThumbnail())
                    .centerCrop()
                    .placeholder(R.drawable.emptyimg)
                    .resize(width, width)
                    .into(((ManagementViewHolder) holder).thumb);

            ((ManagementViewHolder) holder).tvPrice.setText(String.valueOf(product.getPrice()));
            ((ManagementViewHolder) holder).tvAddress.setText(String.valueOf(product.getGrossFloorArea()));
            ((ManagementViewHolder) holder).tvTitle.setText(product.getTitle());
            ((ManagementViewHolder) holder).tvAddress.setText(product.getAddresss());

            ((ManagementViewHolder) holder).tvProductID.setText(MessageFormat.format("#{0}", product.getId()));
            ((ManagementViewHolder) holder).tvNote.setText(product.getNote());

            ((ManagementViewHolder) holder).tvNumView.setText(String.valueOf(product.getNumView()));
            ((ManagementViewHolder) holder).tvNumLike.setText(String.valueOf(product.getNumLike()));

            ((ManagementViewHolder) holder).btnManagementOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(v, position);
                }
            });

            if (position == productList.size() - 1)
                loading = false;
        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void setImageMark(ImageView imageMark, int status) {
        switch (status) {
            case Config.PRODUCT_NEW:
                imageMark.setImageResource(R.drawable.vector_checking);
                break;
            case Config.PRODUCT_CERTIFIED:
                imageMark.setImageResource(R.drawable.vector_certified);
                break;
            case Config.PRODUCT_END_CERTIFIED:
                imageMark.setImageResource(R.drawable.vector_end_certified);
                break;
            case Config.PRODUCT_COMPLETED:
                imageMark.setImageResource(R.drawable.vector_completed);
                break;
            case Config.PRODUCT_FAILED:
                imageMark.setImageResource(R.drawable.vector_failed);
                break;
            default:
                break;
        }
    }

    protected class ManagementViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout markLayout;
        ImageView thumb, mark;

        TextView tvPrice, tvArea, tvTitle, tvAddress, tvProductID, tvNote, tvNumView, tvNumLike;
        ImageButton btnManagementOption;

        public ManagementViewHolder(View itemView) {
            super(itemView);

            markLayout = (RelativeLayout) itemView.findViewById(R.id.markLayout);
            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            mark = (ImageView) itemView.findViewById(R.id.imageMark);

            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvArea = (TextView) itemView.findViewById(R.id.tvArea);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvProductID = (TextView) itemView.findViewById(R.id.tvProductID);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvNumView = (TextView) itemView.findViewById(R.id.tvNumView);
            tvNumLike = (TextView) itemView.findViewById(R.id.tvNumLike);

            btnManagementOption = (ImageButton) itemView.findViewById(R.id.btnManagementOption);
        }
    }
}
