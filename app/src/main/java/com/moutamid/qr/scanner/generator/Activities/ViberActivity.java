package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
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
import com.hbb20.CountryCodePicker;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;

import java.util.ArrayList;
import java.util.Locale;

public class ViberActivity extends AppCompatActivity {

    private TextInputLayout phonenumber;
    CountryCodePicker cpp;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_viber);

        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ViberActivity.this, mediatedBannerView);
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
        phonenumber = findViewById(R.id.edit_phone);
        cpp = findViewById(R.id.cpp);
        historyVM = new ViewModelProvider(ViberActivity.this).get(HistoryVM.class);
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

    public void viberGenerate(View view) {

        String data = cpp.getSelectedCountryCode() + phonenumber.getEditText().getText().toString();

        if (data.equals("")) {
            phonenumber.getEditText().setError("Please enter Number");
        } else {

            try {
                final Telephone telephone = new Telephone();
                telephone.setTelephone(data);
                if (history) {
                    History phoneHistory = new History(telephone.generateString(), "viber", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    historyList.add(phoneHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "viber");
                intent.putExtra("phone", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        phonenumber.getEditText().setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(ViberActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void backViber(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    public void openViber(View view) {
        PackageManager pm = getPackageManager();
        boolean isViberInstalled = false;
        try {
            isViberInstalled = pm.getPackageInfo("com.viber.voip", PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // If the Viber app is not installed, open the Viber website in a web browser.
        if (!isViberInstalled) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.viber.com"));
            startActivity(browserIntent);
        }

        // If the Viber app is installed, open the Viber app.
        else {
            Intent viberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("viber://"));
            startActivity(viberIntent);
        }
    }
}