package com.moutamid.qr.scanner.generator.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.qr.scanner.generator.Activities.BarCodeActivity;
import com.moutamid.qr.scanner.generator.Activities.ClipboardActivity;
import com.moutamid.qr.scanner.generator.Activities.ContactGenActivity;
import com.moutamid.qr.scanner.generator.Activities.EmailGenActivity;
import com.moutamid.qr.scanner.generator.Activities.EventActivity;
import com.moutamid.qr.scanner.generator.Activities.FacebookActivity;
import com.moutamid.qr.scanner.generator.Activities.InstagramActivity;
import com.moutamid.qr.scanner.generator.Activities.LocationActivity;
import com.moutamid.qr.scanner.generator.Activities.MainActivity;
import com.moutamid.qr.scanner.generator.Activities.PayPalActivity;
import com.moutamid.qr.scanner.generator.Activities.PhoneActivity;
import com.moutamid.qr.scanner.generator.Activities.ScanResultActivity;
import com.moutamid.qr.scanner.generator.Activities.SmsGenActivity;
import com.moutamid.qr.scanner.generator.Activities.SpotifyActivity;
import com.moutamid.qr.scanner.generator.Activities.TextGenActivity;
import com.moutamid.qr.scanner.generator.Activities.TwitterActivity;
import com.moutamid.qr.scanner.generator.Activities.UrlGenActivity;
import com.moutamid.qr.scanner.generator.Activities.ViberActivity;
import com.moutamid.qr.scanner.generator.Activities.WhatsAppActivity;
import com.moutamid.qr.scanner.generator.Activities.WifiGenActivity;
import com.moutamid.qr.scanner.generator.Activities.YouTubeActivity;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.HistoryAdapter;
import com.moutamid.qr.scanner.generator.interfaces.HistoryItemClickListner;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Spotify;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateFragment extends Fragment implements HistoryItemClickListner {

    private HistoryVM historyVM;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter adapter;
    private LinearLayout tvIsEmpty;
    private boolean isEmpty = false;
    private SharedPreferences prefs;
    private MaterialButton deleteImg;

    RelativeLayout recyclerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        boolean theme = prefs.getBoolean("theme", false);
        if (theme) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

        } else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_fragment, container, false);
        historyVM = new ViewModelProvider(CreateFragment.this).get(HistoryVM.class);
        historyRecyclerView = view.findViewById(R.id.history_recyclerview);
        historyRecyclerView.setHasFixedSize(false);
        tvIsEmpty = view.findViewById(R.id.tv_is_empty);
        deleteImg = view.findViewById(R.id.all_delete_history);

        recyclerLayout = view.findViewById(R.id.recyclerLayout);
        Button scanNow = view.findViewById(R.id.scanNow);
        scanNow.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.create();
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllHistory();
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getLocale();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistoryData();
    }

    private void getLocale() {

        String lang = prefs.getString("lang", "");
        String name = prefs.getString("lang_name", "");
        //   languageTxt.setText(name);
        setLocale(lang, name);
    }

    private void setLocale(String lng, String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    List<History> historyList = new ArrayList<>();

    private void getHistoryData() {
        ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
        if (historyList.size() == 0) {
            tvIsEmpty.setVisibility(View.VISIBLE);
            recyclerLayout.setVisibility(View.GONE);
            isEmpty = true;
        } else {
//            Collections.reverse(historyList);
            tvIsEmpty.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(requireContext(), historyList, this);
            historyRecyclerView.setAdapter(adapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            historyRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public void editItem(String type, String history) {
        Intent intent;
        switch (type) {
            case "contact": {
                VCard vCard = new VCard();
                vCard.parseSchema(history);
                intent = new Intent(getActivity(), ContactGenActivity.class);
                intent.putExtra(Constants.passed, vCard);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "email": {
                EMail eMail = new EMail();
                eMail.parseSchema(history);
                intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra(Constants.passed, eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "event": {
                IEvent iEvent = new IEvent();
                iEvent.parseSchema(history);
                intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra(Constants.passed, iEvent);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "location": {
                GeoInfo geoInfo = new GeoInfo();
                geoInfo.parseSchema(history);
                intent = new Intent(getActivity(), LocationActivity.class);
                intent.putExtra(Constants.passed, geoInfo);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "phone": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent = new Intent(getActivity(), PhoneActivity.class);
                intent.putExtra(Constants.passed, telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "sms": {
                SMS sms = new SMS();
                sms.parseSchema(history);
                intent = new Intent(getActivity(), SmsGenActivity.class);
                intent.putExtra(Constants.passed, sms);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "whatsapp": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent = new Intent(getActivity(), WhatsAppActivity.class);
                intent.putExtra(Constants.passed, telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "spotify": {
                Spotify telephone = new Spotify();
                telephone.parseSchema(history);
                intent = new Intent(getActivity(), SpotifyActivity.class);
                intent.putExtra(Constants.passed, telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "viber": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent = new Intent(getActivity(), ViberActivity.class);
                intent.putExtra(Constants.passed, telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "barcode":
                intent = new Intent(getActivity(), BarCodeActivity.class);
                intent.putExtra(Constants.passed, history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "text":
                intent = new Intent(getActivity(), TextGenActivity.class);
                intent.putExtra(Constants.passed, history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "clipboard":
                intent = new Intent(getActivity(), ClipboardActivity.class);
                intent.putExtra(Constants.passed, history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "url": {
                Url url = new Url();
                url.parseSchema(history);
                intent = new Intent(getActivity(), UrlGenActivity.class);
                intent.putExtra(Constants.passed, url);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "wifi": {
                Wifi wifi = new Wifi();
                wifi.parseSchema(history);
                intent = new Intent(getActivity(), WifiGenActivity.class);
                intent.putExtra(Constants.passed, wifi);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "youtube":
                Social social = new Social();
                social.parseSchema(history);
                intent = new Intent(getActivity(), YouTubeActivity.class);
                intent.putExtra(Constants.passed, social);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "insta":
                Social social1 = new Social();
                social1.parseSchema(history);
                intent = new Intent(getActivity(), InstagramActivity.class);
                intent.putExtra(Constants.passed, social1);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "paypal":
                Social social2 = new Social();
                social2.parseSchema(history);
                intent = new Intent(getActivity(), PayPalActivity.class);
                intent.putExtra(Constants.passed, social2);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "facebook":
                Social social3 = new Social();
                social3.parseSchema(history);
                intent = new Intent(getActivity(), FacebookActivity.class);
                intent.putExtra(Constants.passed, social3);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "twitter":
                Social social4 = new Social();
                social4.parseSchema(history);
                intent = new Intent(getActivity(), TwitterActivity.class);
                intent.putExtra(Constants.passed, social4);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
        }
    }

    @Override
    public void clickedItem(View view, int position, String type, String history) {
        Intent intent = new Intent(getActivity(), ScanResultActivity.class);
        switch (type) {
            case "contact": {
                VCard vCard = new VCard();
                vCard.parseSchema(history);
                intent.putExtra("type", "VCard");
                intent.putExtra("vCard", vCard);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "email": {
                EMail eMail = new EMail();
                eMail.parseSchema(history);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "event": {
                IEvent iEvent = new IEvent();
                iEvent.parseSchema(history);
                intent.putExtra("type", "Event");
                intent.putExtra("event", iEvent);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "location": {
                GeoInfo geoInfo = new GeoInfo();
                geoInfo.parseSchema(history);
                intent.putExtra("type", "GeoInfo");
                intent.putExtra("geoInfo", geoInfo);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "phone": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent.putExtra("type", "telephone");
                intent.putExtra("phone", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "sms": {
                SMS sms = new SMS();
                sms.parseSchema(history);
                intent.putExtra("type", "Sms");
                intent.putExtra("sms", sms);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "whatsapp": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent.putExtra("type", "whatsapp");
                intent.putExtra("phone", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "spotify": {
                Spotify telephone = new Spotify();
                telephone.parseSchema(history);
                intent.putExtra("type", "spotify");
                intent.putExtra("spotify", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "viber": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent.putExtra("type", "viber");
                intent.putExtra("phone", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "barcode":
                intent.putExtra("type", "Barcode");
                intent.putExtra("barcode", history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "text":
                intent.putExtra("type", "Text");
                intent.putExtra("text", history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "clipboard":
                intent.putExtra("type", "clipboard");
                intent.putExtra("text", history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "url": {
                Url url = new Url();
                url.parseSchema(history);
                intent.putExtra("type", "url");
                intent.putExtra("Url", url);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "wifi": {
                Wifi wifi = new Wifi();
                wifi.parseSchema(history);
                intent.putExtra("type", "wifi");
                intent.putExtra("Wifi", wifi);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            }
            case "youtube":
                Social social = new Social();
                social.parseSchema(history);
                intent.putExtra("type", "youtube");
                intent.putExtra("social", social);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "insta":
                Social social1 = new Social();
                social1.parseSchema(history);
                intent.putExtra("type", "insta");
                intent.putExtra("social", social1);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "paypal":
                Social social2 = new Social();
                social2.parseSchema(history);
                intent.putExtra("type", "paypal");
                intent.putExtra("social", social2);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "facebook":
                Social social3 = new Social();
                social3.parseSchema(history);
                intent.putExtra("type", "facebook");
                intent.putExtra("social", social3);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "twitter":
                Social social4 = new Social();
                social4.parseSchema(history);
                intent.putExtra("type", "twitter");
                intent.putExtra("social", social4);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
        }
    }

    @Override
    public void deleteSingleItem(History history, int i) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Delete History");
        adb.setMessage("Are you sure you want to delete?");
        adb.setPositiveButton("Yes", (dialog, which) -> {
            ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
            historyList.remove(history);
            Stash.put(Constants.CREATE, historyList);
            adapter.notifyItemRemoved(i);
            if (historyList.size() == 0) {
                tvIsEmpty.setVisibility(View.VISIBLE);
                recyclerLayout.setVisibility(View.GONE);
                isEmpty = true;
            }
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        adb.setNegativeButton("No", (dialog, which) -> {
        });
        adb.show();

    }

    public void backHistory(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
        }

    }

    public void deleteAllHistory() {

        if (isEmpty) {
            Toast.makeText(getActivity(), "Nothing to delete!", Toast.LENGTH_SHORT).show();
        } else {

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Delete History");
            adb.setMessage("Are you sure you want to delete all the previous history?");
            adb.setPositiveButton("Yes", (dialog, which) -> {
                ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                historyList.clear();
                Stash.put(Constants.CREATE, historyList);
                adapter = new HistoryAdapter(requireContext(), historyList, this);
                historyRecyclerView.setAdapter(adapter);
                if (historyList.size() == 0) {
                    tvIsEmpty.setVisibility(View.VISIBLE);
                    recyclerLayout.setVisibility(View.GONE);
                    isEmpty = true;
                }
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
            });
            adb.setNegativeButton("No", (dialog, which) -> {
            });
            adb.show();
        }
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}
