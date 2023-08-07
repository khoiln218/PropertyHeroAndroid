package com.gomicorp.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.VolleyError;
import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.EditProductActivity;
import com.gomicorp.propertyhero.activities.ProductDetailsActivity;
import com.gomicorp.propertyhero.callbacks.OnProductConfigListener;
import com.gomicorp.propertyhero.callbacks.OnResponseListener;
import com.gomicorp.propertyhero.json.ProductRequest;
import com.gomicorp.propertyhero.model.Product;
import com.gomicorp.propertyhero.model.ResponseInfo;

/**
 * Created by CTO-HELLOSOFT on 5/31/2016.
 */
public class ProductConfigDialog extends DialogFragment implements View.OnClickListener {

    public OnProductConfigListener listener;
    private Product product;

    public ProductConfigDialog() {
    }

    public static ProductConfigDialog instance(Product product) {
        ProductConfigDialog dialog = new ProductConfigDialog();
        Bundle args = new Bundle();
        args.putParcelable(Config.DATA_EXTRA, product);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setContentView(R.layout.dialog_product_config);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.product = getArguments().getParcelable(Config.DATA_EXTRA);
        if (product == null)
            dismiss();

        dialog.findViewById(R.id.btnViewProduct).setOnClickListener(this);
        dialog.findViewById(R.id.btnEditNote).setOnClickListener(this);
        dialog.findViewById(R.id.btnEditProduct).setOnClickListener(this);
        dialog.findViewById(R.id.btnDeleteProduct).setOnClickListener(this);
        dialog.findViewById(R.id.btnReCertified).setOnClickListener(this);
        dialog.findViewById(R.id.btnComplete).setOnClickListener(this);
        dialog.findViewById(R.id.btnEndCertified).setOnClickListener(this);
        dialog.findViewById(R.id.btnClose).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onResume() {

        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewProduct:
                launchProductDetail();
                break;
            case R.id.btnEditNote:
                showDialogNote();
                break;
            case R.id.btnEditProduct:
                if (product.getStatus() != Config.PRODUCT_CERTIFIED) {
                    dismiss();
                    Intent intent = new Intent(getActivity(), EditProductActivity.class);
                    intent.putExtra(Config.DATA_EXTRA, product.getId());
                    startActivity(intent);
                } else
                    L.showToast(getString(R.string.msg_edit_product).replace("...", product.getId() + ""));
                break;
            case R.id.btnDeleteProduct:
                showDialogConfirm(getString(R.string.title_delete_product).replace("...", product.getId() + ""), getString(R.string.text_msg_delete_product), Config.PRODUCT_DELETE);
                break;
            case R.id.btnReCertified:
                if (product.getStatus() != Config.PRODUCT_CERTIFIED)
                    listener.onClick(Config.PRODUCT_ACTIVATED);
                else
                    showDialogConfirm(getString(R.string.title_re_certified).replace("...", product.getId() + ""), getString(R.string.msg_re_certified), Config.PRODUCT_CERTIFIED);
                break;
            case R.id.btnComplete:
                if (product.getStatus() != Config.PRODUCT_COMPLETED)
                    listener.onClick(Config.PRODUCT_COMPLETED);
                else
                    L.showToast(getString(R.string.msg_completed_product).replace("...", product.getId() + ""));
                break;
            case R.id.btnEndCertified:
                if (product.getStatus() == Config.PRODUCT_CERTIFIED)
                    showDialogConfirm(getString(R.string.title_end_certified).replace("...", product.getId() + ""), getString(R.string.msg_end_certified), Config.PRODUCT_END_CERTIFIED);
                else
                    L.showToast(getString(R.string.text_end_certified_err).replace("...", product.getId() + ""));
                break;
            default:
                dismiss();
                break;
        }
    }

    private void launchProductDetail() {
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra(Config.DATA_EXTRA, product.getId());
        startActivity(intent);
    }

    private void showDialogNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.text_note_product));

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);
        builder.setView(view);

        final TextView inputText = (TextView) view.findViewById(R.id.inputText);
        inputText.setHint(getString(R.string.text_hint_note_product));
        inputText.setText(product.getNote());

        builder.setPositiveButton(getString(R.string.btn_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestUpdateNote(inputText.getText().toString());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void requestUpdateNote(final String note) {
        ProductRequest.updateNote(product.getId(), note, new OnResponseListener() {
            @Override
            public void onSuccess(ResponseInfo info) {
                if (info != null && info.isSuccess()) {
                    product.setNote(note);
                    Toast.makeText(getActivity(), getString(R.string.text_update_success), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getActivity(), getString(R.string.err_request_api), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.err_request_api), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialogConfirm(String title, String msg, final int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton(getString(R.string.btn_continues), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(status);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

}
