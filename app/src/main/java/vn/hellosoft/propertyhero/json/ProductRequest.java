package vn.hellosoft.propertyhero.json;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.propertyhero.callbacks.OnLoadProductListener;
import vn.hellosoft.propertyhero.callbacks.OnResponseListener;
import vn.hellosoft.propertyhero.extras.EndPoints;
import vn.hellosoft.propertyhero.model.Product;
import vn.hellosoft.propertyhero.model.SearchInfo;
import vn.hellosoft.helper.MultipartRequest;

/**
 * Created by CTO-HELLOSOFT on 4/20/2016.
 */
public class ProductRequest {

    private static final String TAG = ProductRequest.class.getSimpleName();

    public static void cancelRequest() {
        AppController.getInstance().cancelPedingRequesrs(TAG);
    }

    public static void create(Product product, List<String> imageList, final OnResponseListener listener) {
        MultipartRequest reqCreate = new MultipartRequest(EndPoints.URL_CREATE_PRODUCT, null, Utils.mimeType, pathBodyCreate(product, imageList), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.responseInfo(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        reqCreate.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(reqCreate, TAG);

    }

    private static byte[] pathBodyCreate(Product product, List<String> imageList) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "Address", Utils.iso88951_To_utf8(product.getAddresss()));
            Utils.buildTextPart(dos, "Latitude", String.valueOf(product.getLatitude()));
            Utils.buildTextPart(dos, "Longitude", String.valueOf(product.getLongitude()));
            Utils.buildTextPart(dos, "CountryID", String.valueOf(Config.VIETNAM));
            Utils.buildTextPart(dos, "ProvinceID", String.valueOf(product.getProvinceID()));
            Utils.buildTextPart(dos, "DistrictID", String.valueOf(product.getDistrictID()));
            Utils.buildTextPart(dos, "PropertyID", String.valueOf(product.getPropertyID()));
            Utils.buildTextPart(dos, "BuildingID", String.valueOf(product.getBuildingID()));
            Utils.buildTextPart(dos, "Deposit", String.valueOf(product.getDeposit()));
            Utils.buildTextPart(dos, "Price", String.valueOf(product.getPrice()));
            Utils.buildTextPart(dos, "Floor", String.valueOf(product.getFloor()));
            Utils.buildTextPart(dos, "FloorCount", String.valueOf(product.getFloorCount()));
            Utils.buildTextPart(dos, "SiteArea", String.valueOf(product.getSiteArea()));
            Utils.buildTextPart(dos, "GrossFloorArea", String.valueOf(product.getGrossFloorArea()));
            Utils.buildTextPart(dos, "Bedroom", String.valueOf(product.getBedroom()));
            Utils.buildTextPart(dos, "Bathroom", String.valueOf(product.getBathroom()));
            Utils.buildTextPart(dos, "DirectionID", String.valueOf(product.getDirectionID()));
            Utils.buildTextPart(dos, "ServiceFee", String.valueOf(product.getServiceFee()));
            Utils.buildTextPart(dos, "FeatureList", product.getFeatureList());
            Utils.buildTextPart(dos, "FurnitureList", product.getFurnitureList());
            Utils.buildTextPart(dos, "Elevator", String.valueOf(product.getElevator()));
            Utils.buildTextPart(dos, "Pets", String.valueOf(product.getPets()));
            Utils.buildTextPart(dos, "NumberPerson", String.valueOf(product.getNumPerson()));
            Utils.buildTextPart(dos, "Title", Utils.iso88951_To_utf8(product.getTitle()));
            Utils.buildTextPart(dos, "Content", Utils.iso88951_To_utf8(product.getContent()));
            Utils.buildTextPart(dos, "Note", Utils.iso88951_To_utf8(product.getNote()));
            Utils.buildTextPart(dos, "ContactName", Utils.iso88951_To_utf8(product.getContactName()));
            Utils.buildTextPart(dos, "ContactPhone", Utils.iso88951_To_utf8(product.getContactPhone()));
            Utils.buildTextPart(dos, "AccountID", String.valueOf(product.getAccountID()));
            Utils.buildTextPart(dos, "AccountRole", String.valueOf(AppController.getInstance().getPrefManager().getAccountRole()));

            int i = 0;
            for (String path : imageList) {
                i++;
                byte[] bytes = vn.hellosoft.helper.Utils.getBytesBitmap(path, Config.IMAGE_MAX_SIZE, Config.IMAGE_MAX_SIZE);
                if (bytes != null)
                    Utils.buildPart(dos, bytes, "Image" + i + ".jpg");
            }

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void search(SearchInfo searchInfo, final OnLoadProductListener listener) {

        MultipartRequest reqSearch = new MultipartRequest(EndPoints.URL_SEARCH_PRODUCT, null, Utils.mimeType, pathBodySearch(searchInfo), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.productList(response), (int) Parser.totalRows(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqSearch, TAG);
    }

    private static byte[] pathBodySearch(SearchInfo searchInfo) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "StartLat", searchInfo.getStartLat());
            Utils.buildTextPart(dos, "StartLng", searchInfo.getStartLng());
            Utils.buildTextPart(dos, "EndLat", searchInfo.getEndLat());
            Utils.buildTextPart(dos, "EndLng", searchInfo.getEndLng());
            Utils.buildTextPart(dos, "Distance", searchInfo.getDistance());
            Utils.buildTextPart(dos, "PropertyType", searchInfo.getPropertyType());
            Utils.buildTextPart(dos, "PropertyID", searchInfo.getPropertyID());
            Utils.buildTextPart(dos, "MinPrice", searchInfo.getMinPrice());
            Utils.buildTextPart(dos, "MaxPrice", searchInfo.getMaxPrice());
            Utils.buildTextPart(dos, "MinArea", searchInfo.getMinArea());
            Utils.buildTextPart(dos, "MaxArea", searchInfo.getMaxArea());
            Utils.buildTextPart(dos, "Bedroom", searchInfo.getBed());
            Utils.buildTextPart(dos, "Bathroom", searchInfo.getBath());
            Utils.buildTextPart(dos, "Status", searchInfo.getStatus());
            Utils.buildTextPart(dos, "PageNo", searchInfo.getPageNo());
            Utils.buildTextPart(dos, "LanguageType", String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getProduct(long id, int isMeViewThis, final OnLoadProductListener listener) {
        MultipartRequest reqGet = new MultipartRequest(EndPoints.URL_GET_PRODUCT, null, Utils.mimeType, pathBodyRequestInfo(id, isMeViewThis), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.productList(response), 1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqGet, TAG);
    }

    public static void favorite(long id, final OnResponseListener listener) {
        MultipartRequest reqFavorite = new MultipartRequest(EndPoints.URL_FAVORITE_PRODUCT, null, Utils.mimeType, pathBodyRequestInfo(id, 0), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.responseInfo(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqFavorite, TAG);
    }

    private static byte[] pathBodyRequestInfo(long id, int isMeViewThis) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "ProductID", String.valueOf(id));
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            Utils.buildTextPart(dos, "IsMeViewThis", String.valueOf(isMeViewThis));
            Utils.buildTextPart(dos, "LanguageType", String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void searchByAccount(int status, String keyword, int pageNo, final OnLoadProductListener listener) {
        MultipartRequest reqSearchByAccount = new MultipartRequest(EndPoints.URL_SEARCH_BY_ACCOUNT, null, Utils.mimeType, pathBodySearchByAccount(status, keyword, pageNo), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.productList(response), (int) Parser.totalRows(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqSearchByAccount, TAG);
    }

    private static byte[] pathBodySearchByAccount(int status, String keyword, int pageNo) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            Utils.buildTextPart(dos, "Status", String.valueOf(status));
            Utils.buildTextPart(dos, "Keyword", Utils.iso88951_To_utf8(keyword));
            Utils.buildTextPart(dos, "PageNo", String.valueOf(pageNo));
            Utils.buildTextPart(dos, "LanguageType", String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateNote(long productID, String note, final OnResponseListener listener) {
        MultipartRequest reqUpdateNote = new MultipartRequest(EndPoints.URL_UPDATE_NOTE, null, Utils.mimeType, pathBodyUpdate(productID, note, 0), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.responseInfo(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqUpdateNote, TAG);
    }

    public static void updateStatus(long productID, int status, final OnResponseListener listener) {
        MultipartRequest reqUpdateNote = new MultipartRequest(EndPoints.URL_UPDATE_STATUS, null, Utils.mimeType, pathBodyUpdate(productID, "", status), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.responseInfo(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqUpdateNote, TAG);
    }

    private static byte[] pathBodyUpdate(long productID, String note, int status) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "ProductID", String.valueOf(productID));
            Utils.buildTextPart(dos, "AccountID", String.valueOf(AppController.getInstance().getPrefManager().getUserID()));
            Utils.buildTextPart(dos, "Status", String.valueOf(status));
            Utils.buildTextPart(dos, "Note", Utils.iso88951_To_utf8(note));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateInfo(Product product, final OnResponseListener listener) {
        MultipartRequest reqUodateInfo = new MultipartRequest(EndPoints.URL_UPDATE_INFO, null, Utils.mimeType, pathBodyUpdateInfo(product), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.responseInfo(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        AppController.getInstance().addToRequestQueue(reqUodateInfo, TAG);
    }

    private static byte[] pathBodyUpdateInfo(Product product) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "Address", Utils.iso88951_To_utf8(product.getAddresss()));
            Utils.buildTextPart(dos, "Latitude", String.valueOf(product.getLatitude()));
            Utils.buildTextPart(dos, "Longitude", String.valueOf(product.getLongitude()));
            Utils.buildTextPart(dos, "ProvinceID", String.valueOf(product.getProvinceID()));
            Utils.buildTextPart(dos, "DistrictID", String.valueOf(product.getDistrictID()));
            Utils.buildTextPart(dos, "PropertyID", String.valueOf(product.getPropertyID()));
            Utils.buildTextPart(dos, "BuildingID", String.valueOf(product.getBuildingID()));
            Utils.buildTextPart(dos, "Deposit", String.valueOf(product.getDeposit()));
            Utils.buildTextPart(dos, "Price", String.valueOf(product.getPrice()));
            Utils.buildTextPart(dos, "Floor", String.valueOf(product.getFloor()));
            Utils.buildTextPart(dos, "FloorCount", String.valueOf(product.getFloorCount()));
            Utils.buildTextPart(dos, "SiteArea", String.valueOf(product.getSiteArea()));
            Utils.buildTextPart(dos, "GrossFloorArea", String.valueOf(product.getGrossFloorArea()));
            Utils.buildTextPart(dos, "Bedroom", String.valueOf(product.getBedroom()));
            Utils.buildTextPart(dos, "Bathroom", String.valueOf(product.getBathroom()));
            Utils.buildTextPart(dos, "DirectionID", String.valueOf(product.getDirectionID()));
            Utils.buildTextPart(dos, "ServiceFee", String.valueOf(product.getServiceFee()));
            Utils.buildTextPart(dos, "FeatureList", product.getFeatureList());
            Utils.buildTextPart(dos, "FurnitureList", product.getFurnitureList());
            Utils.buildTextPart(dos, "Elevator", String.valueOf(product.getElevator()));
            Utils.buildTextPart(dos, "Pets", String.valueOf(product.getPets()));
            Utils.buildTextPart(dos, "NumberPerson", String.valueOf(product.getNumPerson()));
            Utils.buildTextPart(dos, "Title", Utils.iso88951_To_utf8(product.getTitle()));
            Utils.buildTextPart(dos, "Content", Utils.iso88951_To_utf8(product.getContent()));
            Utils.buildTextPart(dos, "ContactName", Utils.iso88951_To_utf8(product.getContactName()));
            Utils.buildTextPart(dos, "ContactPhone", Utils.iso88951_To_utf8(product.getContactPhone()));
            Utils.buildTextPart(dos, "AccountID", String.valueOf(product.getAccountID()));
            Utils.buildTextPart(dos, "ProductID", String.valueOf(product.getId()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
