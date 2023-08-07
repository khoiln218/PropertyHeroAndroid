package com.gomicorp.propertyhero.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.propertyhero.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/14/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageGalleryViewHolder> {

    private List<String> imageList;
    private List<String> selectedList;

    public GalleryAdapter(List<String> imageList, List<String> selectedList) {
        this.imageList = imageList;
        this.selectedList = selectedList;
    }

    @Override
    public ImageGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gallery, parent, false);

        return new ImageGalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageGalleryViewHolder holder, final int position) {
        int width = com.gomicorp.helper.Utils.getScreenWidth() / 3;
        Picasso.with(holder.itemView.getContext())
                .load("file://" + imageList.get(position))
                .centerCrop()
                .placeholder(R.drawable.vector_place_holder)
                .resize(width, width)
                .into(holder.imageView);

        if (selectedList.contains(imageList.get(position)))
            holder.checkBox.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    protected class ImageGalleryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        CheckBox checkBox;

        public ImageGalleryViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageGallery);
            checkBox = (CheckBox) itemView.findViewById(R.id.chkImageGallery);
        }
    }
}
