package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;

import java.util.ArrayList;
import java.util.Locale;

public class MenuFragment extends Fragment {

    LinearLayout barcodeBt,urlBt,textBt,wifiBt,emailBt,contactBt,locationBt,smsBt,facebook,
            youtubeBt,phoneBt,eventBt,whatsappBt,twitterBt,viberBt,spotifyBt,instaBt,paypalBt,cardBt,clipBt, businessCard;
    private SharedPreferences prefs;
    EditText quick;
    ImageView quickBtn;
    View navLay;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_menu, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.transparentStatusBar(false);

        barcodeBt=view.findViewById(R.id.barcodeIcon);
        businessCard=view.findViewById(R.id.businessCard);
        quick=view.findViewById(R.id.quick);
        quickBtn=view.findViewById(R.id.quickBtn);
        urlBt=view.findViewById(R.id.urlIcon);
        textBt=view.findViewById(R.id.textIcon);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        wifiBt=view.findViewById(R.id.wfiIcon);
        emailBt=view.findViewById(R.id.email_icon);
        contactBt=view.findViewById(R.id.contatc_icon);
        locationBt=view.findViewById(R.id.locattion_icon);
        smsBt=view.findViewById(R.id.smsIcon);
        youtubeBt=view.findViewById(R.id.youtubr_icon);
        phoneBt=view.findViewById(R.id.phone_icon);
        eventBt=view.findViewById(R.id.event_icon);
        youtubeBt=view.findViewById(R.id.youtubr_icon);
        phoneBt=view.findViewById(R.id.phone_icon);
        whatsappBt=view.findViewById(R.id.whatsapp_icon);
        instaBt=view.findViewById(R.id.insta_icon);
        facebook=view.findViewById(R.id.fb_icon);
        clipBt=view.findViewById(R.id.clip_icon);
        twitterBt=view.findViewById(R.id.twitter_icon);
        paypalBt=view.findViewById(R.id.paypal_icon);
        cardBt=view.findViewById(R.id.card_icon);
        viberBt=view.findViewById(R.id.viber_icon);
        spotifyBt=view.findViewById(R.id.spotify_icon);
        navLay = view.findViewById(R.id.navLay);
        bottomNavigationView = navLay.findViewById(R.id.bottomNavigationView);

        boolean history = prefs.getBoolean("saveHistory",true);
        quickBtn.setOnClickListener(v -> {
            if (quick.getText().toString().isEmpty()){
                Toast.makeText(mainActivity, "Text is empty", Toast.LENGTH_SHORT).show();
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
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, requireActivity());
                }
            }
        });

        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().LoadInterstitial();
        }

        businessCard.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new BusinessFragment()).commit();
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
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MenuFragment()).commit();
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

        barcodeBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),BarCodeActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });

        urlBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),UrlGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });

        textBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),TextGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });

        wifiBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),WifiGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });

        emailBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),EmailGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        contactBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),ContactGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        locationBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),LocationActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        smsBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),SmsGenActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        youtubeBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),YouTubeActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        phoneBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),PhoneActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        eventBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),EventActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        twitterBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),TwitterActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });

        facebook.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),FacebookActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        viberBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),ViberActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        clipBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),ClipboardActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        instaBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),InstagramActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        whatsappBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),WhatsAppActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        spotifyBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),SpotifyActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        paypalBt.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),PayPalActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        getLocale();
        return  view;
    }



    private void getLocale(){

        String lang = prefs.getString("lang","");
        String name = prefs.getString("lang_name","");
        //   languageTxt.setText(name);
        setLocale(lang,name);
    }

    private void setLocale(String lng,String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());

    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}