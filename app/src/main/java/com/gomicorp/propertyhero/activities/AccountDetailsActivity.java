package com.gomicorp.propertyhero.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.CircleTransform;
import com.gomicorp.app.Config;
import com.gomicorp.helper.ImagePickerActivity;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AccountDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AccountDetailsActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE = 1;
    private static final int AVATAR_MAX_SIZE = 512;

    private ImageView imgAvatar;
    private TextView tvFullName, tvUserName;
    private LinearLayout btnChangePwd;
    private String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);

        btnChangePwd = (LinearLayout) findViewById(R.id.btnChangePwd);

        Bundle data = getIntent().getBundleExtra(Config.DATA_EXTRA);
        if (data == null) {
            finish();
            return;
        }

        getSupportActionBar().setTitle(AppController.getInstance().getPrefManager().getFullName());

        avatarUrl = data.getString(Config.AVATAR_URL);
        Picasso.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.default_avatar)
                .transform(new CircleTransform())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imgAvatar);

        tvFullName.setText(AppController.getInstance().getPrefManager().getFullName());
        String userName = AppController.getInstance().getPrefManager().getUserName();
        tvUserName.setText(Utils.isNullOrEmpty(userName) ? "" : userName);

        if (data.getInt(Config.ACCOUNT_TYPE) != Config.HELLO_RENT)
            btnChangePwd.setVisibility(View.GONE);
        else
            btnChangePwd.setVisibility(View.VISIBLE);

        btnChangePwd.setOnClickListener(this);
        findViewById(R.id.btnChangeAvatar).setOnClickListener(this);
        findViewById(R.id.btnUserInfo).setOnClickListener(this);
        findViewById(R.id.btnLogout).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnChangeAvatar) {
            showImagePickerOptions();
        } else if (id == R.id.btnUserInfo) {
            startActivity(new Intent(this, AccountInfoActivity.class));
        } else if (id == R.id.btnChangePwd) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.btnLogout) {
            String userName = AppController.getInstance().getPrefManager().getUserName();
            AppController.getInstance().getPrefManager().Logout();
            AppController.getInstance().getPrefManager().addUserName(userName);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onCameraSelected() {
                launchCamera();
            }

            @Override
            public void onGallerySelected() {
                launchGallery();
            }
        });
    }

    public void launchCamera() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.REQUEST_CODE_TYPE, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        intent.putExtra(ImagePickerActivity.EXTRA_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.EXTRA_ASPECT_RATIO_X, 1);
        intent.putExtra(ImagePickerActivity.EXTRA_ASPECT_RATIO_Y, 1);

        intent.putExtra(ImagePickerActivity.EXTRA_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.EXTRA_BITMAP_MAX_WIDTH, AVATAR_MAX_SIZE);
        intent.putExtra(ImagePickerActivity.EXTRA_BITMAP_MAX_HEIGHT, AVATAR_MAX_SIZE);

        ActivityCompat.startActivityForResult(this, intent, REQUEST_IMAGE, null);
    }

    public void launchGallery() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.REQUEST_CODE_TYPE, ImagePickerActivity.REQUEST_IMAGE_GALLERY);

        intent.putExtra(ImagePickerActivity.EXTRA_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.EXTRA_ASPECT_RATIO_X, 1);
        intent.putExtra(ImagePickerActivity.EXTRA_ASPECT_RATIO_Y, 1);

        intent.putExtra(ImagePickerActivity.EXTRA_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.EXTRA_BITMAP_MAX_WIDTH, AVATAR_MAX_SIZE);
        intent.putExtra(ImagePickerActivity.EXTRA_BITMAP_MAX_HEIGHT, AVATAR_MAX_SIZE);

        ActivityCompat.startActivityForResult(this, intent, REQUEST_IMAGE, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    final Bitmap bitmap = getBitmap(uri);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCircular(true);
                    imgAvatar.setImageDrawable(roundedBitmapDrawable);
                    handleChangeAvatar(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    L.showToast(e.getLocalizedMessage());
                }
            }
        }
    }

    private Bitmap getBitmap(Uri imageUri) throws Exception {
        Bitmap bitmap;
        ContentResolver contentResolver = getContentResolver();
        if (Build.VERSION.SDK_INT < 28) {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
        } else {
            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
            bitmap = ImageDecoder.decodeBitmap(source);
        }
        return bitmap;
    }

    private void handleChangeAvatar(Bitmap bitmap) {
        AccountRequest.changeAvatar(bitmap, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.text_update_success), Toast.LENGTH_LONG).show();
                } else {
                    Picasso.with(AccountDetailsActivity.this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CircleTransform())
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(imgAvatar);
                    Toast.makeText(getApplicationContext(), getString(R.string.err_request_api), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at handleChangeAvatar()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }
}
