package com.gomicorp.helper;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class MultipartRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> responseListener;
    private Response.ErrorListener errorListener;
    private Map<String, String> headers;
    private String mimeType;
    private byte[] multipartBody;

    public MultipartRequest(String url, Map<String, String> headers, String mimeType, byte[] multipartBody, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        this.responseListener = responseListener;
        this.errorListener = errorListener;
        this.headers = headers;
        this.mimeType = mimeType;
        this.multipartBody = multipartBody;
    }

    @Override
    public String getBodyContentType() {
        return mimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return multipartBody;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new JSONObject(new String(response.data, "UTF-8")),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception ex) {
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        this.responseListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        this.errorListener.onErrorResponse(error);
    }
}
