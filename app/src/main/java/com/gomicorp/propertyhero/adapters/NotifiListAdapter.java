package com.gomicorp.propertyhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnLoadMoreListener;
import com.gomicorp.propertyhero.model.GiftCard;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/18/2016.
 */
public class NotifiListAdapter extends RecyclerView.Adapter {


    private List<GiftCard> notifiList;

    private OnLoadMoreListener loadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading = true;

    public NotifiListAdapter(List<GiftCard> notifiList, RecyclerView recyclerView) {

        this.notifiList = notifiList;

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

    public void setNotifiList(List<GiftCard> notifiList) {
        this.notifiList = notifiList;
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
        return notifiList.get(position) != null ? Config.VIEW_ITEM : Config.VIEW_PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == Config.VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_notification, parent, false);
            viewHolder = new NotifiViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            viewHolder = new ViewHolderProgress(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotifiViewHolder) {
            GiftCard obj = notifiList.get(position);

            if (!Utils.isNullOrEmpty(obj.getThumbnail())) {
                Picasso.with(holder.itemView.getContext())
                        .load(obj.getThumbnail())
                        .placeholder(R.drawable.emptyimg)
                        .error(R.drawable.emptyimg)
                        .into(((NotifiViewHolder) holder).notifiThumb);
            }

            ((NotifiViewHolder) holder).notifiName.setText(obj.getName());
            ((NotifiViewHolder) holder).notifiContent.setText(obj.getContent());
        } else
            ((ViewHolderProgress) holder).progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return notifiList.size();
    }

    protected class NotifiViewHolder extends RecyclerView.ViewHolder {

        ImageView notifiThumb;
        TextView notifiName, notifiContent;

        public NotifiViewHolder(View itemView) {
            super(itemView);

            notifiThumb = (ImageView) itemView.findViewById(R.id.notifThumb);
            notifiName = (TextView) itemView.findViewById(R.id.notifName);
            notifiContent = (TextView) itemView.findViewById(R.id.notifiContent);
        }
    }
}
