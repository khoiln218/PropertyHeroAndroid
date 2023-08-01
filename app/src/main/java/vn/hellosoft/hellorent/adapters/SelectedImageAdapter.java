package vn.hellosoft.hellorent.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.hellosoft.hellorent.callbacks.OnRecyclerItemClickListener;
import vn.hellosoft.hellorent.R;

/**
 * Created by CTO-HELLOSOFT on 4/15/2016.
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder> {

    private List<String> imageList;
    private OnRecyclerItemClickListener itemClickListener;

    public SelectedImageAdapter(List<String> imageList, OnRecyclerItemClickListener itemClickListener) {
        this.imageList = imageList;
        this.itemClickListener = itemClickListener;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @Override
    public SelectedImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selected, parent, false);
        return new SelectedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedImageViewHolder holder, final int position) {
        int width = vn.hellosoft.helper.Utils.getScreenWidth() / 2;
        Picasso.with(holder.itemView.getContext())
                .load("file://" + imageList.get(position))
                .centerCrop()
                .placeholder(R.drawable.vector_place_holder)
                .resize(width, width)
                .into(holder.selectedImage);

        holder.unSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class SelectedImageViewHolder extends RecyclerView.ViewHolder {

        ImageView selectedImage;
        ImageButton unSelectedImage;

        public SelectedImageViewHolder(View itemView) {
            super(itemView);

            selectedImage = (ImageView) itemView.findViewById(R.id.selectedImage);
            unSelectedImage = (ImageButton) itemView.findViewById(R.id.unSelectedImage);
        }
    }
}
