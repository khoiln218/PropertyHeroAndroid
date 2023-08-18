package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.app.PermissionHelper;
import com.gomicorp.helper.L;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.model.Account;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 812;

    private Bundle data;

    private CallbackManager fbCallbackManager;
    private LoginButton fbButton;

    private GoogleApiClient googleApiClient;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            PermissionHelper.hasAccountsPermission(this);

        data = getIntent().getBundleExtra(Config.DATA_EXTRA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_login));

        fbCallbackManager = CallbackManager.Factory.create();
        fbButton = (LoginButton) findViewById(R.id.fbButton);

        handleLoginFacebook();
        buildGoogleApiClient();

        findViewById(R.id.fbLoginButton).setOnClickListener(this);
        findViewById(R.id.googleButtonLogin).setOnClickListener(this);
        findViewById(R.id.btnLoginEmail).setOnClickListener(this);
        findViewById(R.id.btnMemberRegistration).setOnClickListener(this);

    }

    protected synchronized void buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbLoginButton:
                fbButton.performClick();
                progressDialog.show();
                break;
            case R.id.googleButtonLogin:
                Intent googleIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(googleIntent, RC_SIGN_IN);
                progressDialog.show();
                break;
            case R.id.btnLoginEmail:
                Intent loginEmail = new Intent(getApplicationContext(), LoginEmailActivity.class);
                startActivityForResult(loginEmail, Config.REQUEST_LOGIN);
                break;
            case R.id.btnMemberRegistration:
                Intent memberRegister = new Intent(getApplicationContext(), MemberRegistrationActivity.class);
                startActivityForResult(memberRegister, Config.REQUEST_LOGIN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_LOGIN:
                if (resultCode == Config.SUCCESS_RESULT)
                    handleLaunchActivity();
                break;
            case RC_SIGN_IN:
                handleLoginGoogle(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
            default:
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleLoginFacebook() {
        fbButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        fbButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String userName = object.getString("id");
                            String fullName = object.getString("name");
                            int gender = object.getString("gender").equals("male") ? 1 : 0;
                            String email = object.getString("email");

                            requestSocialLogin(new Account(userName, fullName, gender, null, email, Config.ACC_FACEBOOK));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            L.showAlert(LoginActivity.this, null, getString(R.string.err_msg_login_facebook));
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Cancel Login Facebook");
                progressDialog.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.getCause().toString());
                progressDialog.dismiss();
            }
        });
    }

    private void handleLoginGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount object = result.getSignInAccount();
            requestSocialLogin(new Account(object.getId(), object.getDisplayName(), 0, null, object.getEmail(), Config.ACC_GOOGLE));
        } else {
            L.showAlert(this, null, getString(R.string.err_msg_login_google));
            progressDialog.dismiss();
        }
    }

    private void requestSocialLogin(Account account) {
        AccountRequest.socialLogin(account, AppController.getInstance().getPrefManager().getToken(), "", 0, 0, new OnAccountRequestListener() {
            @Override
            public void onSuccess(List<Account> accounts) {
                Account acc = accounts.size() > 0 ? accounts.get(0) : null;
                if (acc != null) {
                    AppController.getInstance().getPrefManager().addUserInfo(acc.getId(), acc.getEmail(), acc.getFullName(), acc.getPhoneNumber(), acc.getAccRole(), null);
                    handleLaunchActivity();
                } else {
                    LoginManager.getInstance().logOut();
                    googleSignOut();
                    L.showAlert(LoginActivity.this, null, getString(R.string.err_msg_login));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                LoginManager.getInstance().logOut();
                googleSignOut();
                Log.e(TAG, "Error at requestSocialLogin()");
            }
        });
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //
            }
        });
    }

    private void handleLaunchActivity() {
        String activity = data == null ? "" : data.getString(Config.STRING_DATA);

        if (CreateProductActivity.class.getSimpleName().equals(activity)) {

            if (AppController.getInstance().getPrefManager().getPhoneNumber() != null) {
                startActivity(new Intent(this, CreateProductActivity.class));
                finish();
            } else
                L.launchUpdatePhone(this, true);

        } else if (ManagementProductActivity.class.getSimpleName().equals(activity)) {

            startActivity(new Intent(this, ManagementProductActivity.class));
            finish();

        } else if (WarningActivity.class.getSimpleName().equals(activity)) {

            long productID = data == null ? 0 : data.getLong(Config.PRODUCT_ID, 0);
            Intent intent = new Intent(this, WarningActivity.class);
            intent.putExtra(Config.PRODUCT_ID, productID);
            startActivity(intent);
            finish();

        } else {
            setResult(Config.SUCCESS_RESULT);
            finish();
        }

    }
}
