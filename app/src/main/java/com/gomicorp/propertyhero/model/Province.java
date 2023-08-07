package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 3/31/2016.
 */
public class Province implements Parcelable {

    public static final Creator<Province> CREATOR = new Creator<Province>() {
        @Override
        public Province createFromParcel(Parcel source) {
            return new Province(source);
        }

        @Override
        public Province[] newArray(int size) {
            return new Province[size];
        }
    };

    private int id;
    private String name;
    private String postalCode;

    public Province() {
    }

    public Province(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        postalCode = parcel.readString();
    }

    public Province(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Province(int id, String name, String postalCode) {
        this.id = id;
        this.name = name;
        this.postalCode = postalCode;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(postalCode);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
