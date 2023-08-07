package com.gomicorp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by CTO-HELLOSOFT on 3/31/2016.
 */
public class HelloAppDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROVINCE = "Province";
    public static final String TABLE_DISTRICT = "District";
    public static final String TABLE_DIRECTION = "HouseDirection";
    public static final String TABLE_PRODUCT = "Product";
    public static final String COL_ID = "Id";
    public static final String COL_NAME = "Name";
    public static final String COL_POSTAL_CODE = "PostalCode";
    public static final String COL_LAT = "Latitude";
    public static final String COL_LNG = "Longitude";
    public static final String COL_THUMB = "Thumbnail";
    public static final String COL_PROV_ID = "ProvinceID";
    public static final String COL_PRICE = "Price";
    public static final String COL_AREA = "Area";
    public static final String COL_TITLE = "Title";
    public static final String COL_ADDRESS = "Address";
    public static final String COL_PHONE = "Phone";
    private static final String TAG = HelloAppDbHelper.class.getSimpleName();
    private static final String DB_NAME = "hellosoft_hellorent_db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE_PROVINCE = "CREATE TABLE " + TABLE_PROVINCE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_NAME + " TEXT, "
            + COL_POSTAL_CODE + " TEXT"
            + ");";

    private static final String CREATE_TABLE_DISTRICT = "CREATE TABLE " + TABLE_DISTRICT
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_NAME + " TEXT, "
            + COL_LAT + " TEXT, "
            + COL_LNG + " TEXT, "
            + COL_PROV_ID + " INTEGER"
            + ");";

    private static final String CREATE_TABLE_DIRECTION = "CREATE TABLE " + TABLE_DIRECTION
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_NAME + " TEXT"
            + ");";

    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT
            + " ("
            + COL_ID + " TEXT, "
            + COL_THUMB + " TEXT, "
            + COL_PRICE + " TEXT, "
            + COL_AREA + " TEXT, "
            + COL_TITLE + " TEXT, "
            + COL_ADDRESS + " TEXT, "
            + COL_NAME + " TEXT, "
            + COL_PHONE + " TEXT"
            + ");";

    public HelloAppDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_PROVINCE);
            db.execSQL(CREATE_TABLE_DISTRICT);
            db.execSQL(CREATE_TABLE_DIRECTION);
            db.execSQL(CREATE_TABLE_PRODUCT);

        } catch (SQLiteException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrade Tables");
            db.execSQL("DROP TABLE " + TABLE_PROVINCE + " IF EXISTS;");
            db.execSQL("DROP TABLE " + TABLE_DISTRICT + " IF EXISTS;");
            db.execSQL("DROP TABLE " + TABLE_DIRECTION + " IF EXISTS;");
            db.execSQL("DROP TABLE " + TABLE_PRODUCT + " IF EXISTS;");

            onCreate(db);
        } catch (SQLiteException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}
