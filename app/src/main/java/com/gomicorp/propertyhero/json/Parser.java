package com.gomicorp.propertyhero.json;

import com.gomicorp.app.Config;
import com.gomicorp.helper.Constants;
import com.gomicorp.propertyhero.model.Account;
import com.gomicorp.propertyhero.model.Advertising;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Feature;
import com.gomicorp.propertyhero.model.GiftCard;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.Notify;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.Property;
import com.gomicorp.propertyhero.model.Province;
import com.gomicorp.propertyhero.model.ResponseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class Parser {

    public static ResponseInfo responseInfo(JSONObject response) {
        ResponseInfo info = null;

        if (response != null && response.length() > 0) {
            try {
                boolean success = false;
                long data = -1;

                if (Utils.keyContains(response, Keys.DATA))
                    success = response.getBoolean(Keys.DATA);

                if (Utils.keyContains(response, Keys.TOTAL_ROWS))
                    data = response.getLong(Keys.TOTAL_ROWS);

                info = new ResponseInfo(success, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return info;
    }

    public static long totalRows(JSONObject response) {
        long rows = 0;

        try {
            if (Utils.keyContains(response, Keys.TOTAL_ROWS))
                rows = response.getLong(Keys.TOTAL_ROWS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public static List<Province> provinceList(JSONObject response) {
        List<Province> provinces = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                String postCode = Constants.NA;

                JSONObject obj = jsonArray.getJSONObject(i);
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);
                if (Utils.keyContains(obj, Keys.POST_CODE))
                    postCode = obj.getString(Keys.POST_CODE);

                provinces.add(new Province(id, name, postCode));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return provinces;
    }

    public static List<District> districtList(JSONObject response) {
        List<District> districts = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                double lat = -1;
                double lng = -1;
                int provinceID = -1;

                JSONObject obj = jsonArray.getJSONObject(i);
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);
                if (Utils.keyContains(obj, Keys.LAT))
                    lat = obj.getDouble(Keys.LAT);
                if (Utils.keyContains(obj, Keys.LNG))
                    lng = obj.getDouble(Keys.LNG);
                if (Utils.keyContains(obj, Keys.PROVINCE_ID))
                    provinceID = obj.getInt(Keys.PROVINCE_ID);

                districts.add(new District(id, name, lat, lng, provinceID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return districts;
    }

    public static List<Account> accountList(JSONObject response) {
        List<Account> accounts = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                long id = -1;
                String userName = Constants.NA;
                String fullName = Constants.NA;
                int gender = -1;
                String birthDate = Constants.NA;
                String phoneNumber = Constants.NA;
                String email = Constants.NA;
                String address = Constants.NA;
                int countryID = -1;
                int provinceID = -1;
                int districtID = -1;
                String avatar = Constants.NA;
                String idCode = Constants.NA;
                String issuedDate = Constants.NA;
                String issuedPlace = Constants.NA;
                int accRole = -1;
                int accType = -1;
                int status = -1;

                JSONObject obj = jsonArray.getJSONObject(i);
                //ID
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getLong(Keys.ID);
                //User Name
                if (Utils.keyContains(obj, Keys.USER_NAME))
                    userName = obj.getString(Keys.USER_NAME);
                //Full Name
                if (Utils.keyContains(obj, Keys.FULL_NAME))
                    fullName = obj.getString(Keys.FULL_NAME);
                //Gender
                if (Utils.keyContains(obj, Keys.GENDER))
                    gender = obj.getInt(Keys.GENDER);
                //Birth Date
                if (Utils.keyContains(obj, Keys.BIRTH_DATE))
                    birthDate = obj.getString(Keys.BIRTH_DATE);
                //Phone Number
                if (Utils.keyContains(obj, Keys.PHONE))
                    phoneNumber = obj.getString(Keys.PHONE);
                //Email
                if (Utils.keyContains(obj, Keys.EMAIL))
                    email = obj.getString(Keys.EMAIL);
                //Addr
                if (Utils.keyContains(obj, Keys.ADDR))
                    address = obj.getString(Keys.ADDR);
                //Country
                if (Utils.keyContains(obj, Keys.COUNTRY_ID))
                    countryID = obj.getInt(Keys.COUNTRY_ID);
                //Province
                if (Utils.keyContains(obj, Keys.PROVINCE_ID))
                    provinceID = obj.getInt(Keys.PROVINCE_ID);
                //District
                if (Utils.keyContains(obj, Keys.DISTRICT_ID))
                    districtID = obj.getInt(Keys.DISTRICT_ID);
                //Avatar
                if (Utils.keyContains(obj, Keys.AVATAR))
                    avatar = obj.getString(Keys.AVATAR);
                //ID CODE
                if (Utils.keyContains(obj, Keys.ID_CODE))
                    idCode = obj.getString(Keys.ID_CODE);
                //ISS Date
                if (Utils.keyContains(obj, Keys.ISS_DATE))
                    issuedDate = obj.getString(Keys.ISS_DATE);
                //ISS Place
                if (Utils.keyContains(obj, Keys.ISS_PLATE))
                    issuedPlace = obj.getString(Keys.ISS_PLATE);
                //Acc Role
                if (Utils.keyContains(obj, Keys.ACC_ROLE))
                    accRole = obj.getInt(Keys.ACC_ROLE);
                //Acc Type
                if (Utils.keyContains(obj, Keys.ACC_TYPE))
                    accType = obj.getInt(Keys.ACC_TYPE);
                //Status
                if (Utils.keyContains(obj, Keys.STATUS))
                    status = obj.getInt(Keys.STATUS);

                accounts.add(new Account(id, userName, fullName, gender, com.gomicorp.helper.Utils.strToDate(birthDate), phoneNumber, email, address, countryID, provinceID, districtID,
                        avatar, idCode, com.gomicorp.helper.Utils.strToDate(issuedDate), issuedPlace, accRole, accType, status));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    public static List<Property> propertyList(JSONObject response) {
        List<Property> categories = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                int type = -1;

                JSONObject obj = jsonArray.getJSONObject(i);
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);
                if (Utils.keyContains(obj, Keys.TYPE))
                    type = obj.getInt(Keys.TYPE);

                categories.add(new Property(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public static List<Property> propertyList_V2(JSONObject response) {
        List<Property> categories = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject(Keys.DATA);
            JSONArray jsonArray = jsonObject.getJSONArray("options");
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;

                JSONObject obj = jsonArray.getJSONObject(i);
                if (Utils.keyContains(obj, "id"))
                    id = Integer.parseInt(obj.getString("id"));
                if (Utils.keyContains(obj, "value"))
                    name = obj.getString("value");

                categories.add(new Property(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public static List<Marker> markerList(JSONObject response) {
        List<Marker> markers = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                String addr = Constants.NA;
                double lat = -1;
                double lng = -1;
                String thumb = Constants.NA;
                int fc = -1;

                JSONObject obj = jsonArray.getJSONObject(i);

                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                if (Utils.keyContains(obj, Keys.ADDR))
                    addr = obj.getString(Keys.ADDR);

                if (Utils.keyContains(obj, Keys.LAT))
                    lat = obj.getDouble(Keys.LAT);

                if (Utils.keyContains(obj, Keys.LNG))
                    lng = obj.getDouble(Keys.LNG);

                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                if (Utils.keyContains(obj, Keys.FLOOR_COUNT))
                    fc = obj.getInt(Keys.FLOOR_COUNT);

                markers.add(new Marker(id, name, addr, lat, lng, thumb, fc));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markers;
    }

    public static List<Feature> featureList(JSONObject response) {
        List<Feature> features = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                String thumb = Constants.NA;

                JSONObject obj = jsonArray.getJSONObject(i);

                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                features.add(new Feature(id, name, thumb));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return features;
    }

    public static List<Info> infoList(JSONObject response) {
        List<Info> infos = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String name = Constants.NA;
                String content = Constants.NA;

                JSONObject obj = jsonArray.getJSONObject(i);
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                if (Utils.keyContains(obj, Keys.CONTENT))
                    content = obj.getString(Keys.CONTENT);

                infos.add(new Info(id, name, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return infos;
    }

    public static List<Product> productList_Favorite(JSONObject response) {
        List<Product> products = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {

                long id = -1;
                String addresss = Constants.NA;
                double latitude = -1;
                double longitude = -1;
                int countryID = -1;
                int provinceID = -1;
                int districtID = -1;
                int propertyID = -1;
                int buildingID = -1;
                int directionID = -1;
                String thumbnail = Constants.NA;
                double deposit = -1;
                double price = -1;
                int floor = -1;
                int floorCount = -1;
                double siteArea = -1;
                double grossFloorArea = -1;
                int bedroom = -1;
                int bathroom = -1;
                String propertyName = Constants.NA;
                String directionName = Constants.NA;
                String buildingName = Constants.NA;
                double serviceFee = -1;
                List<Feature> features = new ArrayList<>();
                List<Feature> furnitures = new ArrayList<>();
                byte elevator = -1;
                byte pets = -1;
                int numPerson = -1;
                String title = Constants.NA;
                String content = Constants.NA;
                String note = Constants.NA;
                long accountID = -1;
                int isLike = -1;
                String contactName = Constants.NA;
                String contactPhone = Constants.NA;
                int status = -1;
                int numView = -1;
                int numLike = -1;

                JSONObject obj = jsonArray.getJSONObject(i);
                //Id
                if (Utils.keyContains(obj, "ProductID"))
                    id = obj.getLong("ProductID");
                //Address
                if (Utils.keyContains(obj, Keys.ADDR))
                    addresss = obj.getString(Keys.ADDR);
                // Images
                if (Utils.keyContains(obj, "Avatar"))
                    thumbnail = obj.getString("Avatar");
                // Price
                if (Utils.keyContains(obj, Keys.PRICE))
                    price = obj.getDouble(Keys.PRICE);
                // GFArea
                if (Utils.keyContains(obj, Keys.GFAREA))
                    grossFloorArea = obj.getDouble(Keys.GFAREA);
                // Title
                if (Utils.keyContains(obj, Keys.TITLE))
                    title = obj.getString(Keys.TITLE);
                // Acount ID
                if (Utils.keyContains(obj, Keys.ACC_ID))
                    accountID = obj.getLong(Keys.ACC_ID);
                // Status
                if (Utils.keyContains(obj, Keys.STATUS))
                    status = obj.getInt(Keys.STATUS);

                products.add(new Product(id, addresss, latitude, longitude, countryID, provinceID, districtID, propertyID, buildingID, directionID, thumbnail, deposit, price, floor, floorCount, siteArea, grossFloorArea, bedroom,
                        bathroom, serviceFee, elevator, pets, numPerson, title, content, note, accountID, contactName, contactPhone, status, numView, numLike, isLike, propertyName, directionName, buildingName, features, furnitures));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }

    public static List<Product> productList(JSONObject response) {
        List<Product> products = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {

                long id = -1;
                String addresss = Constants.NA;
                double latitude = -1;
                double longitude = -1;
                int countryID = -1;
                int provinceID = -1;
                int districtID = -1;
                int propertyID = -1;
                int buildingID = -1;
                int directionID = -1;
                String thumbnail = Constants.NA;
                double deposit = -1;
                double price = -1;
                int floor = -1;
                int floorCount = -1;
                double siteArea = -1;
                double grossFloorArea = -1;
                int bedroom = -1;
                int bathroom = -1;
                String propertyName = Constants.NA;
                String directionName = Constants.NA;
                String buildingName = Constants.NA;
                double serviceFee = -1;
                List<Feature> features = new ArrayList<>();
                List<Feature> furnitures = new ArrayList<>();
                byte elevator = -1;
                byte pets = -1;
                int numPerson = -1;
                String title = Constants.NA;
                String content = Constants.NA;
                String note = Constants.NA;
                long accountID = -1;
                int isLike = -1;
                String contactName = Constants.NA;
                String contactPhone = Constants.NA;
                int status = -1;
                int numView = -1;
                int numLike = -1;

                JSONObject obj = jsonArray.getJSONObject(i);
                //Id
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getLong(Keys.ID);
                //Address
                if (Utils.keyContains(obj, Keys.ADDR))
                    addresss = obj.getString(Keys.ADDR);
                // Lat
                if (Utils.keyContains(obj, Keys.LAT))
                    latitude = obj.getDouble(Keys.LAT);
                // Lng
                if (Utils.keyContains(obj, Keys.LNG))
                    longitude = obj.getDouble(Keys.LNG);
                // Country ID
                if (Utils.keyContains(obj, Keys.COUNTRY_ID))
                    countryID = obj.getInt(Keys.COUNTRY_ID);
                // Province ID
                if (Utils.keyContains(obj, Keys.PROVINCE_ID))
                    provinceID = obj.getInt(Keys.PROVINCE_ID);
                // District ID
                if (Utils.keyContains(obj, Keys.DISTRICT_ID))
                    districtID = obj.getInt(Keys.DISTRICT_ID);
                // Property ID
                if (Utils.keyContains(obj, Keys.PROPERTY_ID))
                    propertyID = obj.getInt(Keys.PROPERTY_ID);
                // Building ID
                if (Utils.keyContains(obj, Keys.BUILDING_ID))
                    buildingID = obj.getInt(Keys.BUILDING_ID);
                // Direction ID
                if (Utils.keyContains(obj, Keys.DIRECTION_ID))
                    directionID = obj.getInt(Keys.DIRECTION_ID);
                // Images
                if (Utils.keyContains(obj, Keys.IMAGES))
                    thumbnail = obj.getString(Keys.IMAGES);
                // Deposit
                if (Utils.keyContains(obj, Keys.DEPOSIT))
                    deposit = obj.getDouble(Keys.DEPOSIT);
                // Price
                if (Utils.keyContains(obj, Keys.PRICE))
                    price = obj.getDouble(Keys.PRICE);
                // Floor
                if (Utils.keyContains(obj, Keys.FLOOR))
                    floor = obj.getInt(Keys.FLOOR);
                // Floor Count
                if (Utils.keyContains(obj, Keys.FLOOR_COUNT))
                    floorCount = obj.getInt(Keys.FLOOR_COUNT);
                // Site Area
                if (Utils.keyContains(obj, Keys.SITE_AREA))
                    siteArea = obj.getDouble(Keys.SITE_AREA);
                // GFArea
                if (Utils.keyContains(obj, Keys.GFAREA))
                    grossFloorArea = obj.getDouble(Keys.GFAREA);
                // Bed
                if (Utils.keyContains(obj, Keys.BED))
                    bedroom = obj.getInt(Keys.BED);
                // Bath
                if (Utils.keyContains(obj, Keys.BATH))
                    bathroom = obj.getInt(Keys.BATH);
                // Property Name
                if (Utils.keyContains(obj, Keys.PROPERTY_NAME))
                    propertyName = obj.getString(Keys.PROPERTY_NAME);
                // Direction Name
                if (Utils.keyContains(obj, Keys.DIRECTION_NAME))
                    directionName = obj.getString(Keys.DIRECTION_NAME);
                // Direction Name
                if (Utils.keyContains(obj, Keys.BUILDING_NAME))
                    buildingName = obj.getString(Keys.BUILDING_NAME);
                // Service Fee
                if (Utils.keyContains(obj, Keys.SERVICE_FEE))
                    serviceFee = obj.getDouble(Keys.SERVICE_FEE);
                // Elevator
                if (Utils.keyContains(obj, Keys.ELEVATON))
                    elevator = (byte) (obj.getBoolean(Keys.ELEVATON) ? 1 : 0);
                // PETS
                if (Utils.keyContains(obj, Keys.PET))
                    pets = (byte) (obj.getBoolean(Keys.PET) ? 1 : 0);
                // Num Person
                if (Utils.keyContains(obj, Keys.NUM_PERSON))
                    numPerson = obj.getInt(Keys.NUM_PERSON);
                // Title
                if (Utils.keyContains(obj, Keys.TITLE))
                    title = obj.getString(Keys.TITLE);
                // Content
                if (Utils.keyContains(obj, Keys.CONTENT))
                    content = obj.getString(Keys.CONTENT);
                // Note
                if (Utils.keyContains(obj, Keys.NOTE))
                    note = obj.getString(Keys.NOTE);
                // Acount ID
                if (Utils.keyContains(obj, Keys.ACC_ID))
                    accountID = obj.getLong(Keys.ACC_ID);
                // Is me Like
                if (Utils.keyContains(obj, Keys.IS_LIKE))
                    isLike = obj.getInt(Keys.IS_LIKE);
                // Contact Name
                if (Utils.keyContains(obj, Keys.CONTACT_NAME))
                    contactName = obj.getString(Keys.CONTACT_NAME);
                // Contact Phone
                if (Utils.keyContains(obj, Keys.CONTACT_PHONE))
                    contactPhone = obj.getString(Keys.CONTACT_PHONE);
                // Status
                if (Utils.keyContains(obj, Keys.STATUS))
                    status = obj.getInt(Keys.STATUS);
                // Num View
                if (Utils.keyContains(obj, Keys.NUM_VIEW))
                    numView = obj.getInt(Keys.NUM_VIEW);
                // Num Like
                if (Utils.keyContains(obj, Keys.NUM_LIKE))
                    numLike = obj.getInt(Keys.NUM_LIKE);

                if (Utils.keyContains(obj, Keys.FEATURES))
                    features = productfeatureList(obj, Config.FEATURE_TYPE);

                if (Utils.keyContains(obj, Keys.FURNITURES))
                    furnitures = productfeatureList(obj, Config.FURNITURE_TYPE);


                products.add(new Product(id, addresss, latitude, longitude, countryID, provinceID, districtID, propertyID, buildingID, directionID, thumbnail, deposit, price, floor, floorCount, siteArea, grossFloorArea, bedroom,
                        bathroom, serviceFee, elevator, pets, numPerson, title, content, note, accountID, contactName, contactPhone, status, numView, numLike, isLike, propertyName, directionName, buildingName, features, furnitures));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }

    private static List<Feature> productfeatureList(JSONObject object, int type) {
        List<Feature> features = new ArrayList<>();

        try {
            JSONArray array = (type == Config.FEATURE_TYPE) ? object.getJSONArray(Keys.FEATURES) : object.getJSONArray(Keys.FURNITURES);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int id = -1;
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                String name = Constants.NA;
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                String thumb = Constants.NA;
                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                features.add(new Feature(id, name, thumb));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return features;
    }

    public static List<Advertising> advList(JSONObject response) {
        List<Advertising> advs = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {
                int id = -1;
                String thumb = Constants.NA;
                String img = Constants.NA;
                String title = Constants.NA;
                String desc = Constants.NA;
                String url = Constants.NA;
                String company = Constants.NA;
                String phone = Constants.NA;

                JSONObject obj = jsonArray.getJSONObject(i);

                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                if (Utils.keyContains(obj, Keys.IMG_DETAILS))
                    img = obj.getString(Keys.IMG_DETAILS);

                if (Utils.keyContains(obj, Keys.TITLE))
                    title = obj.getString(Keys.TITLE);

                if (Utils.keyContains(obj, Keys.DESCRIPTION))
                    desc = obj.getString(Keys.DESCRIPTION);

                if (Utils.keyContains(obj, Keys.URL))
                    url = obj.getString(Keys.URL);

                if (Utils.keyContains(obj, Keys.COMPANY_NAME))
                    company = obj.getString(Keys.COMPANY_NAME);

                if (Utils.keyContains(obj, Keys.CONTACT_PHONE))
                    phone = obj.getString(Keys.CONTACT_PHONE);

                advs.add(new Advertising(id, thumb, img, title, desc, url, company, phone));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return advs;
    }

    public static List<Notify> notifyList(JSONObject response) {
        List<Notify> notifies = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = -1;
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                String name = Constants.NA;
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                String content = Constants.NA;
                if (Utils.keyContains(obj, Keys.CONTENT))
                    content = obj.getString(Keys.CONTENT);

                String thumb = Constants.NA;
                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                String url = Constants.NA;
                if (Utils.keyContains(obj, Keys.URL))
                    url = obj.getString(Keys.URL);

                String date = Constants.NA;
                if (Utils.keyContains(obj, Keys.CREATE_DATE))
                    date = obj.getString(Keys.CREATE_DATE);

                notifies.add(new Notify(id, name, content, thumb, url, com.gomicorp.helper.Utils.strToDate(date)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notifies;
    }

    public static List<GiftCard> giftCardList(JSONObject response) {
        List<GiftCard> giftCards = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray(Keys.DATA);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = -1;
                if (Utils.keyContains(obj, Keys.ID))
                    id = obj.getInt(Keys.ID);

                String name = Constants.NA;
                if (Utils.keyContains(obj, Keys.NAME))
                    name = obj.getString(Keys.NAME);

                String content = Constants.NA;
                if (Utils.keyContains(obj, Keys.CONTENT))
                    content = obj.getString(Keys.CONTENT);

                String thumb = Constants.NA;
                if (Utils.keyContains(obj, Keys.THUMB))
                    thumb = obj.getString(Keys.THUMB);

                String image = Constants.NA;
                if (Utils.keyContains(obj, Keys.PIC_CARD))
                    image = obj.getString(Keys.PIC_CARD);

                giftCards.add(new GiftCard(id, name, content, thumb, image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return giftCards;
    }
}
