package com.gomicorp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.ListViewProductActivity;
import com.gomicorp.propertyhero.activities.UpdatePhoneNumberActivity;
import com.gomicorp.propertyhero.model.Marker;
import com.gomicorp.propertyhero.model.SearchInfo;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class L {
    public static void showAlert(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("OK", null);
        builder.show();
    }

    public static ProgressDialog progressDialog(Context context, String title, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }

    public static void launchUpdatePhone(final Activity activity, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_update_phone));
        builder.setMessage(activity.getString(R.string.msg_update_phone));
        builder.setNegativeButton(activity.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish)
                    activity.finish();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(activity.getString(R.string.btn_continues), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(activity, UpdatePhoneNumberActivity.class));
                if (isFinish)
                    activity.finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showSelectDistance(final Activity activity, final Marker obj) {
        String[] distanceText = activity.getResources().getStringArray(R.array.distance_text);
        final int[] distanceValue = activity.getResources().getIntArray(R.array.distance_value);
        final int[] distanceSelected = {distanceValue[1]};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(obj.getName());
        builder.setSingleChoiceItems(distanceText, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                distanceSelected[0] = distanceValue[which];
            }
        });

        builder.setNegativeButton(activity.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(activity.getString(R.string.btn_continues), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, ListViewProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.STRING_DATA, obj.getName() + " " + distanceSelected[0] + "km");
                bundle.putParcelable(Config.PARCELABLE_DATA, new SearchInfo(obj.getLatitude(), obj.getLongitude(), distanceSelected[0], Config.UNDEFINED));
                intent.putExtra(Config.DATA_EXTRA, bundle);
                activity.startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showToast(String msg) {
        Toast.makeText(AppController.getInstance().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
