package com.gomicorp.propertyhero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.MultipartRequest;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.callbacks.OnAccountRequestListener;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.json.AccountRequest;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.json.Utils;
import com.gomicorp.propertyhero.model.Account;
import com.gomicorp.propertyhero.model.ResponseInfo;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MemberRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MemberRegistrationActivity.class.getSimpleName();

    private TextInputLayout inputLayoutUserName, inputLayoutPassword, inputLayoutConfirm, inputLayoutFullName, inputLayoutPhone;
    private EditText inputUserName, inputPassword, inputConfirm, inputFullName, inputPhone;
    private CheckBox chkTerms, chkPrivacy;
    private Button btnSubmit;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        inputLayoutUserName = (TextInputLayout) findViewById(R.id.inputLayoutUserNameMemberReg);
        inputUserName = (EditText) findViewById(R.id.inputUserNameMemberReg);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPasswordMemberReg);
        inputPassword = (EditText) findViewById(R.id.inputPasswordMemberReg);
        inputLayoutConfirm = (TextInputLayout) findViewById(R.id.inputLayoutConfirmMemberReg);
        inputConfirm = (EditText) findViewById(R.id.inputConfirmMemberReg);
        inputLayoutFullName = (TextInputLayout) findViewById(R.id.inputLayoutFullNameMemberReg);
        inputFullName = (EditText) findViewById(R.id.inputFullNameMemberReg);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutPhoneMemberReg);
        inputPhone = (EditText) findViewById(R.id.inputPhoneMemberReg);

        chkTerms = (CheckBox) findViewById(R.id.chkTermsMember);
        chkPrivacy = (CheckBox) findViewById(R.id.chkPrivacyMember);
        btnSubmit = (Button) findViewById(R.id.btnSubmitMemberReg);

        chkTerms.setOnClickListener(this);
        chkPrivacy.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        findViewById(R.id.tvTermsMember).setOnClickListener(this);
        findViewById(R.id.tvPrivacyMember).setOnClickListener(this);

        enabledSubmit();

        com.gomicorp.helper.Utils.hideSoftKeyboard(this, findViewById(R.id.memberRegistration));

        progressDialog = L.progressDialog(this, null, getString(R.string.text_msg_reg));
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
            case R.id.chkTermsMember:
                enabledSubmit();
                break;
            case R.id.chkPrivacyMember:
                enabledSubmit();
                break;
            case R.id.tvTermsMember:
                launchWebView(Config.INFO_TERMS);
                break;
            case R.id.tvPrivacyMember:
                launchWebView(Config.INFO_PRIVACY);
                break;
            case R.id.btnSubmitMemberReg:
                submitRegister();
            default:
                break;
        }
    }

    private void enabledSubmit() {
        if (chkTerms.isChecked() && chkPrivacy.isChecked())
            btnSubmit.setEnabled(true);
        else
            btnSubmit.setEnabled(false);
    }

    private void launchWebView(int index) {
        Intent intent = new Intent(this, InfoViewActivity.class);
        intent.putExtra(Config.DATA_EXTRA, index);
        startActivity(intent);
    }

    private void submitRegister() {
        if (!InputValidation.userName(this, inputLayoutUserName, inputUserName))
            return;
        final String userName = inputUserName.getText().toString().trim();

        if (!InputValidation.password(this, inputLayoutPassword, inputPassword, getString(R.string.text_err_pwd_empty), getString(R.string.text_err_pwd_length)))
            return;
        final String pwd = inputPassword.getText().toString().trim();

        if (!InputValidation.confirmPassword(this, inputLayoutConfirm, inputConfirm, pwd))
            return;

        if (!InputValidation.inputText(this, inputLayoutFullName, inputFullName, getString(R.string.text_err_full_name)))
            return;
        final String fullName = inputFullName.getText().toString().trim();

        if (!InputValidation.phoneNumber(this, inputLayoutPhone, inputPhone))
            return;
        final String phone = inputPhone.getText().toString().trim();

        progressDialog.show();
        AccountRequest.verify(userName, new OnAccountRequestListener() {
            @Override
            public void onSuccess(List<Account> accounts) {
                if (accounts.size() != 0) {
                    progressDialog.dismiss();
                    L.showAlert(MemberRegistrationActivity.this, null, getString(R.string.text_verify_user));
                    InputValidation.requestFocus(MemberRegistrationActivity.this, inputUserName);
                    return;
                }

                handleRegister(userName, pwd, fullName, phone);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private void handleRegister(final String userName, final String pwd, String fullName, String phone) {
        MultipartRequest memberReg = new MultipartRequest(EndPoints.MEMBER_REG, null, Utils.mimeType, memberRegPathBody(userName, pwd, fullName, phone), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseInfo info = Parser.responseInfo(response);
                if (info != null && info.isSuccess()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.text_register_success), Toast.LENGTH_LONG).show();
                    progressDialog.setMessage(getString(R.string.text_msg_login));
                    handleLogin(userName, pwd);
                } else {
                    progressDialog.dismiss();
                    L.showAlert(MemberRegistrationActivity.this, null, getString(R.string.err_request_api));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                L.showToast(getString(R.string.request_time_out));
            }
        });

        AppController.getInstance().addToRequestQueue(memberReg, TAG);
    }

    private void handleLogin(String userName, final String pwd) {
        AccountRequest.login(userName, pwd, "", 0, 0, new OnAccountRequestListener() {
            @Override
            public void onSuccess(List<Account> accounts) {
                Account acc = accounts.size() > 0 ? accounts.get(0) : null;
                if (acc != null) {
                    if (acc.getAccType() != Config.ACC_LOCKED) {
                        AppController.getInstance().getPrefManager().addUserInfo(acc.getId(), acc.getUserName(), acc.getFullName(), acc.getPhoneNumber(), acc.getAccRole(), pwd);
                        Toast.makeText(MemberRegistrationActivity.this, getString(R.string.text_login_success), Toast.LENGTH_LONG).show();
                        setResult(Config.SUCCESS_RESULT);
                        finish();
                    } else
                        L.showAlert(MemberRegistrationActivity.this, getString(R.string.title_acc_locked), getString(R.string.msg_acc_locked));

                } else
                    L.showAlert(MemberRegistrationActivity.this, getString(R.string.err_title_login), getString(R.string.err_msg_login));

                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                L.showToast(getString(R.string.request_time_out));
            }
        });
    }

    private byte[] memberRegPathBody(String userName, String pwd, String fullName, String phone) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Utils.buildTextPart(dos, "UserName", userName);
            Utils.buildTextPart(dos, "Password", pwd);
            Utils.buildTextPart(dos, "FullName", Utils.iso88951_To_utf8(fullName));
            Utils.buildTextPart(dos, "PhoneNumber", phone);

            dos.writeBytes(Utils.twoHyphens + Utils.boundary + Utils.twoHyphens + Utils.lineEnd);
            // pass to multipart body
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}