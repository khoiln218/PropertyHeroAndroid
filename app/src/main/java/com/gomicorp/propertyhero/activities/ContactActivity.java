package com.gomicorp.propertyhero.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.helper.InputValidation;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.google.android.material.textfield.TextInputLayout;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = ContactActivity.class.getSimpleName();
    private static final String[] recipients = {"info@gomicorp.vn"};

    private TextInputLayout inputLayoutFullName, inputLayoutPhoneNumber, inputLayoutContent;
    private EditText inputFullName, inputPhoneNumber, inputContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Utils.hideSoftKeyboard(this, findViewById(R.id.layoutUpdateInfo));

        inputLayoutFullName = (TextInputLayout) findViewById(R.id.inputLayoutFullName);
        inputLayoutPhoneNumber = (TextInputLayout) findViewById(R.id.inputLayoutPhoneNumber);
        inputLayoutContent = (TextInputLayout) findViewById(R.id.inputLayoutContent);

        inputFullName = (EditText) findViewById(R.id.inputFullName);
        inputPhoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);
        inputContent = (EditText) findViewById(R.id.inputContent);

        inputFullName.setText(Utils.isNullOrEmpty(AppController.getInstance().getPrefManager().getFullName()) ? "" : AppController.getInstance().getPrefManager().getFullName());
        inputPhoneNumber.setText(Utils.isNullOrEmpty(AppController.getInstance().getPrefManager().getPhoneNumber()) ? "" : AppController.getInstance().getPrefManager().getPhoneNumber());

        findViewById(R.id.btnSubmitContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitForm() {
        if (!InputValidation.inputText(this, inputLayoutFullName, inputFullName, getString(R.string.text_err_full_name)))
            return;

        if (!InputValidation.phoneNumber(this, inputLayoutPhoneNumber, inputPhoneNumber))
            return;

        if (!InputValidation.inputText(this, inputLayoutContent, inputContent, getString(R.string.text_err_content)))
            return;

        String subject = inputFullName.getText().toString() + " - " + inputPhoneNumber.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setDataAndType(Uri.parse("mailto:"), "message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, inputContent.getText().toString());

        try {
            ActivityCompat.startActivityForResult(this, Intent.createChooser(email, "Send Email"), Config.REQUEST_SEND_EMAIL, null);
        } catch (android.content.ActivityNotFoundException ex) {
            L.showToast("No email client installed.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_SEND_EMAIL) {
            L.showToast(getString(R.string.text_contact_success));
            finish();
        }
    }
}
