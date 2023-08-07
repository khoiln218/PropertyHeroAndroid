package com.gomicorp.propertyhero.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private TabLayout tabSearch;
    private ViewPager pagerSearch;
    private ViewPagerAdapter pagerSearchAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        tabSearch = (TabLayout) root.findViewById(R.id.tabSearch);
        pagerSearch = (ViewPager) root.findViewById(R.id.pagerSearch);

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            pagerSearch.setOffscreenPageLimit(2);
            pagerSearchAdapter = new ViewPagerAdapter(getFragmentManager());
            pagerSearchAdapter.addFragment(new SearchLocationFragment(), getString(R.string.text_tab_location));
            pagerSearchAdapter.addFragment(new SearchMarkerFragment(), getString(R.string.text_tab_marker));
            pagerSearch.setAdapter(pagerSearchAdapter);

            tabSearch.post(new Runnable() {
                @Override
                public void run() {
                    tabSearch.setupWithViewPager(pagerSearch);
                }
            });
        }
    }
}
