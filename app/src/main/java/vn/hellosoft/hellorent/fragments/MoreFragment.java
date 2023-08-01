package vn.hellosoft.hellorent.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.CircleTransform;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.activities.AccountDetailsActivity;
import vn.hellosoft.hellorent.activities.ContactActivity;
import vn.hellosoft.hellorent.activities.LoginActivity;
import vn.hellosoft.hellorent.activities.ManagementProductActivity;
import vn.hellosoft.hellorent.activities.SettingsActivity;
import vn.hellosoft.hellorent.callbacks.OnAccountRequestListener;
import vn.hellosoft.hellorent.json.AccountRequest;
import vn.hellosoft.hellorent.model.Account;
import vn.hellosoft.helper.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MoreFragment.class.getSimpleName();

    private ImageView imgAvatar;
    private TextView tvFullName, tvUserName;
    private RelativeLayout progressLayout;

    private Account account;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_more, container, false);

        imgAvatar = (ImageView) root.findViewById(R.id.imgAvatar);
        tvFullName = (TextView) root.findViewById(R.id.tvFullName);
        tvUserName = (TextView) root.findViewById(R.id.tvUserName);
        progressLayout = (RelativeLayout) root.findViewById(R.id.progressLayout);

        root.findViewById(R.id.btnAccount).setOnClickListener(this);
        root.findViewById(R.id.btnRating).setOnClickListener(this);
        root.findViewById(R.id.btnContact).setOnClickListener(this);
        root.findViewById(R.id.btnManagement).setOnClickListener(this);
        root.findViewById(R.id.btnSettings).setOnClickListener(this);

        setupUI();

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            setupUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_LOGIN && resultCode == Config.SUCCESS_RESULT)
            setupUI();
    }

    private void setupUI() {
        progressLayout.setVisibility(View.VISIBLE);
        tvFullName.setText(AppController.getInstance().getPrefManager().getFullName());
        if (AppController.getInstance().getPrefManager().getUserID() != 0) {
            AccountRequest.getDetails(AppController.getInstance().getPrefManager().getUserID(), new OnAccountRequestListener() {
                @Override
                public void onSuccess(List<Account> accounts) {
                    if (accounts.size() > 0) {
                        account = accounts.get(0);
                        String userName = account.getAccType() == Config.HELLO_RENT ? account.getUserName() : account.getEmail();
                        AppController.getInstance().getPrefManager().addUserInfo(account.getId(), userName, account.getFullName(), account.getPhoneNumber(), account.getAccRole(), null);
                        tvFullName.setText(account.getFullName());
                        tvUserName.setVisibility(View.VISIBLE);

                        Picasso.with(getActivity())
                                .load(account.getAvatar())
                                .placeholder(R.drawable.default_avatar)
                                .transform(new CircleTransform())
                                .into(imgAvatar);

                        progressLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    L.showToast(getString(R.string.request_time_out));
                }
            });
        } else {
            imgAvatar.setImageResource(R.drawable.vector_action_login);
            tvFullName.setText(getString(R.string.text_login));
            tvUserName.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccount:
                if (account == null) {
                    ActivityCompat.startActivityForResult(requireActivity(), new Intent(getActivity(), LoginActivity.class), Config.REQUEST_LOGIN, null);
                } else {
                    Intent accDetails = new Intent(getActivity(), AccountDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.AVATAR_URL, account.getAvatar());
                    bundle.putInt(Config.ACCOUNT_TYPE, account.getAccType());
                    accDetails.putExtra(Config.DATA_EXTRA, bundle);
                    startActivity(accDetails);
                }
                break;
            case R.id.btnRating:
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.btnContact:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                break;
            case R.id.btnManagement:
                if (AppController.getInstance().getPrefManager().getUserID() == 0) {
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.STRING_DATA, ManagementProductActivity.class.getSimpleName());
                    intentLogin.putExtra(Config.DATA_EXTRA, bundle);
                    startActivity(intentLogin);
                } else
                    startActivity(new Intent(getActivity(), ManagementProductActivity.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
    }
}
