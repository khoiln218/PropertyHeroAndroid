package vn.hellosoft.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.activities.MapViewProductActivity;
import vn.hellosoft.hellorent.model.Feature;
import vn.hellosoft.services.AddressResultReceiver;
import vn.hellosoft.services.FetchAddressIntentService;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class Utils {

    public static String versionRelease() {
        return Build.VERSION.RELEASE;
    }

    public static String getVersionName() {
        Context context = AppController.getInstance().getAppContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double toNumber(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public static boolean isValidPhoneNumber(String number) {

        try {
            double isNumber = Double.parseDouble(number);
            int stChar = Integer.parseInt(Character.toString(number.charAt(0)));
            int ndChar = Integer.parseInt(Character.toString(number.charAt(1)));
            int rdChar = Integer.parseInt(Character.toString(number.charAt(2)));

            if (stChar == 0) {
                if (number.length() == 11 && ndChar == 1)
                    return true;
                if (number.length() == 10) {
                    switch (ndChar) {
                        case 9:
                            return true;
                        case 8:
                            if (rdChar == 6 || rdChar == 8 || rdChar == 9)
                                return true;
                            else
                                return false;
                        default:
                            return false;
                    }
                }
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty() && !str.equals("null"))
            return false;

        return true;
    }

    public static void hideSoftKeyboard(final Activity activity, View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                hideSoftKeyboard(activity, ((ViewGroup) view).getChildAt(i));
        }
    }

    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) AppController.getInstance().getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ingore) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point.x;
    }

    public static boolean isSDPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static List<String> getImagePaths(Context context) {
        List<String> imagePaths = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media._ID};
        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);

        int col_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            imagePaths.add(cursor.getString(col_index_data));
        }

        return imagePaths;
    }

    public static byte[] getBytesBitmap(String filePath, int reqWidth, int reqHeight) {

        try {
            File file = new File(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            return stream.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSize) > reqHeight && (halfWidth / inSize) > reqWidth) {
                inSize *= 2;
            }
        }

        return inSize;
    }

    public static Bitmap decodeFile(File file, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(new FileInputStream(file), null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBitmapPath(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);
        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
    }

    public static void launchMapView(Activity activity, String title, LatLng latLng, int type) {
        Intent intent = new Intent(activity, MapViewProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Config.STRING_DATA, title);
//        bundle.putParcelable(Config.PARCELABLE_DATA, latLng);
        bundle.putInt(Config.DATA_TYPE, type);
        intent.putExtra(Config.DATA_EXTRA, bundle);
        activity.startActivityForResult(intent, Config.REQUEST_FIND_MARKER);
    }

    public static void startAddressService(Activity activity, AddressResultReceiver addressResult, LatLng location) {
        Intent addressService = new Intent(activity, FetchAddressIntentService.class);
        addressService.putExtra(Config.RECEIVER, addressResult);
//        addressService.putExtra(Config.PARCELABLE_DATA, location);
        activity.startService(addressService);
    }

    public static LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(AppController.getInstance().getApplicationContext());
        try {
            List<Address> addressList = coder.getFromLocationName(strAddress, 5);
            if (addressList.size() > 0 && addressList != null)
                return new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String featureListToString(List<Feature> featureList, boolean getName) {
        List<String> arr = new ArrayList<>();
        for (Feature obj : featureList) {
            if (getName)
                arr.add(obj.getName());
            else
                arr.add(String.valueOf(obj.getId()));
        }
        return TextUtils.join(", ", arr);
    }

    public static String phoneFormatter(String phone) {
        return phone.substring(0, 4) + "-" + phone.substring(4, 7) + "-" + phone.substring(7, phone.length());
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
