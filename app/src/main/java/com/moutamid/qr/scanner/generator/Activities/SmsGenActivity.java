package com.moutamid.qr.scanner.generator.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;



import com.moutamid.qr.scanner.generator.utils.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;

import java.util.ArrayList;
import java.util.Locale;

public class SmsGenActivity extends AppCompatActivity {

    private TextInputLayout number,message;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    SMS passed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_sms_gen);
        number=findViewById(R.id.phone_number);
        message=findViewById(R.id.text_message);
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

        passed = (SMS) getIntent().getSerializableExtra(Constants.passed);

        if (passed != null) {
            number.getEditText().setText(passed.getNumber());
            message.getEditText().setText(passed.getSubject());
        }

        historyVM = new ViewModelProvider(SmsGenActivity.this).get(HistoryVM.class);
        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, SmsGenActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
        }
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
    public void smsGenerate(View view) {

        String data1 = number.getEditText().getText().toString();
        String data2 = message.getEditText().getText().toString();
        if (data1.equals("")) {
            number.setError("Please enter Number");
        } else if (data2.equals("")) {
            message.setError("Please enter Message");
        } else {
            try {
                final SMS sms = new SMS();
                sms.setNumber(number.getEditText().getText().toString());
                sms.setSubject(message.getEditText().getText().toString());
                if (history) {
                    History smsHistory = new History(sms.generateString(), "sms", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    if (passed != null) {
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getData().equals(passed.generateString())){
                                historyList.set(i, smsHistory);
                            }
                        }
                    } else
                        historyList.add(smsHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "Sms");
                intent.putExtra("sms", sms);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();


                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        message.getEditText().setText(null);
                        number.getEditText().setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(SmsGenActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void backSms(View view) {
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