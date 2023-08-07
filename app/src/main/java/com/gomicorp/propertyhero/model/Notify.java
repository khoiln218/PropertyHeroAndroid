package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by CTO-HELLOSOFT on 5/19/2016.
 */
public class Notify implements Parcelable {

    public static final Creator<Notify> CREATOR = new Creator<Notify>() {
        @Override
        public Notify createFromParcel(Parcel source) {
            return new Notify(source);
        }

        @Override
        public Notify[] newArray(int size) {
            return new Notify[0];
        }
    };

    private int id;
    private String name;
    private String content;
    private String thumbnail;
    private String url;
    private Date createdDate;

    public Notify(Parcel source) {
        id = source.readInt();
        name = source.readString();
        content = source.readString();
        thumbnail = source.readString();
        url = source.readString();
        long date = source.readLong();
        createdDate = (date == -1 ? null : new Date(date));
    }

    public Notify(int id, String name, String content, String thumbnail, String url, Date createdDate) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.thumbnail = thumbnail;
        this.url = url;
        this.createdDate = createdDate;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(content);
        dest.writeString(thumbnail);
        dest.writeString(url);
        dest.writeLong(createdDate == null ? -1 : createdDate.getTime());
    }
}
