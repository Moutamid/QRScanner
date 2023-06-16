package com.moutamid.qr.scanner.generator.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.google.android.material.tabs.TabLayout;

import com.moutamid.qr.scanner.generator.adapter.HistoryTabAdapter;

import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

public class HistoryActivity extends Fragment {

    private HistoryVM historyVM;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_history, container, false);
        CAMediatedBannerView mediatedBannerView = view.findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, getActivity(), mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(HistoryActivity.this).get(HistoryVM.class);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("SCAN"));
        tabLayout.addTab(tabLayout.newTab().setText("CREATE"));
        tabLayout.addTab(tabLayout.newTab().setText("CARDS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final HistoryTabAdapter adapter = new HistoryTabAdapter(getActivity(),getFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return view;
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}