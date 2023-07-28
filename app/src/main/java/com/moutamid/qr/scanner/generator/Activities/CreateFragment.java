package com.moutamid.qr.scanner.generator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.Fragments.CardsFragment;
import com.moutamid.qr.scanner.generator.Fragments.ScanFragment;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.HistoryTabAdapter;
import com.moutamid.qr.scanner.generator.qrscanner.History;

import java.util.ArrayList;
import java.util.List;

public class CreateFragment extends Fragment {
    EditText quick;
    ImageView quickBtn;
    View navLay;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    private SharedPreferences prefs;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.transparentStatusBar(false);

        quick=view.findViewById(R.id.quick);
        quickBtn=view.findViewById(R.id.quickBtn);
        navLay = view.findViewById(R.id.navLay);
        bottomNavigationView = navLay.findViewById(R.id.bottomNavigationView);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean history = prefs.getBoolean("saveHistory",true);
        quickBtn.setOnClickListener(v -> {
            if (quick.getText().toString().isEmpty()){
                Toast.makeText(requireContext(), "Text is empty", Toast.LENGTH_SHORT).show();
            } else {
                String data = quick.getText().toString();
                if (history) {
                    History textHistory = new History(data, "text", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    historyList.add(textHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(requireActivity(), ScanResultActivity.class);
                intent.putExtra("type", "Text");
                intent.putExtra("text", data);
                startActivity(intent);
                if (!Constants.getPurchaseSharedPreference(requireContext())) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, requireActivity());
                }
            }
        });


        bottomNavigationView.setSelectedItemId(R.id.generate_qr);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.scan:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new QRScanFragment()).commit();
                        break;
                    case R.id.generate_qr:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CreateFragment()).commit();
                        break;
                    case R.id.history:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HistoryActivity()).commit();
                        break;
                    case R.id.settings:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MySettingsFragment()).commit();
                        break;
                }
                return false;
            }
        });

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        CreateTabAdapter adapter = new CreateTabAdapter(getChildFragmentManager());

        adapter.addFragment(new MenuFragment(), "QRs");
        adapter.addFragment(new BusinessFragment(), "Business Cards");

        viewPager.setAdapter(adapter);
    }


    static class CreateTabAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public CreateTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return this.mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.mFragmentTitleList.get(position);
        }
    }


}