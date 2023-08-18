package com.gomicorp.propertyhero.json;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.model.Account;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by CTO-HELLOSOFT on 4/7/2016.
 */
public class AccountRequest {

    private static final String TAG = AccountRequest.class.getSimpleName();

    public static void verify(String userName, final OnAccountRequestListener listener) {
        String url = EndPoints.URL_VERIFY_USER
                .replace(UrlParams.USER_NAME, userName);

        JsonObjectRequest reqVerify = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.accountList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqVerify, TAG);
    }

    public static void login(String userName, String pwd, String addr, double lat, double lng, final OnAccountRequestListener listener) {
        MultipartRequest loginReq = new MultipartRequest(EndPoints.ACC_LOGIN, null, Utils.mimeType, accLoginPathBody(userName, pwd, addr, lat, lng), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.accountList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(loginReq, TAG);
    }

    private static byte[] accLoginPathBody(String userName, String pwd, String addr, double lat, double lng) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "UserName", userName);
            Utils.buildTextPart(dos, "Password", pwd);
            Utils.buildTextPart(dos, "Token", AppController.getInstance().getPrefManager().getToken());
            Utils.buildTextPart(dos, "DeviceType", Config.DEVICE_TYPE);
            Utils.buildTextPart(dos, "Version", com.gomicorp.helper.Utils.versionRelease() + "_" + com.gomicorp.helper.Utils.getVersionName());
            Utils.buildTextPart(dos, "Address", Utils.iso88951_To_utf8(addr));
            Utils.buildTextPart(dos, "Latitude", String.valueOf(lat));
            Utils.buildTextPart(dos, "Longitude", String.valueOf(lng));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void socialLogin(Account account, String token, String address, double lat, double lng, final OnAccountRequestListener listener) {
        MultipartRequest reqSocialLogin = new MultipartRequest(EndPoints.SOCIAL_LOGIN, null, Utils.mimeType, socialLoginPathBody(account, token, address, lat, lng), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.accountList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(reqSocialLogin, TAG);
    }

    private static byte[] socialLoginPathBody(Account account, String token, String address, double lat, double lng) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "UserName", account.getUserName());
            Utils.buildTextPart(dos, "FullName", Utils.iso88951_To_utf8(account.getFullName()));
            Utils.buildTextPart(dos, "Email", account.getEmail());
            Utils.buildTextPart(dos, "BirthDate", account.getBirthDate() != null ? com.gomicorp.helper.Utils.dateToString(account.getBirthDate()) : "");
            Utils.buildTextPart(dos, "Gender", String.valueOf(account.getGender()));
            Utils.buildTextPart(dos, "AccountType", String.valueOf(account.getAccType()));
            Utils.buildTextPart(dos, "Token", token);
            Utils.buildTextPart(dos, "DeviceType", Config.DEVICE_TYPE);
            Utils.buildTextPart(dos, "Version", com.gomicorp.helper.Utils.versionRelease() + "_" + com.gomicorp.helper.Utils.getVersionName());
            Utils.buildTextPart(dos, "Address", Utils.iso88951_To_utf8(address));
            Utils.buildTextPart(dos, "Latitude", String.valueOf(lat));
            Utils.buildTextPart(dos, "Longitude", String.valueOf(lng));

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getDetails(long accountID, final OnAccountRequestListener listener) {
        String url = EndPoints.GET_DETAILS.replace(UrlParams.ACCOUNT_ID, String.valueOf(accountID));

        JsonObjectRequest regDetail = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(Parser.accountList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(regDetail, TAG);
    }

    public static void changeAvatar(Bitmap avatar, final OnResponseListener listener) {
        MultipartRequest reqAvatar = new MultipartRequest(EndPoints.CHANGE_AVATAR, null, Utils.mimeType, changeAvatarPathBody(avatar), new Response.Listener<JSONObject>() {
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

        AppController.getInstance().addToRequestQueue(reqAvatar, TAG);
    }

    private static byte[] changeAvatarPathBody(Bitmap avatar) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        String userName = AppController.getInstance().getPrefManager().getUserName();
        long userID = AppController.getInstance().getPrefManager().getUserID();

        try {
            Utils.buildTextPart(dos, "UserName", userName);
            Utils.buildTextPart(dos, "AccountID", String.valueOf(userID));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            Utils.buildPart(dos, outputStream.toByteArray(), "avatar.jpg");

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
