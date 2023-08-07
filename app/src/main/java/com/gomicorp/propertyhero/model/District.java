package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 3/31/2016.
 */
public class District implements Parcelable {

    public static final Creator<District> CREATOR = new Creator<District>() {
        @Override
        public District createFromParcel(Parcel source) {
            return new District(source);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };

    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private int provinceID;

    public District() {
    }

    public District(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        provinceID = parcel.readInt();
    }

    public District(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public District(int id, String name, double latitude, double longitude, int provinceID) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provinceID = provinceID;
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

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(provinceID);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
