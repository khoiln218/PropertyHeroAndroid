package com.gomicorp.propertyhero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 5/12/2016.
 */
public class ImageSlideAdapter extends PagerAdapter {

    private Context context;
    private List<String> imageList;
    private int status;
    private LayoutInflater inflater;
    private boolean showCurrentItem;
    private OnPagerItemSelected listener;

    public ImageSlideAdapter(Context context, List<String> imageList, int status, boolean showCurrentItem, OnPagerItemSelected listener) {
        this.context = context;
        this.imageList = imageList;
        this.status = status;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCurrentItem = showCurrentItem;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.item_slider_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        ImageView logoCertified = (ImageView) view.findViewById(R.id.logoCertified);
        TextView tvSlider = (TextView) view.findViewById(R.id.tvSlider);

        if (status == Config.PRODUCT_CERTIFIED)
            logoCertified.setVisibility(View.VISIBLE);
        else
            logoCertified.setVisibility(View.GONE);

        if (showCurrentItem)
            tvSlider.setText((position + 1) + "/" + imageList.size());
        else
            tvSlider.setVisibility(View.GONE);

        int width = com.gomicorp.helper.Utils.getScreenWidth();
        Picasso.with(context)
                .load(imageList.get(position).trim())
                .placeholder(R.drawable.emptyimg)
                .error(R.drawable.emptyimg)
                .centerCrop()
                .resize(width, width)
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.pagerItemSelected();
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public interface OnPagerItemSelected {
        void pagerItemSelected();
    }
}
