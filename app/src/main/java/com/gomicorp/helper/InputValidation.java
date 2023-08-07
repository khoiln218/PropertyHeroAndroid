package com.gomicorp.helper;

import android.app.Activity;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.gomicorp.app.AppController;
import com.gomicorp.propertyhero.R;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by CTO-HELLOSOFT on 4/6/2016.
 */
public class InputValidation {

    public static boolean userName(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText) {
        String userName = editText.getText().toString().trim();

        if (userName.isEmpty()) {
            inputLayout.setError(activity.getString(R.string.text_err_username_empty));
            requestFocus(activity, editText);
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            inputLayout.setErrorEnabled(false);
        } else if (!Utils.isValidPhoneNumber(userName)) {
            inputLayout.setError(activity.getString(R.string.text_err_username));
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;
    }

    public static boolean password(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText, String errEmpty, String errLength) {
        String pwd = editText.getText().toString().trim();
        if (pwd.isEmpty()) {
            inputLayout.setError(errEmpty);
            requestFocus(activity, editText);
            return false;
        } else if (pwd.length() < 6 || pwd.length() > 32) {
            inputLayout.setError(errLength);
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;
    }

    public static boolean confirmPassword(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText, String pwd) {
        String str = editText.getText().toString();

        if (str.isEmpty() || !str.equals(pwd)) {
            inputLayout.setError(activity.getString(R.string.text_err_confirm));
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;
    }

    public static boolean oldPassword(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText) {
        String input = editText.getText().toString();
        String pwd = AppController.getInstance().getPrefManager().getPassword();

        if (input.isEmpty() || !input.equals(pwd)) {
            inputLayout.setError(activity.getString(R.string.text_err_old_pwd));
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;
    }

    public static boolean phoneNumber(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText) {
        String phone = editText.getText().toString().trim();

        if (phone.isEmpty()) {
            inputLayout.setError(activity.getString(R.string.text_err_phone_empty));
            requestFocus(activity, editText);
            return false;
        } else if (!Utils.isValidPhoneNumber(phone)) {
            inputLayout.setError(activity.getString(R.string.text_err_phone));
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;

    }

    public static boolean inputText(AppCompatActivity activity, TextInputLayout inputLayout, EditText editText, String errMsg) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError(errMsg);
            requestFocus(activity, editText);
            return false;
        } else
            inputLayout.setErrorEnabled(false);

        return true;
    }


    public static void requestFocus(Activity activity, View view) {
        if (view.requestFocus())
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
