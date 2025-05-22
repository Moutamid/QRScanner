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



import com.moutamid.qr.scanner.generator.utils.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.Social;

import java.util.ArrayList;
import java.util.Locale;

public class PayPalActivity extends AppCompatActivity {

    private TextInputLayout link;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    Social passed;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_pay_pal);
        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, PayPalActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
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

        passed = (Social) getIntent().getSerializableExtra(Constants.passed);

        if (passed != null)
            link.getEditText().setText(passed.getUrl());

        historyVM = new ViewModelProvider(PayPalActivity.this).get(HistoryVM.class);
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

    public void paypalGenerate(View view) {


        String urlValue = "https://" + link.getEditText().getText().toString();
        if (link.getEditText().getText().toString().equals("")) {
            link.setError("PLease Enter Link");
        } else {
            try {
                final Social social = new Social();
                social.setUrl(urlValue);
                if (history) {
                    History urlHistory = new History(social.generateString(), "paypal", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    if (passed != null) {
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getData().equals(passed.generateString())){
                                historyList.set(i, urlHistory);
                            }
                        }
                    } else
                        historyList.add(urlHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "paypal");
                intent.putExtra("social", social);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
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
                    ActivityCompat.requestPermissions(PayPalActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backPayPal(View view) {
        //if (!getPurchaseSharedPreference()) {
  //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
    //    }
        finish();
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    public void openPaypal(View view) {
        PackageManager pm = getPackageManager();
        boolean isPayPalInstalled = false;
        try {
            isPayPalInstalled = pm.getPackageInfo("com.paypal.android.p2pmobile", PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // If the PayPal app is not installed, open the PayPal website in a web browser.
        if (!isPayPalInstalled) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com"));
            startActivity(browserIntent);
        }

        // If the PayPal app is installed, open the PayPal app.
        else {
            Intent paypalIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("pp://"));
            startActivity(paypalIntent);
        }
    }
}