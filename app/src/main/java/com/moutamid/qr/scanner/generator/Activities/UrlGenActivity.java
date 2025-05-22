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
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;



import com.moutamid.qr.scanner.generator.utils.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.Url;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlGenActivity extends AppCompatActivity {
    private TextInputLayout urledit;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    Url passed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_url_gen);

        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, UrlGenActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = prefs.getBoolean("theme",false);
        history = prefs.getBoolean("saveHistory",true);
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
        urledit = findViewById(R.id.url_edit);

        passed = (Url) getIntent().getSerializableExtra(Constants.passed);

        if (passed!=null){
            urledit.getEditText().setText(passed.getUrl());
        }

        findViewById(R.id.http).setOnClickListener(v -> {
            urledit.getEditText().setText("http://");
            urledit.getEditText().setSelection(urledit.getEditText().getText().length());
        });
        findViewById(R.id.https).setOnClickListener(v -> {
            urledit.getEditText().setText("https://");
            urledit.getEditText().setSelection(urledit.getEditText().getText().length());
        });
        findViewById(R.id.www).setOnClickListener(v -> {
            urledit.getEditText().append( "www.");
        });

        findViewById(R.id.com).setOnClickListener(v -> {
           // String s = urledit.getEditText().getText().toString();
            urledit.getEditText().append(".com");
        });


        historyVM = new ViewModelProvider(UrlGenActivity.this).get(HistoryVM.class);
        getLocale();
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

    public void urlGenerate(View view) {
        String urlValue = urledit.getEditText().getText().toString();
        if (urledit.getEditText().getText().toString().equals("")) {
            urledit.setError("PLease Enter Url");
        } else {
            String regex = "((http|https)://)(www.)?"
                    + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                    + "{2,256}\\.[a-z]"
                    + "{2,6}\\b([-a-zA-Z0-9@:%"
                    + "._\\+~#?&//=]*)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(urlValue);
            if (!m.matches()){
                Toast.makeText(this, "URL Pattern is not valid", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    final Url url = new Url();
                    url.setUrl(urlValue);
                    if (history) {
                        History urlHistory = new History(url.generateString(), "url", false);
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
                    intent.putExtra("type", "url");
                    intent.putExtra("Url", url);
                    startActivity(intent);
                    if (!getPurchaseSharedPreference()) {
                        //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                    }
                    finish();

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            urledit.getEditText().setText(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ActivityCompat.requestPermissions(UrlGenActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public void backUrl(View view) {
        //if (!getPurchaseSharedPreference()) {
  //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
    //    }
        finish();
    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}