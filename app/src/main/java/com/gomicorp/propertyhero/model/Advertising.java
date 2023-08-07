package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 5/13/2016.
 */
public class Advertising implements Parcelable {

    public static final Creator<Advertising> CREATOR = new Creator<Advertising>() {
        @Override
        public Advertising createFromParcel(Parcel source) {
            return new Advertising(source);
        }

        @Override
        public Advertising[] newArray(int size) {
            return new Advertising[size];
        }
    };

    private int id;
    private String thumbnail;
    private String imageDetails;
    private String title;
    private String description;
    private String url;
    private String companyName;
    private String contactPhone;

    public Advertising(Parcel source) {
        id = source.readInt();
        thumbnail = source.readString();
        imageDetails = source.readString();
        title = source.readString();
        description = source.readString();
        url = source.readString();
        companyName = source.readString();
        contactPhone = source.readString();
    }

    public Advertising(int id, String thumbnail, String imageDetails, String title, String description, String url, String companyName, String contactPhone) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.imageDetails = imageDetails;
        this.title = title;
        this.description = description;
        this.url = url;
        this.companyName = companyName;
        this.contactPhone = contactPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(String imageDetails) {
        this.imageDetails = imageDetails;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(thumbnail);
        dest.writeString(imageDetails);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(companyName);
        dest.writeString(contactPhone);
    }
}
