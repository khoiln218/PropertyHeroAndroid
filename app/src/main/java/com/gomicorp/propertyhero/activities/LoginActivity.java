package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.model.Account;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 812;
    public CallbackManager callbackManager;
    private Bundle data;
    private LoginButton fbButton;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = getIntent().getBundleExtra(Config.DATA_EXTRA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_login));

        callbackManager = CallbackManager.Factory.create();
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
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        if (id == R.id.fbLoginButton) {
            facebookSignOut();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
            fbButton.performClick();
            progressDialog.show();
        } else if (id == R.id.googleButtonLogin) {
            googleSignOut();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ActivityCompat.startActivityForResult(this, signInIntent, RC_SIGN_IN, null);
            progressDialog.show();
        } else if (id == R.id.btnLoginEmail) {
            Intent loginEmail = new Intent(getApplicationContext(), LoginEmailActivity.class);
            ActivityCompat.startActivityForResult(this, loginEmail, Config.REQUEST_LOGIN, null);
        } else if (id == R.id.btnMemberRegistration) {
            Intent memberRegister = new Intent(getApplicationContext(), MemberRegistrationActivity.class);
            ActivityCompat.startActivityForResult(this, memberRegister, Config.REQUEST_LOGIN, null);
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
                handleLoginGoogle(data);
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleLoginFacebook() {
        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String fields = "id,name,email";
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        (object, response) -> {
                            if (response != null && object != null) {
                                try {
                                    String userName = object.getString("id");
                                    String fullName = object.getString("name");
                                    String email = object.getString("email");
                                    requestSocialLogin(new Account(userName, fullName, 0, null, email, Config.ACC_FACEBOOK));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    L.showAlert(LoginActivity.this, null, getString(R.string.err_msg_login_facebook));
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", fields);
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Cancel Login Facebook");
                progressDialog.dismiss();
            }

            @Override
            public void onError(@NotNull FacebookException error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    private void handleLoginGoogle(Intent data) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (account == null) {
                L.showAlert(this, null, getString(R.string.err_msg_login_google));
                progressDialog.dismiss();
            } else {
                String userName = account.getId();
                String fullName = account.getDisplayName();
                String email = account.getEmail();
                requestSocialLogin(new Account(userName, fullName, 0, null, email, Config.ACC_GOOGLE));
            }
        } catch (ApiException e) {
            e.printStackTrace();
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

    private void facebookSignOut() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LoginManager.getInstance().logOut();
        }
    }

    private void googleSignOut() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut();
        }
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
