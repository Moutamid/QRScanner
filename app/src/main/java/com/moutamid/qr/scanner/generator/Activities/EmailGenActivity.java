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
import com.moutamid.qr.scanner.generator.utils.formates.EMail;

import java.util.ArrayList;
import java.util.Locale;

public class EmailGenActivity extends AppCompatActivity {
    private TextInputLayout email,subject,body;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    EMail passed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_email_gen);
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
        email=findViewById(R.id.email);
        body=findViewById(R.id.email_body);
        subject=findViewById(R.id.email_subject);

        passed = (EMail) getIntent().getSerializableExtra(Constants.passed);

        if (passed!=null){
            email.getEditText().setText(passed.getEmail());
            body.getEditText().setText(passed.getMailBody());
            subject.getEditText().setText(passed.getMailSubject());
        }

        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, EmailGenActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(EmailGenActivity.this).get(HistoryVM.class);
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

    public void emailGenerate(View view) {
        if (email.getEditText().getText().toString().equals("")) {
            email.setError("Please enter Email");
        } else if (subject.getEditText().getText().toString().equals("")) {
            subject.setError("Please enter Subject");
        } else if (body.getEditText().getText().toString().equals("")) {
            body.setError("Please enter Body");
        } else {
            try {
                final EMail eMail = new EMail();
                eMail.setEmail(email.getEditText().getText().toString());
                eMail.setMailBody(body.getEditText().getText().toString());
                eMail.setMailSubject(subject.getEditText().getText().toString());
                if (history) {
                    History emailHistory = new History(eMail.generateString(), "email", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    if (passed != null) {
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getData().equals(passed.generateString())){
                                historyList.set(i, emailHistory);
                            }
                        }
                    } else
                        historyList.add(emailHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
//                    //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        email.getEditText().setText(null);
                        body.getEditText().setText(null);
                        subject.getEditText().setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(EmailGenActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backEmail(View view) {
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
