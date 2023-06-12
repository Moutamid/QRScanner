package free.qr.code.scanner.generator.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.google.android.material.tabs.TabLayout;

import free.qr.code.scanner.generator.adapter.HistoryTabAdapter;
import free.qr.code.scanner.generator.interfaces.HistoryItemClickListner;

import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.adapter.HistoryAdapter;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.EMail;
import free.qr.code.scanner.generator.utils.formates.GeoInfo;
import free.qr.code.scanner.generator.utils.formates.IEvent;
import free.qr.code.scanner.generator.utils.formates.SMS;
import free.qr.code.scanner.generator.utils.formates.Social;
import free.qr.code.scanner.generator.utils.formates.Telephone;
import free.qr.code.scanner.generator.utils.formates.Url;
import free.qr.code.scanner.generator.utils.formates.VCard;
import free.qr.code.scanner.generator.utils.formates.Wifi;

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