package com.gomicorp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 3/31/2016.
 */
public class HelloAppDb {

    private HelloAppDbHelper dbHelper;
    private SQLiteDatabase db;

    public HelloAppDb(Context context) {
        this.dbHelper = new HelloAppDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    private void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }

    public void insertProvince(List<Province> provinces, boolean clearPrevious) {
        if (clearPrevious)
            deleteTable(dbHelper.TABLE_PROVINCE);

        String sql = "INSERT INTO " + dbHelper.TABLE_PROVINCE + " VALUES (?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();

        for (Province obj : provinces) {
            statement.clearBindings();

            statement.bindLong(1, obj.getId());
            statement.bindString(2, obj.getName());
            statement.bindString(3, obj.getPostalCode());

            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Province> getProvinceList() {
        List<Province> provinces = new ArrayList<>();

        String[] cols = {dbHelper.COL_ID, dbHelper.COL_NAME, dbHelper.COL_POSTAL_CODE};

        Cursor cursor = db.query(dbHelper.TABLE_PROVINCE, cols, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COL_ID));
                String name = cursor.getString(cursor.getColumnIndex(dbHelper.COL_NAME));
                String postCode = cursor.getString(cursor.getColumnIndex(dbHelper.COL_POSTAL_CODE));

                provinces.add(new Province(id, name, postCode));
            } while (cursor.moveToNext());
        }

        return provinces;
    }

    public void insertDistrict(List<District> districts, boolean clearPrevious) {
        if (clearPrevious)
            deleteTable(dbHelper.TABLE_DISTRICT);

        String sql = "INSERT INTO " + dbHelper.TABLE_DISTRICT + " VALUES (?,?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();

        for (District obj : districts) {
            statement.clearBindings();

            statement.bindLong(1, obj.getId());
            statement.bindString(2, obj.getName());
            statement.bindDouble(3, obj.getLatitude());
            statement.bindDouble(4, obj.getLongitude());
            statement.bindLong(5, obj.getProvinceID());

            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<District> getDistrictList(int provinceID) {
        List<District> districts = new ArrayList<>();

        String[] cols = {dbHelper.COL_ID, dbHelper.COL_NAME, dbHelper.COL_LAT, dbHelper.COL_LNG, dbHelper.COL_PROV_ID};

        Cursor cursor = db.query(dbHelper.TABLE_DISTRICT, cols, dbHelper.COL_PROV_ID + "=?", new String[]{String.valueOf(provinceID)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COL_ID));
                String name = cursor.getString(cursor.getColumnIndex(dbHelper.COL_NAME));
                double lat = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(dbHelper.COL_LNG));
                int provID = cursor.getInt(cursor.getColumnIndex(dbHelper.COL_PROV_ID));

                districts.add(new District(id, name, lat, lng, provID));
            } while (cursor.moveToNext());
        }

        return districts;
    }

    public void insertDirection(List<Info> directions, boolean clearPrevious) {
        if (clearPrevious)
            deleteTable(dbHelper.TABLE_DIRECTION);

        String sql = "INSERT INTO " + dbHelper.TABLE_DIRECTION + " VALUES (?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();

        for (Info obj : directions) {
            statement.clearBindings();

            statement.bindLong(1, obj.getId());
            statement.bindString(2, obj.getName());

            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Info> getDirectionList() {
        List<Info> directions = new ArrayList<>();

        String[] cols = {dbHelper.COL_ID, dbHelper.COL_NAME};

        Cursor cursor = db.query(dbHelper.TABLE_DIRECTION, cols, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COL_ID));
                String name = cursor.getString(cursor.getColumnIndex(dbHelper.COL_NAME));

                directions.add(new Info(id, name));
            } while (cursor.moveToNext());
        }

        return directions;
    }

    public void insertProduct(Product product) {
        if (getProductByID(product.getId()) != null)
            return;

        String sql = "INSERT INTO " + dbHelper.TABLE_PRODUCT + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        db.beginTransaction();

        statement.clearBindings();

        statement.bindString(1, String.valueOf(product.getId()));
        statement.bindString(2, String.valueOf(product.getThumbnail()));
        statement.bindString(3, String.valueOf(product.getPrice()));
        statement.bindString(4, String.valueOf(product.getGrossFloorArea()));
        statement.bindString(5, product.getTitle());
        statement.bindString(6, product.getAddresss());
        statement.bindString(7, product.getContactName());
        statement.bindString(8, product.getContactPhone());

        statement.execute();

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Product> getProductList() {
        List<Product> products = new ArrayList<>();

        String[] cols = {dbHelper.COL_ID, dbHelper.COL_THUMB, dbHelper.COL_PRICE, dbHelper.COL_AREA, dbHelper.COL_TITLE, dbHelper.COL_ADDRESS, dbHelper.COL_NAME, dbHelper.COL_PHONE};

        Cursor cursor = db.query(dbHelper.TABLE_PRODUCT, cols, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ID));
                String thumb = cursor.getString(cursor.getColumnIndex(dbHelper.COL_THUMB));
                String price = cursor.getString(cursor.getColumnIndex(dbHelper.COL_PRICE));
                String area = cursor.getString(cursor.getColumnIndex(dbHelper.COL_AREA));
                String title = cursor.getString(cursor.getColumnIndex(dbHelper.COL_TITLE));
                String address = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ADDRESS));
                String name = cursor.getString(cursor.getColumnIndex(dbHelper.COL_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(dbHelper.COL_PHONE));

                products.add(new Product(Long.parseLong(id), thumb, address, Double.parseDouble(price), Double.parseDouble(area), title, name, phone));

            } while (cursor.moveToNext());
        }
        return products;
    }

    public Product getProductByID(long productID) {
        String[] cols = {dbHelper.COL_ID, dbHelper.COL_THUMB, dbHelper.COL_PRICE, dbHelper.COL_AREA, dbHelper.COL_TITLE, dbHelper.COL_ADDRESS, dbHelper.COL_NAME, dbHelper.COL_PHONE};

        Cursor cursor = db.query(dbHelper.TABLE_PRODUCT, cols, dbHelper.COL_ID + "=?", new String[]{String.valueOf(productID)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ID));
            String thumb = cursor.getString(cursor.getColumnIndex(dbHelper.COL_THUMB));
            String price = cursor.getString(cursor.getColumnIndex(dbHelper.COL_PRICE));
            String area = cursor.getString(cursor.getColumnIndex(dbHelper.COL_AREA));
            String title = cursor.getString(cursor.getColumnIndex(dbHelper.COL_TITLE));
            String address = cursor.getString(cursor.getColumnIndex(dbHelper.COL_ADDRESS));
            String name = cursor.getString(cursor.getColumnIndex(dbHelper.COL_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(dbHelper.COL_PHONE));

            return new Product(Long.parseLong(id), thumb, address, Double.parseDouble(price), Double.parseDouble(area), title, name, phone);
        }

        return null;
    }

    public boolean deleteProductByID(long productID) {
        return db.delete(dbHelper.TABLE_PRODUCT, dbHelper.COL_ID + " = " + productID, null) > 0;
    }
}
