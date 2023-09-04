package com.gomicorp.app;

import com.gomicorp.propertyhero.BuildConfig;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class Config {

    public static final boolean USE_V2 = true;
    public static final boolean DISABLE_CREATE = true;
    public static final int LIMITED = 20;
    public static final String DEVICE_TYPE = "1"; // Android
    public static final int VIETNAM = 1;

    public static final float MAP_ZOOM = 15;

    public static final int MIN_PIC = 5;
    public static final int MAX_PIC = 20;
    public static final String PIC_TEXT = "(.../" + MAX_PIC + ")";

    public static final int MIN_TITLE = 18;
    public static final int MAX_TITLE = 180;
    public static final String TITLE_TEXT = ".../" + MAX_TITLE;

    public static final int MIN_CONTENT = 30;
    public static final int MAX_CONTENT = 3000;
    public static final String CONTENT_TEXT = ".../" + MAX_CONTENT;

    public static final int VIEW_PROGRESS = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_HEADER = 2;

    public static final int IMAGE_MAX_SIZE = 1024;
    public static final int TIMER_DELAY = 880;

    // Location updates intervals in sec
    public static final int UPDATE_INTERVAL = 10000; // 10 sec
    public static final int FASTEST_INTERVAL = 5000;
    public static final int DISPLACEMENT = 10;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PERMS_REQUEST = 8000;

    public static final int UNDEFINED = 254;

    public static final int PROPERTY_HOUSE = 0;
    public static final int PROPERTY_APARTMENT = 1;
    public static final int PROPERTY_ROOM = 2;

    // TODO: PRODUCT STATUS
    public static final int PRODUCT_NEW = 0;
    public static final int PRODUCT_ACTIVATED = 1;
    public static final int PRODUCT_CERTIFIED = 2;
    public static final int PRODUCT_END_CERTIFIED = 3;
    public static final int PRODUCT_COMPLETED = 4;
    public static final int PRODUCT_FAILED = 5;
    public static final int PRODUCT_DELETE = 250;


    public static final int INFO_TERMS = 0;
    public static final int INFO_PRIVACY = 1;

    // Account Type
    public static final int HELLO_RENT = 0;
    public static final int ACC_FACEBOOK = 1;
    public static final int ACC_GOOGLE = 2;

    // Account Status
    public static final int ACC_LOCKED = 200;
    public static final int ACC_DELETION = 250;

    // Marker Type
    public static final int MARKER_BUILDING = 0;
    public static final int MARKER_ATTR = 1;

    // Warning Type
    public static final int WARNING_COMPLETED = 0;
    public static final int WARNING_INFO = 1;

    //Filter KEY
    public static final String KEY_PROPERTY = "Property";
    public static final String KEY_MIN_PRICE = "MinPrice";
    public static final String KEY_MAX_PRICE = "MaxPrice";
    public static final String KEY_MIN_AREA = "MinArea";
    public static final String KEY_MAX_AREA = "MaxArea";
    public static final String KEY_BED = "Beds";
    public static final String KEY_BATH = "Baths";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";

    public static final int PROVINCE_TYPE = 81;
    public static final int DISTRICT_TYPE = 82;
    public static final int PROPERTY_TYPE = 83;
    public static final int MARKER_TYPE = 84;
    public static final int DIRECTION_TYPE = 85;
    public static final int FEATURE_TYPE = 86;
    public static final int FURNITURE_TYPE = 87;

    public static final int REQUEST_LOGIN = 810;
    public static final int REQUEST_GALLERY = 811;
    public static final int REQUEST_CAMERA = 812;
    public static final int REQUEST_CROPPING = 813;
    public static final int REQUEST_FILTER = 814;
    public static final int REQUEST_FIND_MARKER = 815;
    public static final int REQUEST_PRODUCT = 816;
    public static final int REQUEST_SEND_EMAIL = 817;

    public static final int FAILURE_RESULT = 0;
    public static final int SUCCESS_RESULT = 1;

    public static final String RECEIVER = "ResultReceiver";
    public static final String RESULT_DATA = "ResultData";

    public static final String DATA_EXTRA = "DataExtra";
    public static final String DATA_TYPE = "DataType";
    public static final String STRING_DATA = "StringData";
    public static final String PARCELABLE_DATA = "ParcelableData";
    public static final String ADDRESS_DATA = "AddressData";
    public static final String IMAGE_LIST = "ImageList";
    public static final String AVATAR_URL = "AvatarUrl";
    public static final String ACCOUNT_TYPE = "AccountType";
    public static final String PRODUCT_ID = "ProductID";
    public static final String STATUS_DATA = "StatusData";

    public static final boolean DEBUG = BuildConfig.DEBUG;
}
