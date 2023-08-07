package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 4/18/2016.
 */
public class Marker implements Parcelable {

    public static final Creator<Marker> CREATOR = new Creator<Marker>() {
        @Override
        public Marker createFromParcel(Parcel source) {
            return new Marker(source);
        }

        @Override
        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };

    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String thumbnail;
    private int floorCount;

    public Marker(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        address = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        thumbnail = parcel.readString();
        floorCount = parcel.readInt();
    }

    public Marker(int id, String name, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Marker(int id, String name, String address, double latitude, double longitude, String thumbnail, int floorCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
        this.floorCount = floorCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(thumbnail);
        dest.writeInt(floorCount);
    }
}
