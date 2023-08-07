package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 4/19/2016.
 */
public class Info implements Parcelable {

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel source) {
            return new Info(source);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    private int id;
    private String name;
    private String content;

    public Info(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        content = parcel.readString();
    }

    public Info(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Info(int id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(content);
    }
}
