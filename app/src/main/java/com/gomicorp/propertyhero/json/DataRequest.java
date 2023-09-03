package com.gomicorp.propertyhero.json;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.callbacks.OnLoadDistrictListener;
import com.gomicorp.propertyhero.callbacks.OnLoadFeatureListener;
import com.gomicorp.propertyhero.callbacks.OnLoadInfoListener;
import com.gomicorp.propertyhero.callbacks.OnLoadMarkerListener;
import com.gomicorp.propertyhero.callbacks.OnLoadPropertyListener;
import com.gomicorp.propertyhero.callbacks.OnLoadProvinceListener;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.model.District;
import com.gomicorp.propertyhero.model.Info;
import com.gomicorp.propertyhero.model.Property;
import com.gomicorp.propertyhero.model.Province;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by CTO-HELLOSOFT on 4/2/2016.
 */
public class DataRequest {

    private static final String TAG = DataRequest.class.getSimpleName();

    public static void cancelRequest() {
        AppController.getInstance().cancelPedingRequesrs(TAG);
    }

    public static void provinceList(final OnLoadProvinceListener listener) {
        List<Province> provinceList = AppController.getInstance().getWritableDb().getProvinceList();

        if (provinceList.size() > 0) {
            listener.onSuccess(provinceList);
        } else {
            String url = EndPoints.URL_LIST_PROVINCE
                    .replace(UrlParams.COUNTRY_ID, String.valueOf(Config.VIETNAM));
            JsonObjectRequest reqProvince = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    List<Province> provinces = Parser.provinceList(response);
                    if (provinces.size() > 0) {
                        listener.onSuccess(provinces);
                        AppController.getInstance().getWritableDb().insertProvince(provinces, true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });

            AppController.getInstance().addToRequestQueue(reqProvince, TAG);
        }
    }

    public static void districtList(int provinceID, final OnLoadDistrictListener listener) {
        List<District> districtList = AppController.getInstance().getWritableDb().getDistrictList(provinceID);

        if (districtList.size() > 0) {
            listener.onSuccess(districtList);
        } else {
            String url = EndPoints.URL_LIST_DISTRICT
                    .replace(UrlParams.PROVINCE_ID, String.valueOf(provinceID));
            JsonObjectRequest reqDistrict = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    List<District> districts = Parser.districtList(response);
                    if (districts.size() > 0) {
                        listener.onSuccess(districts);
                        AppController.getInstance().getWritableDb().insertDistrict(districts, false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });

            AppController.getInstance().addToRequestQueue(reqDistrict, TAG);
        }
    }

    public static void propertyList_V2(final OnLoadPropertyListener listener) {

        String url = EndPoints.URL_LIST_PROPERTY_V2;

        JsonObjectRequest reqCate = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Property> categories = Parser.propertyList_V2(response);
                if (categories.size() > 0) {
                    listener.onSuccess(categories);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqCate, TAG);
    }

    public static void propertyList(final OnLoadPropertyListener listener) {

        String url = EndPoints.URL_LIST_PROPERTY.replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqCate = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Property> categories = Parser.propertyList(response);
                if (categories.size() > 0) {
                    listener.onSuccess(categories);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqCate, TAG);
    }

    public static void buildingByDistrict(int distID, final OnLoadMarkerListener listener) {

        String url = EndPoints.URL_BUILDING_BY_DIST
                .replace(UrlParams.DISTRICT_ID, String.valueOf(distID))
                .replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqBuilding = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.markerList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqBuilding, TAG);
    }

    public static void featureList(final OnLoadFeatureListener listener) {

        String url = EndPoints.URL_LIST_FEATURE.replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqFeature = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.featureList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqFeature, TAG);
    }

    public static void furnitureList(final OnLoadFeatureListener listener) {

        String url = EndPoints.URL_LIST_FURNITURE.replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

        JsonObjectRequest reqFurniture = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.featureList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqFurniture, TAG);
    }

    public static void directionList(final OnLoadInfoListener listener) {
        List<Info> infos = AppController.getInstance().getWritableDb().getDirectionList();
        if (infos.size() > 0)
            listener.onSuccess(infos);
        else {

            String url = EndPoints.URL_LIST_DIRECTION.replace(UrlParams.LANGUAGE_TYPE, String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

            JsonObjectRequest reqDirection = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    List<Info> infos = Parser.infoList(response);
                    AppController.getInstance().getWritableDb().insertDirection(infos, true);
                    listener.onSuccess(infos);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });

            AppController.getInstance().addToRequestQueue(reqDirection, TAG);
        }
    }

    public static void findByKeyword(String keyword, int provinceID, int markerType, final OnLoadMarkerListener listener) {

        MultipartRequest reqMarkerByKeyword = new MultipartRequest(EndPoints.URL_MARKER_BY_KEYWORD, null, Utils.mimeType, pathBodyFindByKeyWord(keyword, provinceID, markerType), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (Config.DEBUG)
                    Log.e("findByKeyword", "onResponse: " + new Gson().toJson(response));
                listener.onSuccess(Parser.markerList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqMarkerByKeyword, TAG);
    }

    private static byte[] pathBodyFindByKeyWord(String keyword, int provinceID, int markerType) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "Keyword", Utils.iso88951_To_utf8(keyword));
            Utils.buildTextPart(dos, "ProvinceID", String.valueOf(provinceID));
            Utils.buildTextPart(dos, "MarkerType", String.valueOf(markerType));
            Utils.buildTextPart(dos, "LanguageType", String.valueOf(AppController.getInstance().getPrefManager().getLanguageType()));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);

            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
