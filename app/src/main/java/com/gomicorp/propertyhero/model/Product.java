package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/19/2016.
 */
public class Product implements Parcelable {

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private long id;
    private String addresss;
    private double latitude;
    private double longitude;
    private int countryID;
    private int provinceID;
    private int districtID;
    private int propertyID;
    private int buildingID;
    private String thumbnail;
    private double deposit;
    private double price;
    private int floor;
    private int floorCount;
    private double siteArea;
    private double grossFloorArea;
    private int bedroom;
    private int bathroom;
    private int directionID;
    private double serviceFee;
    private String featureList;
    private String furnitureList;
    private byte elevator;
    private byte pets;
    private int numPerson;
    private String title;
    private String content;
    private String note;
    private long accountID;
    private String contactName;
    private String contactPhone;
    private int status;
    private int numView;
    private int numLike;
    private int isLikeThis;
    private String propertyName;
    private String directionName;
    private String buildingName;

    private List<Feature> features;
    private List<Feature> furnitures;

    public Product(Parcel parcel) {
        id = parcel.readLong();
        addresss = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        countryID = parcel.readInt();
        provinceID = parcel.readInt();
        districtID = parcel.readInt();
        propertyID = parcel.readInt();
        buildingID = parcel.readInt();
        thumbnail = parcel.readString();
        deposit = parcel.readDouble();
        price = parcel.readDouble();
        floor = parcel.readInt();
        floorCount = parcel.readInt();
        siteArea = parcel.readDouble();
        grossFloorArea = parcel.readDouble();
        bedroom = parcel.readInt();
        bathroom = parcel.readInt();
        directionID = parcel.readInt();
        serviceFee = parcel.readDouble();
        featureList = parcel.readString();
        furnitureList = parcel.readString();
        elevator = parcel.readByte();
        pets = parcel.readByte();
        numPerson = parcel.readInt();
        title = parcel.readString();
        content = parcel.readString();
        note = parcel.readString();
        accountID = parcel.readLong();
        contactName = parcel.readString();
        contactPhone = parcel.readString();
        status = parcel.readInt();
        numView = parcel.readInt();
        numLike = parcel.readInt();
        isLikeThis = parcel.readInt();
        propertyName = parcel.readString();
        directionName = parcel.readString();
        buildingName = parcel.readString();
    }

    public Product(long accountID, String contactName, String contactPhone) {
        this.accountID = accountID;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.features = new ArrayList<>();
        this.furnitures = new ArrayList<>();
    }

