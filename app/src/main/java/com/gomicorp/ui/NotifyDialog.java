package com.gomicorp.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.model.Notify;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by CTO-HELLOSOFT on 5/19/2016.
 */
public class NotifyDialog extends DialogFragment {

    public NotifyDialog() {
    }

    public static NotifyDialog instance(Notify notify) {
        NotifyDialog dialog = new NotifyDialog();
        Bundle args = new Bundle();
        args.putParcelable(Config.DATA_EXTRA, notify);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setContentView(R.layout.dialog_notify);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);
        Notify data = getArguments().getParcelable(Config.DATA_EXTRA);
        if (data == null)
            dismiss();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.notifyThumb);

        Picasso.with(getActivity())
                .load(data.getThumbnail())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        setCancelable(true);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

        return dialog;
    }
}
