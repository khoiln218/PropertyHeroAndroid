package com.gomicorp.propertyhero.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gomicorp.app.AppController;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.LoginActivity;
import com.gomicorp.propertyhero.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends Fragment {

    private TabLayout tabCollection;
    private ViewPager pagerCollection;
    private ViewPagerAdapter pagerCollectionAdapter;

    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_collection, container, false);

        tabCollection = (TabLayout) root.findViewById(R.id.tabCollection);
        pagerCollection = (ViewPager) root.findViewById(R.id.pagerCollection);

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (AppController.getInstance().getPrefManager().getUserID() == 0)
                startActivity(new Intent(getActivity(), LoginActivity.class));

            pagerCollection.setOffscreenPageLimit(2);
            pagerCollectionAdapter = new ViewPagerAdapter(getFragmentManager());
            pagerCollectionAdapter.addFragment(new ProductViewFragment(), getString(R.string.text_tab_recent_view));
            pagerCollectionAdapter.addFragment(new FavoriteFragment(), getString(R.string.text_tab_favorite));
            pagerCollection.setAdapter(pagerCollectionAdapter);

            tabCollection.post(new Runnable() {
                @Override
                public void run() {
                    tabCollection.setupWithViewPager(pagerCollection);
                }
            });
        }
    }
}
