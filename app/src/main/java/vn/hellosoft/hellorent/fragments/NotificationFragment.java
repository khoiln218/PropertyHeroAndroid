package vn.hellosoft.hellorent.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.activities.GiftCardActivity;
import vn.hellosoft.hellorent.activities.LoginActivity;
import vn.hellosoft.hellorent.adapters.NotifiListAdapter;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.hellorent.extras.EndPoints;
import vn.hellosoft.hellorent.extras.UrlParams;
import vn.hellosoft.hellorent.json.Parser;
import vn.hellosoft.hellorent.model.GiftCard;
import vn.hellosoft.ui.DividerItemDecoration;

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

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
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
