package com.moutamid.qr.scanner.generator.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moutamid.qr.scanner.generator.Fragments.CardsFragment;
import com.moutamid.qr.scanner.generator.Fragments.CreateFragment;
import com.moutamid.qr.scanner.generator.Fragments.ScanFragment;

public class HistoryTabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public HistoryTabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ScanFragment scanFragment = new ScanFragment();
                return scanFragment;
            case 1:
                CreateFragment createFragment = new CreateFragment();
                return createFragment;

            case 2:
                CardsFragment cardsFragment = new CardsFragment();
                return cardsFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
