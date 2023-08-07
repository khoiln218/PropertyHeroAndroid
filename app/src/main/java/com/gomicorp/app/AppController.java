package com.gomicorp.app;

import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.gomicorp.database.HelloAppDb;
import com.gomicorp.propertyhero.BuildConfig;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController appInstance;

    private AppPreferenceManager pref;
    private HelloAppDb appDb;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private LruBitmapCache lruBitmapCache;

    public static synchronized AppController getInstance() {
        return appInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            boolean isUnderUnitTest;
            try {
                Class<?> robolectric = Class.forName("org.robolectric.Robolectric");
                isUnderUnitTest = (robolectric != null);
            } catch (ClassNotFoundException e) {
                isUnderUnitTest = false;
            }

            if (!isUnderUnitTest) {
                // Re-throw if this does not seem to be triggered by Robolectric.
                throw multiDexException;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        appDb = new HelloAppDb(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        VolleyLog.DEBUG = BuildConfig.DEBUG;
    }

    public Context getAppContext() {
        return appInstance.getApplicationContext();
    }

    public AppPreferenceManager getPrefManager() {
        if (pref == null)
            pref = new AppPreferenceManager(this);

        return pref;
    }

    public synchronized HelloAppDb getWritableDb() {
        if (appDb == null)
            appDb = new HelloAppDb(getAppContext());

        return appDb;

    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            getLruBitmapCache();
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }

        return this.imageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (lruBitmapCache == null)
            lruBitmapCache = new LruBitmapCache();
        return this.lruBitmapCache;
    }

    public void cancelPedingRequesrs(Object tag) {
        if (requestQueue != null)
            requestQueue.cancelAll(tag);
    }
}
