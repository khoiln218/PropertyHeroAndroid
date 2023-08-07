package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 5/9/2016.
 */
public class SearchInfo implements Parcelable {

    public static final Creator<SearchInfo> CREATOR = new Creator<SearchInfo>() {
        @Override
        public SearchInfo createFromParcel(Parcel source) {
            return new SearchInfo(source);
        }

        @Override
        public SearchInfo[] newArray(int size) {
            return new SearchInfo[size];
        }
    };

    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;
    private String distance;
    private String propertyType;
    private String propertyID;
    private String minPrice;
    private String maxPrice;
    private String minArea;
    private String maxArea;
    private String bed;
    private String bath;
    private String status;
    private String pageNo;

    public SearchInfo(Parcel parcel) {
        startLat = parcel.readString();
        startLng = parcel.readString();
        endLat = parcel.readString();
        endLng = parcel.readString();
        distance = parcel.readString();
        propertyType = parcel.readString();
        propertyID = parcel.readString();
        minPrice = parcel.readString();
        maxPrice = parcel.readString();
        minArea = parcel.readString();
        maxArea = parcel.readString();
        bed = parcel.readString();
        bath = parcel.readString();
        status = parcel.readString();
        pageNo = parcel.readString();
    }

    public SearchInfo(double startLat, double startLng, float distance, int propertyType) {
        this.startLat = String.valueOf(startLat);
        this.startLng = String.valueOf(startLng);
        this.distance = String.valueOf(distance);
        this.propertyType = String.valueOf(propertyType);
    }

    public SearchInfo(String startLat, String startLng, String distance, String propertyType, String propertyID, String minPrice, String maxPrice, String minArea, String maxArea, String bed, String bath, String status, String pageNo) {
        this.startLat = startLat;
        this.startLng = startLng;
        this.distance = distance;
        this.propertyType = propertyType;
        this.propertyID = propertyID;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.bed = bed;
        this.bath = bath;
        this.status = status;
        this.pageNo = pageNo;
    }

    public SearchInfo(String startLat, String startLng, String endLat, String endLng, String propertyType, String propertyID, String minPrice, String maxPrice, String minArea, String maxArea, String bed, String bath, String status, String pageNo) {
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.propertyType = propertyType;
        this.propertyID = propertyID;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.bed = bed;
        this.bath = bath;
        this.status = status;
        this.pageNo = pageNo;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLng() {
        return endLng;
    }

    public void setEndLng(String endLng) {
        this.endLng = endLng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinArea() {
        return minArea;
    }

    public void setMinArea(String minArea) {
        this.minArea = minArea;
    }

    public String getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(String maxArea) {
        this.maxArea = maxArea;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getBath() {
        return bath;
    }

    public void setBath(String bath) {
        this.bath = bath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startLat);
        dest.writeString(startLng);
        dest.writeString(endLat);
        dest.writeString(endLng);
        dest.writeString(distance);
        dest.writeString(propertyType);
        dest.writeString(propertyID);
        dest.writeString(minPrice);
        dest.writeString(maxPrice);
        dest.writeString(minArea);
        dest.writeString(maxArea);
        dest.writeString(bed);
        dest.writeString(bath);
        dest.writeString(status);
        dest.writeString(pageNo);
    }
}
