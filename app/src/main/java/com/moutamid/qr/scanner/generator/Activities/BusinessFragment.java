package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.CardMainAdapter;
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Integer> cardList;
    private SharedPreferences prefs;
    ImageButton close;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean theme = prefs.getBoolean("theme",false);
        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.business_fragment, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.transparentStatusBar(false);

        recyclerView = view.findViewById(R.id.recyclerView);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cardList = new ArrayList<>();
        loadData();
        getLocale();
        return view;
    }


    private void getLocale(){

        String lang = prefs.getString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getResources().updateConfiguration(configuration,getActivity().getResources().getDisplayMetrics());
    }


    private void loadData() {
        cardList.add(R.drawable.ic_card_calender_1);
        cardList.add(R.drawable.ic_card_tel_1);
        cardList.add(R.drawable.ic_card_email_2);
        cardList.add(R.drawable.ic_card_txt_2);
        cardList.add(R.drawable.ic_card_geo_2);
        cardList.add(R.drawable.ic_card_url_2);
        cardList.add(R.drawable.ic_card_url_3);
        cardList.add(R.drawable.ic_card_wifi_1);
        cardList.add(R.drawable.ic_card_social_2);
        cardList.add(R.drawable.ic_card_social_3);
        cardList.add(R.drawable.ic_card_sms_2);
        cardList.add(R.drawable.ic_card_email_3);

        CardMainAdapter adapter = new CardMainAdapter(cardList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setButtonItemClickListener(new ButtonItemClickListener() {
            @Override
            public void clickedItem(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), CardCalendarActivity.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(getActivity(), CardTelActivity.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(getActivity(), CardEmailActivity.class);
                    startActivity(intent);
                }
                if (position == 3) {
                    Intent intent = new Intent(getActivity(), CardTxtActivity.class);
                    startActivity(intent);
                }
                if (position == 4) {
                    Intent intent = new Intent(getActivity(), CardGeoActivity.class);
                    startActivity(intent);
                }
                if (position == 5) {
                    Intent intent = new Intent(getActivity(), CardUrlActivity.class);
                    startActivity(intent);
                }
                if (position == 6) {
                    Intent intent = new Intent(getActivity(), CardUrl2Activity.class);
                    startActivity(intent);
                }
                if (position == 7) {
                    Intent intent = new Intent(getActivity(), CardWiFiActivity.class);
                    startActivity(intent);
                }
                if (position == 8) {
                    Intent intent = new Intent(getActivity(), CardSocialActivity.class);
                    startActivity(intent);
                }
                if (position == 9) {
                    Intent intent = new Intent(getActivity(), CardSocial2Activity.class);
                    startActivity(intent);
                }
                if (position == 10) {
                    Intent intent = new Intent(getActivity(), CardSMSActivity.class);
                    startActivity(intent);
                }
                if (position == 11) {
                    Intent intent = new Intent(getActivity(), CardEmail2Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void clickedItemButton(View view, int position, String type) {

            }
        });
    }
}