    public Product(long id, String thumbnail, String addresss, double price, double grossFloorArea, String title, String contactName, String contactPhone) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.addresss = addresss;
        this.price = price;
        this.grossFloorArea = grossFloorArea;
        this.title = title;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
    }

    public Product(long id, String addresss, double latitude, double longitude, int countryID, int provinceID, int districtID, int propertyID, int buildingID, int directionID, String thumbnail, double deposit, double price,
                   int floor, int floorCount, double siteArea, double grossFloorArea, int bedroom, int bathroom, double serviceFee, byte elevator, byte pets, int numPerson, String title, String content, String note,
                   long accountID, String contactName, String contactPhone, int status, int numView, int numLike, int isLikeThis, String propertyName, String directionName, String buildingName, List<Feature> features, List<Feature> furnitures) {
        this.id = id;
        this.addresss = addresss;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryID = countryID;
        this.provinceID = provinceID;
        this.districtID = districtID;
        this.propertyID = propertyID;
        this.buildingID = buildingID;
        this.directionID = directionID;
        this.thumbnail = thumbnail;
        this.deposit = deposit;
        this.price = price;
        this.floor = floor;
        this.floorCount = floorCount;
        this.siteArea = siteArea;
        this.grossFloorArea = grossFloorArea;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.serviceFee = serviceFee;
        this.elevator = elevator;
        this.pets = pets;
        this.numPerson = numPerson;
        this.title = title;
        this.content = content;
        this.note = note;
        this.accountID = accountID;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = status;
        this.numView = numView;
        this.numLike = numLike;
        this.isLikeThis = isLikeThis;
        this.propertyName = propertyName;
        this.directionName = directionName;
        this.buildingName = buildingName;
        this.features = features;
        this.furnitures = furnitures;
    }

    public Product(long id, String addresss, double latitude, double longitude, int provinceID, int districtID, int propertyID, int buildingID, String thumbnail, double deposit, double price,
                   int floor, int floorCount, double siteArea, double grossFloorArea, int bedroom, int bathroom, int directionID, double serviceFee, String featureList, String furnitureList,
                   byte elevator, byte pets, int numPerson, String title, String content, String note, long accountID, String contactName, String contactPhone, int status) {
        this.id = id;
        this.addresss = addresss;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provinceID = provinceID;
        this.districtID = districtID;
        this.propertyID = propertyID;
        this.buildingID = buildingID;
        this.thumbnail = thumbnail;
        this.deposit = deposit;
        this.price = price;
        this.floor = floor;
        this.floorCount = floorCount;
        this.siteArea = siteArea;
        this.grossFloorArea = grossFloorArea;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.directionID = directionID;
        this.serviceFee = serviceFee;
        this.featureList = featureList;
        this.furnitureList = furnitureList;
        this.elevator = elevator;
        this.pets = pets;
        this.numPerson = numPerson;
        this.title = title;
        this.content = content;
        this.note = note;
        this.accountID = accountID;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddresss() {
        return addresss;
    }

    public void setAddresss(String addresss) {
        this.addresss = addresss;
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

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public int getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public int getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    public double getSiteArea() {
        return siteArea;
    }

    public void setSiteArea(double siteArea) {
        this.siteArea = siteArea;
    }

    public double getGrossFloorArea() {
        return grossFloorArea;
    }

    public void setGrossFloorArea(double grossFloorArea) {
        this.grossFloorArea = grossFloorArea;
    }

    public int getBedroom() {
        return bedroom;
    }

    public void setBedroom(int bedroom) {
        this.bedroom = bedroom;
    }

    public int getBathroom() {
        return bathroom;
    }

    public void setBathroom(int bathroom) {
        this.bathroom = bathroom;
    }

    public int getDirectionID() {
        return directionID;
    }

    public void setDirectionID(int directionID) {
        this.directionID = directionID;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getFeatureList() {
        return featureList;
    }

    public void setFeatureList(String featureList) {
        this.featureList = featureList;
    }

    public String getFurnitureList() {
        return furnitureList;
    }

    public void setFurnitureList(String furnitureList) {
        this.furnitureList = furnitureList;
    }

    public byte getElevator() {
        return elevator;
    }

    public void setElevator(byte elevator) {
        this.elevator = elevator;
    }

    public byte getPets() {
        return pets;
    }

    public void setPets(byte pets) {
        this.pets = pets;
    }

    public int getNumPerson() {
        return numPerson;
    }

    public void setNumPerson(int numPerson) {
        this.numPerson = numPerson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNumView() {
        return numView;
    }

    public void setNumView(int numView) {
        this.numView = numView;
    }

    public int getNumLike() {
        return numLike;
    }

    public void setNumLike(int numLike) {
        this.numLike = numLike;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getIsLikeThis() {
        return isLikeThis;
    }

    public void setIsLikeThis(int isLikeThis) {
        this.isLikeThis = isLikeThis;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Feature> getFurnitures() {
        return furnitures;
    }

    public void setFurnitures(List<Feature> furnitures) {
        this.furnitures = furnitures;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(addresss);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(countryID);
        dest.writeInt(provinceID);
        dest.writeInt(districtID);
        dest.writeInt(propertyID);
        dest.writeInt(buildingID);
        dest.writeString(thumbnail);
        dest.writeDouble(deposit);
        dest.writeDouble(price);
        dest.writeInt(floor);
        dest.writeInt(floorCount);
        dest.writeDouble(siteArea);
        dest.writeDouble(grossFloorArea);
        dest.writeInt(bedroom);
        dest.writeInt(bathroom);
        dest.writeInt(directionID);
        dest.writeDouble(serviceFee);
        dest.writeString(featureList);
        dest.writeString(furnitureList);
        dest.writeByte(elevator);
        dest.writeByte(pets);
        dest.writeInt(numPerson);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(note);
        dest.writeLong(accountID);
        dest.writeString(contactName);
        dest.writeString(contactPhone);
        dest.writeInt(status);
        dest.writeInt(numView);
        dest.writeInt(numLike);
        dest.writeInt(isLikeThis);
        dest.writeString(propertyName);
        dest.writeString(directionName);
        dest.writeString(buildingName);
    }
}
