package vn.hellosoft.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CTO-HELLOSOFT on 4/12/2016.
 */
public class Property implements Parcelable {

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @Override
        public Property createFromParcel(Parcel source) {
            return new Property(source);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    private int id;
    private String name;
    private int type;

    public Property() {
    }

    public Property(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        type = parcel.readInt();
    }

    public Property(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(type);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
