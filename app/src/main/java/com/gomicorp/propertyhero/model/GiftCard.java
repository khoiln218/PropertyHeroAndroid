package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 5/18/2016.
 */
public class GiftCard implements Parcelable {

    public static final Creator<GiftCard> CREATOR = new Creator<GiftCard>() {
        @Override
        public GiftCard createFromParcel(Parcel source) {
            return new GiftCard(source);
        }

        @Override
        public GiftCard[] newArray(int size) {
            return new GiftCard[size];
        }
    };

    private int id;
    private String name;
    private String content;
    private String thumbnail;
    private String pictureCard;

    public GiftCard(Parcel source) {
        id = source.readInt();
        name = source.readString();
        content = source.readString();
        thumbnail = source.readString();
        pictureCard = source.readString();
    }

    public GiftCard(int id, String name, String content, String thumbnail, String pictureCard) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.thumbnail = thumbnail;
        this.pictureCard = pictureCard;
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

    public String getPictureCard() {
        return pictureCard;
    }

    public void setPictureCard(String pictureCard) {
        this.pictureCard = pictureCard;
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
        dest.writeString(pictureCard);
    }
}
