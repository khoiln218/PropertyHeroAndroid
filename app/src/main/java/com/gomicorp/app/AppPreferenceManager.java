package com.gomicorp.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class AppPreferenceManager {

    private static final String TAG = AppPreferenceManager.class.getSimpleName();
    private static final String PREF_NAME = "Gomicorp.PropertyHeros";
    private static final String KEY_TOKEN = "TOKEN";
    private static final String KEY_FIRST_LAUNCH = "FirstLaunch";
    private static final String KEY_LANGUAGE = "LanguageType";
    private static final String KEY_LAST_LAT = "LastLatitude";
    private static final String KEY_LAST_LNG = "LastLongitude";
    private static final String KEY_MAP_ZOOM = "MapZoom";
    private static final String KEY_USER_ID = "UserID";
    private static final String KEY_USER_NAME = "UserName";
    private static final String KEY_FULL_NAME = "FullName";
    private static final String KEY_PHONE = "PhoneNumber";
    private static final String KEY_ACC_ROLE = "AccountRole";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_PROVINCE = "DefaultProvince";
    int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public AppPreferenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void Logout() {
        editor.clear();
        editor.commit();
    }

    public void addToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void addFirstLaunch() {
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.commit();
    }

    public boolean getFirstLaunch() {
        return pref.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void addLanguageType(int language) {
        editor.putInt(KEY_LANGUAGE, language);
        editor.commit();
    }

    public int getLanguageType() {
        return pref.getInt(KEY_LANGUAGE, 0);
    }

    public void addLastLatLng(LatLng latLng, float mapZoom) {
        editor.putString(KEY_LAST_LAT, String.valueOf(latLng.latitude));
        editor.putString(KEY_LAST_LNG, String.valueOf(latLng.longitude));
        editor.putFloat(KEY_MAP_ZOOM, mapZoom);
        editor.commit();
    }

    public LatLng getLastLatLng() {

        String lat = pref.getString(KEY_LAST_LAT, null);
        String lng = pref.getString(KEY_LAST_LNG, null);
        if (lat != null && lng != null)
            return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        return new LatLng(10.771513, 106.698387); // Ben Thanh Market;
    }

    public float getMapZoom() {
        return pref.getFloat(KEY_MAP_ZOOM, 15);
    }

    public void addUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public void addUserInfo(long userID, String userName, String fullName, String phoneNumber, int accountRole, String password) {
        if (userID > 0)
            editor.putLong(KEY_USER_ID, userID);

        if (userName != null && !userName.isEmpty())
            editor.putString(KEY_USER_NAME, userName);

        if (fullName != null && !fullName.isEmpty())
            editor.putString(KEY_FULL_NAME, fullName);

        if (phoneNumber != null && phoneNumber.trim().length() > 9)
            editor.putString(KEY_PHONE, phoneNumber);

        if (accountRole > 0)
            editor.putInt(KEY_ACC_ROLE, accountRole);

        if (password != null && !password.isEmpty())
            editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public long getUserID() {
        return pref.getLong(KEY_USER_ID, 0);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, null);
    }

    public String getPhoneNumber() {
        return pref.getString(KEY_PHONE, null);
    }

    public int getAccountRole() {
        return pref.getInt(KEY_ACC_ROLE, 0);
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public void addFilterSet(Map<String, String> filters) {

        editor.putString(Config.KEY_PROPERTY, filters.get(Config.KEY_PROPERTY));
        editor.putString(Config.KEY_MIN_PRICE, filters.get(Config.KEY_MIN_PRICE));
        editor.putString(Config.KEY_MAX_PRICE, filters.get(Config.KEY_MAX_PRICE));
        editor.putString(Config.KEY_MIN_AREA, filters.get(Config.KEY_MIN_AREA));
        editor.putString(Config.KEY_MAX_AREA, filters.get(Config.KEY_MAX_AREA));
        editor.putString(Config.KEY_BED, filters.get(Config.KEY_BED));
        editor.putString(Config.KEY_BATH, filters.get(Config.KEY_BATH));

        editor.commit();
    }

    public Map<String, String> getFilterSet() {
        Map<String, String> filterSet = new HashMap<>();

        filterSet.put(Config.KEY_PROPERTY, pref.getString(Config.KEY_PROPERTY, null));
        filterSet.put(Config.KEY_MIN_PRICE, pref.getString(Config.KEY_MIN_PRICE, null));
        filterSet.put(Config.KEY_MAX_PRICE, pref.getString(Config.KEY_MAX_PRICE, null));
        filterSet.put(Config.KEY_MIN_AREA, pref.getString(Config.KEY_MIN_AREA, null));
        filterSet.put(Config.KEY_MAX_AREA, pref.getString(Config.KEY_MAX_AREA, null));
        filterSet.put(Config.KEY_BED, pref.getString(Config.KEY_BED, null));
        filterSet.put(Config.KEY_BATH, pref.getString(Config.KEY_BATH, null));

        return filterSet;
    }

    public Map<String, String> defaultFilterSet() {
        HashMap<String, String> filers = new HashMap<>();
        filers.put(Config.KEY_PROPERTY, null);
        filers.put(Config.KEY_MIN_PRICE, null);
        filers.put(Config.KEY_MAX_PRICE, null);
        filers.put(Config.KEY_MIN_AREA, null);
        filers.put(Config.KEY_MAX_AREA, null);
        filers.put(Config.KEY_BED, null);
        filers.put(Config.KEY_BATH, null);

        return filers;
    }

    public void addDefaultProvince(int id) {
        editor.putInt(KEY_PROVINCE, id);
        editor.commit();
    }

    public int getDefaultProvince() {
        return pref.getInt(KEY_PROVINCE, 2);
    }
}
