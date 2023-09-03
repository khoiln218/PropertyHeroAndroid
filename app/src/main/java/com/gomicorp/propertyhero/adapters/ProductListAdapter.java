package com.gomicorp.propertyhero.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnLoadMoreListener;
import com.gomicorp.propertyhero.model.Product;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/9/2016.
 */
public class ProductListAdapter extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private int totalCertified;
    private int totalActivated;

    private OnLoadMoreListener loadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading = true;

    public ProductListAdapter(List<Product> productList, RecyclerView recyclerView) {
        this.productList = productList;

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

    public void addTotalItems(int totalCertified, int totalActivated) {
        this.totalCertified = totalCertified;
        this.totalActivated = totalActivated;
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
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == Config.VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_product, parent, false);
            viewHolder = new ViewHolderProduct(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ViewHolderProgress(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderProduct) {
            Product product = productList.get(position);

            int width = com.gomicorp.helper.Utils.getScreenWidth() / 3;

            String imageUrl = product.getThumbnail().split(", ")[0];
            if (TextUtils.isEmpty(imageUrl)) {
                ((ViewHolderProduct) holder).thumb.setImageResource(R.drawable.emptyimg);
            } else {
                Picasso.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.emptyimg)
                        .resize(width, width)
                        .into(((ViewHolderProduct) holder).thumb);
            }

            ((ViewHolderProduct) holder).tvPrice.setText(product.getPrice() + " ");
            ((ViewHolderProduct) holder).tvArea.setText(product.getGrossFloorArea() + " ");
            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            String title = product.getTitle().replaceAll(characterFilter, "").trim();
            ((ViewHolderProduct) holder).tvTitle.setText(title);
            ((ViewHolderProduct) holder).tvAddress.setText(product.getAddresss());

            if (position == productList.size() - 1)
                loading = false;
        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public long getHeaderId(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_list_product, parent, false);
        return new ViewHolderHeader(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader) {
            if (productList.get(position).getStatus() == Config.PRODUCT_CERTIFIED) {
                ((ViewHolderHeader) holder).headerLayout.setBackgroundColor(Color.parseColor("#17BCCD"));
                ((ViewHolderHeader) holder).tvHeader.setText(holder.itemView.getResources().getString(R.string.text_certified));
                ((ViewHolderHeader) holder).tvNumItems.setText(holder.itemView.getResources().getString(R.string.text_total_items).replace("...", totalCertified + ""));
            } else if (productList.get(position).getStatus() == Config.PRODUCT_ACTIVATED) {
                ((ViewHolderHeader) holder).headerLayout.setBackgroundColor(Color.parseColor("#616161"));
                ((ViewHolderHeader) holder).tvHeader.setText(holder.itemView.getResources().getString(R.string.text_activated));
                ((ViewHolderHeader) holder).tvNumItems.setText(holder.itemView.getResources().getString(R.string.text_total_items).replace("...", totalActivated + ""));
            }
        }
    }

    protected class ViewHolderHeader extends RecyclerView.ViewHolder {
        LinearLayout headerLayout;
        TextView tvHeader, tvNumItems;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            headerLayout = (LinearLayout) itemView.findViewById(R.id.headerLayout);
            tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
            tvNumItems = (TextView) itemView.findViewById(R.id.tvNumItems);
        }
    }

    protected class ViewHolderProduct extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView tvPrice, tvArea, tvTitle, tvAddress;

        public ViewHolderProduct(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvArea = (TextView) itemView.findViewById(R.id.tvArea);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }
    }
}
