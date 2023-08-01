package vn.hellosoft.hellorent.extras;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class EndPoints {

    public static final String BASE_URL = "http://hellorentapi.gomimall.vn/";

    public static final String URL_CREATE_DEVICE = BASE_URL + "api/Device/CreateDevice";

    // TODO: EndPoints Location
    private static final String API_LOCATION = BASE_URL + "api/Location/";
    public static final String URL_LIST_PROVINCE = API_LOCATION + "ListProvince/CountryID={countryID}/";
    public static final String URL_LIST_DISTRICT = API_LOCATION + "ListDistrict/ProvinceID={provinceID}/";
    public static final String URL_ATTRACTION_BY_LOCATION = API_LOCATION + "ListAttractionByLocation/Latitude={lat}/Longitude={lng}/NumItems={numItems}/Language={language}/";
    public static final String URL_MARKER_BY_KEYWORD = API_LOCATION + "ListMarkerByKeyword";
    public static final String URL_BUILDING_BY_DIST = API_LOCATION + "ListBuildingByDistrict/DistrictID={districtID}/Language={language}/";
    public static final String URL_UNIVERSITY_KOREA = API_LOCATION + "ListUniversityInKorea/NumItems={numItems}/Language={language}/";


    // TODO: EndPoint Account
    private static final String API_ACCOUNT = BASE_URL + "api/Account/";
    public static final String URL_VERIFY_USER = API_ACCOUNT + "Verify/UserName={userName}/";
    public static final String MEMBER_REG = API_ACCOUNT + "MemberRegistration";
    public static final String ACC_LOGIN = API_ACCOUNT + "AccountLogin";
    public static final String SOCIAL_LOGIN = API_ACCOUNT + "SocialLogin";
    public static final String UPDATE_PHONE = API_ACCOUNT + "UpdatePhoneNumber/UserID={userID}/AccountID={accountID}/PhoneNumber={phoneNumber}/";
    public static final String GET_DETAILS = API_ACCOUNT + "GetDetails/AccountID={accountID}/";
    public static final String CHANGE_AVATAR = API_ACCOUNT + "ChangeAvatar";
    public static final String UPDATE_INFO = API_ACCOUNT + "UpdateInfo";
    public static final String CHANGE_PWD = API_ACCOUNT + "ChangePassword/AccountID={accountID}/Password={password}/";


    // TODO: EndPoint Utilities
    private static final String API_UTIL = BASE_URL + "api/Utilities/";
    public static final String URL_LIST_PROPERTY = API_UTIL + "ListProperty/LanguageType={language}/";
    public static final String URL_LIST_FEATURE = API_UTIL + "ListFeature/LanguageType={language}/";
    public static final String URL_LIST_FURNITURE = API_UTIL + "ListFurniture/LanguageType={language}/";
    public static final String URL_LIST_DIRECTION = API_UTIL + "ListDirection/LanguageType={language}/";
    public static final String URL_LIST_ADV_MAIN = API_UTIL + "ListMainBanner";
    public static final String URL_LIST_POWER_LINK = API_UTIL + "ListPowerLink/ProvinceID={provinceID}/";
    public static final String URL_SEND_WARTNING = API_UTIL + "SendWarning";
    public static final String URL_GET_INFO = API_UTIL + "GetInfoByType/InfoType={type}/LanguageType={language}/";
    //TODO: GIFT CARD
    public static final String URL_GET_GIFT_CARD = API_UTIL + "GetGiftCard/AccountID={accountID}/";
    public static final String URL_UPDATE_ACC_GIFT = API_UTIL + "UpdateAccountGift";


    // TODO: EndPoint Product
    private static final String API_PRODUCT = BASE_URL + "api/Product/";
    public static final String URL_CREATE_PRODUCT = API_PRODUCT + "Create";
    public static final String URL_SEARCH_PRODUCT = API_PRODUCT + "Search";
    public static final String URL_GET_PRODUCT = API_PRODUCT + "GetByID";
    public static final String URL_SEARCH_BY_ACCOUNT = API_PRODUCT + "SearchByAccount";
    public static final String URL_FAVORITE_PRODUCT = API_PRODUCT + "Favorite";
    public static final String URL_LIST_USER_LIKE = API_PRODUCT + "ListUserLike/UserID={userID}/Language={language}/";
    public static final String URL_UPDATE_NOTE = API_PRODUCT + "UpdateNote";
    public static final String URL_UPDATE_STATUS = API_PRODUCT + "UpdateProductStatus";
    public static final String URL_UPDATE_INFO = API_PRODUCT + "UpdateInfo";


    // TODO: EndPoint Notify
    private static final String API_NOTIFY = BASE_URL + "api/Notify/";
    public static final String URL_NOTIFY_LAUNCHER = API_NOTIFY + "GetMainLauncher/";
}
