package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;

public class MenuFragment extends Fragment {

    LinearLayout barcodeBt,urlBt,textBt,wifiBt,emailBt,contactBt,locationBt,smsBt,facebook,
            youtubeBt,phoneBt,eventBt,whatsappBt,twitterBt,viberBt,spotifyBt,instaBt,paypalBt,cardBt,clipBt;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_menu, container, false);
        barcodeBt=view.findViewById(R.id.barcodeIcon);
        urlBt=view.findViewById(R.id.urlIcon);
        textBt=view.findViewById(R.id.textIcon);
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
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().LoadInterstitial();
        }

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
        return  view;
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}