package com.gomicorp.propertyhero.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by CTO-HELLOSOFT on 4/7/2016.
 */
public class Account implements Parcelable {

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private long id;
    private String userName;
    private String fullName;
    private int gender;
    private Date birthDate;
    private String phoneNumber;
    private String email;
    private String address;
    private int countryID;
    private int provinceID;
    private int districtID;
    private String avatar;
    private String idCode;
    private Date issuedDate;
    private String issuedPlace;
    private int accRole;
    private int accType;
    private int status;

    public Account(Parcel parcel) {
        id = parcel.readInt();
        userName = parcel.readString();
        fullName = parcel.readString();
        gender = parcel.readInt();
        long birthDateMillis = parcel.readLong();
        birthDate = (birthDateMillis == -1 ? null : new Date(birthDateMillis));
        phoneNumber = parcel.readString();
        email = parcel.readString();
        address = parcel.readString();
        countryID = parcel.readInt();
        provinceID = parcel.readInt();
        districtID = parcel.readInt();
        avatar = parcel.readString();
        idCode = parcel.readString();
        long issuedDateMillis = parcel.readLong();
        issuedDate = (issuedDateMillis == -1 ? null : new Date(issuedDateMillis));
        issuedPlace = parcel.readString();
        accRole = parcel.readInt();
        accType = parcel.readInt();
        status = parcel.readInt();
    }

    public Account(String userName, String fullName, int gender, Date birthDate, String email, int accType) {
        this.userName = userName;
        this.fullName = fullName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.email = email;
        this.accType = accType;
    }

    public Account(long id, String userName, String fullName, int gender, Date birthDate, String phoneNumber, String email, String address, int countryID, int provinceID, int districtID, String avatar, String idCode, Date issuedDate, String issuedPlace, int accRole, int accType, int status) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.countryID = countryID;
        this.provinceID = provinceID;
        this.districtID = districtID;
        this.avatar = avatar;
        this.idCode = idCode;
        this.issuedDate = issuedDate;
        this.issuedPlace = issuedPlace;
        this.accRole = accRole;
        this.accType = accType;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssuedPlace() {
        return issuedPlace;
    }

    public void setIssuedPlace(String issuedPlace) {
        this.issuedPlace = issuedPlace;
    }

    public int getAccRole() {
        return accRole;
    }

    public void setAccRole(int accRole) {
        this.accRole = accRole;
    }

    public int getAccType() {
        return accType;
    }

    public void setAccType(int accType) {
        this.accType = accType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(userName);
        dest.writeString(fullName);
        dest.writeInt(gender);
        dest.writeLong(birthDate == null ? -1 : birthDate.getTime());
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeInt(countryID);
        dest.writeInt(provinceID);
        dest.writeInt(districtID);
        dest.writeString(avatar);
        dest.writeString(idCode);
        dest.writeLong(issuedDate == null ? -1 : issuedDate.getTime());
        dest.writeString(issuedPlace);
        dest.writeInt(accRole);
        dest.writeInt(accType);
        dest.writeInt(status);
    }
}
