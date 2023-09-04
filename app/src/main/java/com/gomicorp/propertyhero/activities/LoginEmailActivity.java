package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.model.Account;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LoginEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginEmailActivity.class.getSimpleName();

    private TextInputLayout inputLayoutUserName, inputLayoutPassword;
    private EditText inputUserName, inputPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.loginEmail));

        inputLayoutUserName = (TextInputLayout) findViewById(R.id.inputLayoutUserNameLogin);
        inputUserName = (EditText) findViewById(R.id.inputUserNameLogin);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPasswordLogin);
        inputPassword = (EditText) findViewById(R.id.inputPasswordLogin);

        String userName = AppController.getInstance().getPrefManager().getUserName();
        inputUserName.setText(Utils.isNullOrEmpty(userName) ? "" : userName);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE)
                    submitLogin();

                return false;
            }
        });

        findViewById(R.id.btnSubmitLogin).setOnClickListener(this);

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_login));
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
            case R.id.btnSubmitLogin:
                submitLogin();
                break;
            case R.id.btnForgotPassword:
                break;
        }
    }

    private void submitLogin() {
        if (!InputValidation.userName(this, inputLayoutUserName, inputUserName))
            return;
        final String userName = inputUserName.getText().toString().trim();

        if (!InputValidation.password(this, inputLayoutPassword, inputPassword, getString(R.string.text_err_pwd_empty), getString(R.string.text_err_pwd_length)))
            return;
        final String pwd = inputPassword.getText().toString().trim();

        progressDialog.show();
        AppController.getInstance().getPrefManager().addUserName(userName);

        AccountRequest.login(userName, pwd, "", 0, 0, new OnAccountRequestListener() {
            @Override
            public void onSuccess(List<Account> accounts) {
                Account acc = accounts.size() > 0 ? accounts.get(0) : null;
                if (acc != null) {
                    if (acc.getAccType() == Config.ACC_LOCKED) {
                        L.showAlert(LoginEmailActivity.this, getString(R.string.title_acc_locked), getString(R.string.msg_acc_locked));
                    } else if (acc.getAccType() == Config.ACC_DELETION) {
                        L.showAlert(LoginEmailActivity.this, getString(R.string.title_acc_deletion), getString(R.string.msg_acc_locked));
                    } else {
                        AppController.getInstance().getPrefManager().addUserInfo(acc.getId(), acc.getUserName(), acc.getFullName(), acc.getPhoneNumber(), acc.getAccRole(), pwd);
                        Toast.makeText(LoginEmailActivity.this, getString(R.string.text_login_success), Toast.LENGTH_LONG).show();
                        setResult(Config.SUCCESS_RESULT);
                        finish();
                    }
                } else
                    L.showAlert(LoginEmailActivity.this, getString(R.string.err_title_login), getString(R.string.err_msg_login));

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error at submitLogin()");
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }
}
