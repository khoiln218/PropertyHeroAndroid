package vn.hellosoft.hellorent.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.CircleTransform;
import vn.hellosoft.app.Config;
import vn.hellosoft.app.PermissionHelper;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.callbacks.OnResponseListener;
import vn.hellosoft.hellorent.json.AccountRequest;
import vn.hellosoft.hellorent.model.ResponseInfo;
import vn.hellosoft.helper.CroppingOption;
import vn.hellosoft.helper.CroppingOptionAdapter;
import vn.hellosoft.helper.L;
import vn.hellosoft.helper.Utils;

public class AccountDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AccountDetailsActivity.class.getSimpleName();
    private static final int TIMER_CHANGE_AVATR = 3000; // 3s Auto change

    private ImageView imgAvatar;
    private TextView tvFullName, tvUserName;

    private LinearLayout btnChangePwd;

    private ImageLoader imageLoader;
    private Uri captureUri;
    private File outPutFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PermissionHelper.hasCameraPermission(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);

        btnChangePwd = (LinearLayout) findViewById(R.id.btnChangePwd);

        imageLoader = AppController.getInstance().getImageLoader();
        outPutFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");

        Bundle data = getIntent().getBundleExtra(Config.DATA_EXTRA);
        if (data == null)
            finish();

        getSupportActionBar().setTitle(AppController.getInstance().getPrefManager().getFullName());

        Picasso.with(this)
                .load(data.getString(Config.AVATAR_URL))
                .placeholder(R.drawable.default_avatar)
                .transform(new CircleTransform())
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
        switch (v.getId()) {
            case R.id.btnChangeAvatar:
                if (Utils.isSDPresent())
                    selectImageOption();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.err_msg_sd_card), Toast.LENGTH_LONG).show();
                break;
            case R.id.btnUserInfo:
                startActivity(new Intent(this, AccountInfoActivity.class));
                break;
            case R.id.btnChangePwd:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.btnLogout:
                String userName = AppController.getInstance().getPrefManager().getUserName();
                AppController.getInstance().getPrefManager().Logout();
                AppController.getInstance().getPrefManager().addUserName(userName);

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_CAMERA:
                if (resultCode == RESULT_OK)
                    croppingImage();
                break;
            case Config.REQUEST_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    captureUri = data.getData();
                    croppingImage();
                }
                break;
            case Config.REQUEST_CROPPING:
                if (outPutFile.exists()) {
                    final Bitmap bitmap = Utils.decodeFile(outPutFile, 512, 512);
                    imgAvatar.setImageBitmap(bitmap);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handleChangeAvatar(bitmap);
                        }
                    }, TIMER_CHANGE_AVATR);
                }
                break;
        }
    }

    private void selectImageOption() {
        final CharSequence[] items = getResources().getStringArray(R.array.image_option);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                        captureUri = Uri.fromFile(file);

                        camera.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                        startActivityForResult(camera, Config.REQUEST_CAMERA);
                        break;
                    case 1:
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, Config.REQUEST_GALLERY);
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });

        builder.show();
    }

    private void croppingImage() {
        final ArrayList<CroppingOption> cropOptions = new ArrayList();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(captureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);

            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, Config.REQUEST_CROPPING);
            } else {
                for (ResolveInfo res : list) {
                    final CroppingOption co = new CroppingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CroppingOptionAdapter adapter = new CroppingOptionAdapter(getApplicationContext(), cropOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(cropOptions.get(which).appIntent, Config.REQUEST_CROPPING);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (captureUri != null) {
                            getContentResolver().delete(captureUri, null, null);
                            captureUri = null;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void handleChangeAvatar(Bitmap bitmap) {
        AccountRequest.changeAvatar(bitmap, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess())
                    Toast.makeText(getApplicationContext(), getString(R.string.text_update_success), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at handleChangeAvatar()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }
}
