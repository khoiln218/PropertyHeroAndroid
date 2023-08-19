package com.gomicorp.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.gomicorp.propertyhero.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ImagePickerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_IMAGE_GALLERY = 1;
    public static final String EXTRA_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String EXTRA_ASPECT_RATIO_Y = "aspect_ratio_y";
    public static final String EXTRA_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String EXTRA_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String EXTRA_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String EXTRA_BITMAP_MAX_WIDTH = "max_width";
    public static final String EXTRA_BITMAP_MAX_HEIGHT = "max_height";
    public static final String REQUEST_CODE_TYPE = "request_code";
    private int IMAGE_COMPRESSION = 80;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private String fileName;

    public static void showImagePickerOptions(Context context, final PickerOptionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.create_avatar_title);

        String[] items = context.getResources().getStringArray(R.array.image_option);

        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0: {
                    listener.onCameraSelected();
                    break;
                }
                case 1: {
                    listener.onGallerySelected();
                    break;
                }
                default: {
                    dialog.dismiss();
                    break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) return;

        ASPECT_RATIO_X = intent.getIntExtra(EXTRA_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(EXTRA_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(EXTRA_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(EXTRA_LOCK_ASPECT_RATIO, lockAspectRatio);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(EXTRA_SET_BITMAP_MAX_WIDTH_HEIGHT, setBitmapMaxWidthHeight);
        bitmapMaxWidth = intent.getIntExtra(EXTRA_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(EXTRA_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int request = intent.getIntExtra(REQUEST_CODE_TYPE, REQUEST_IMAGE_GALLERY);

        if (request == REQUEST_IMAGE_CAPTURE) {
            checkCameraPermissions();
        } else {
            checkGalleryPermissions();
        }
    }

    @AfterPermissionGranted(REQUEST_IMAGE_CAPTURE)
    private void checkCameraPermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            takeCameraImage();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera), REQUEST_IMAGE_CAPTURE, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_IMAGE_GALLERY)
    private void checkGalleryPermissions() {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }
        if (EasyPermissions.hasPermissions(this, perms)) {
            takeGalleryImage();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_external_storage), REQUEST_IMAGE_GALLERY, perms);
        }
    }

    public void takeCameraImage() {
        fileName = System.currentTimeMillis() + ".jpg";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
        ActivityCompat.startActivityForResult(this, takePictureIntent, REQUEST_IMAGE_CAPTURE, null);
    }

    public void takeGalleryImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ActivityCompat.startActivityForResult(this, pickIntent, REQUEST_IMAGE_GALLERY, null);
    }

    @SuppressWarnings("all")
    public Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");

        if (!path.exists()) {
            path.mkdirs();
        }

        File image = new File(path, fileName);

        return FileProvider.getUriForFile(ImagePickerActivity.this, getPackageName() + ".provider", image);
    }

    public void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setToolbarTitle(getString(R.string.edit_image));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        if (lockAspectRatio) {
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
        }

        if (setBitmapMaxWidthHeight) {
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);
        }

        UCrop.of(sourceUri, destinationUri).withOptions(options).start(this);
    }

    public String queryName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) return "";

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();
        return name;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
                break;
            }
            case REQUEST_IMAGE_GALLERY: {
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            }
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;
            }
            case UCrop.RESULT_ERROR: {
                if (data != null) {
                    Throwable error = UCrop.getError(data);
                    if (error != null) error.printStackTrace();
                }
                setResultCancelled();
                break;
            }
            default:
                setResultCancelled();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface PickerOptionListener {

        void onCameraSelected();

        void onGallerySelected();
    }
}
