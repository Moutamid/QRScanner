package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

import java.util.Locale;

public class ClipboardActivity extends AppCompatActivity {

    private EditText textedit;
    private HistoryVM historyVM;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private boolean copied =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard);
        textedit=findViewById(R.id.text_edit);
        prefs = PreferenceManager.getDefaultSharedPreferences(ClipboardActivity.this);
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
        edit = prefs.edit();
        copied = prefs.getBoolean("copy",false);
        historyVM = new ViewModelProvider(ClipboardActivity.this).get(HistoryVM.class);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ClipboardActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
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

    public void clipGenerate(View view) {
        String data = textedit.getText().toString();


        if (data.equals("")) {
            textedit.setError("Please enter Text");
        } else {
            History textHistory = new History(data, "clipboard");
            historyVM.insertHistory(textHistory);
            Intent intent = new Intent(ClipboardActivity.this, ScanResultActivity.class);
            intent.putExtra("type", "clipboard");
            intent.putExtra("text", data);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }

            if (copied){
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText("COUPON",data));
                Toast.makeText(this, manager.getPrimaryClip().getItemAt(0).getText().toString(), Toast.LENGTH_SHORT).show();
            }
            finish();
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    textedit.setText(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(ClipboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }


    public void backClipboard(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

}