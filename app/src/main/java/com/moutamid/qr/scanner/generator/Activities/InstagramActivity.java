package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.Social;

import java.util.ArrayList;
import java.util.Locale;

public class InstagramActivity extends AppCompatActivity {

    private TextInputLayout link;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_instagram);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, InstagramActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = prefs.getBoolean("theme", false);
        history = prefs.getBoolean("saveHistory", true);
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
        link = findViewById(R.id.facebook_link);
        historyVM = new ViewModelProvider(InstagramActivity.this).get(HistoryVM.class);
        getLocale();
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

    public void instaGenerate(View view) {


        String urlValue = link.getEditText().getText().toString();
        if (link.getEditText().getText().toString().equals("")) {
            link.setError("PLease Enter Link");
        } else {
            try {
                final Social social = new Social();
                social.setUrl(urlValue);
                if (history) {
                    History urlHistory = new History(social.generateString(), "insta", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    historyList.add(urlHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "insta");
                intent.putExtra("social", social);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        link.getEditText().setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(InstagramActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backInsta(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    public void openInstagram(View view) {
        PackageManager pm = getPackageManager();
        boolean isInstagramInstalled = false;
        try {
            isInstagramInstalled = pm.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // If the Instagram app is not installed, open the Instagram website in a web browser.
        if (!isInstagramInstalled) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
            startActivity(browserIntent);
        }

        // If the Instagram app is installed, open the Instagram app.
        else {
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://"));
            startActivity(instagramIntent);
        }
    }
}