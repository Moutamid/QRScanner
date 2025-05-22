package com.moutamid.qr.scanner.generator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;



import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.Stash;

import java.util.ArrayList;
import java.util.Locale;

public class BarCodeActivity extends AppCompatActivity {

    String passed;
    private TextInputLayout editText;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_bar_code);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editText = findViewById(R.id.edit_barcode);
        history = prefs.getBoolean("saveHistory", true);
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

        passed = getIntent().getStringExtra(Constants.passed);

        if (passed != null) {
            editText.getEditText().setText(passed);
        }

        historyVM = new ViewModelProvider(BarCodeActivity.this).get(HistoryVM.class);
        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(this, BarCodeActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
        }
        getLocale();
    }


    private void getLocale() {

        String lang = prefs.getString("lang", "");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    public void barcodeGenerate(View view) {
        String barcodeText = editText.getEditText().getText().toString();
        if (barcodeText.equals("") || barcodeText.length() < 12) {
            editText.getEditText().setError(getString(R.string.please_enter_valid_barcode_text_should_be_12_or_13_digits_long));
        } else {
            if (history) {
                History contactHistory = new History(barcodeText, "barcode", false);
                ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                if (passed != null) {
                    for (int i = 0; i < historyList.size(); i++) {
                        if (historyList.get(i).getData().equals(passed)) {
                            historyList.set(i, contactHistory);
                        }
                    }
                } else
                    historyList.add(contactHistory);
                Stash.put(Constants.CREATE, historyList);
            }
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcode", barcodeText);
            startActivity(intent);
//            if (!getPurchaseSharedPreference()) {
//                //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
//            }
            finish();
        }
    }

    public void backBarcode(View view) {
        //if (!getPurchaseSharedPreference()) {
        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        //    }
        finish();

    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }

}