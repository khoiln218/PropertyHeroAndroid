package com.gomicorp.propertyhero.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by CTO-HELLOSOFT on 5/12/2016.
 */
public class ProductItem implements ClusterItem {

    private long id;
    private String thumbnail;
    private double price;
    private String address;
    private LatLng position;

    public ProductItem(long id, String thumbnail, double price, String address, double lat, double lng) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.price = price;
        this.address = address;
        this.position = new LatLng(lat, lng);
    }

    public long getId() {
        return id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    @Nullable
    @Override
    public Float getZIndex() {
        return null;
    }
}
