package com.gomicorp.propertyhero.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomicorp.app.AppController;
import com.gomicorp.app.Config;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.GiftCardActivity;
import com.gomicorp.propertyhero.activities.LoginActivity;
import com.gomicorp.propertyhero.adapters.NotifiListAdapter;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;
import com.gomicorp.propertyhero.extras.EndPoints;
import com.gomicorp.propertyhero.extras.UrlParams;
import com.gomicorp.propertyhero.json.Parser;
import com.gomicorp.propertyhero.model.GiftCard;
import com.gomicorp.ui.DividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();

    private RelativeLayout resultLayout;
    private RecyclerView recyclerView;
    private List<GiftCard> cardList;
    private NotifiListAdapter adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notification, container, false);

        resultLayout = (RelativeLayout) root.findViewById(R.id.resultLayout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerNotification);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getActivity(), recyclerView, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), GiftCardActivity.class);
                intent.putExtra(Config.DATA_EXTRA, cardList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        cardList = new ArrayList<>();
        adapter = new NotifiListAdapter(cardList, recyclerView);
        recyclerView.setAdapter(adapter);

        if (AppController.getInstance().getPrefManager().getUserID() != 0) {
            cardList.add(null);
            fetchNotificationData();
        } else
            resultLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (AppController.getInstance().getPrefManager().getUserID() == 0)
                startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void fetchNotificationData() {
        String url = EndPoints.URL_GET_GIFT_CARD
                .replace(UrlParams.ACCOUNT_ID, String.valueOf(AppController.getInstance().getPrefManager().getUserID()));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cardList.clear();
                cardList.addAll(Parser.giftCardList(response));
                adapter.notifyDataSetChanged();

                if (cardList.size() > 0)
                    resultLayout.setVisibility(View.GONE);
                else
                    resultLayout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error at fetchNotificationData()");
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }
}
