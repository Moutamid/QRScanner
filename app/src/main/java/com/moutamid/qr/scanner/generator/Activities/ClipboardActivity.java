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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

import java.util.Locale;

public class ClipboardActivity extends AppCompatActivity {

    private TextInputLayout textedit;
    private HistoryVM historyVM;
    private ClipboardManager clipboardManager;
    private TextView textView;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private boolean copied =false;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard);
        textedit=findViewById(R.id.text_edit);
        prefs = PreferenceManager.getDefaultSharedPreferences(ClipboardActivity.this);
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
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        textView = findViewById(R.id.clipboard); // Replace with the actual ID of your TextView

        String copiedText = getTextFromClipboard();
        textView.setText(copiedText);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the desired action when the TextView is clicked
                textedit.getEditText().setText(copiedText);
            }
        });
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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

    private String getTextFromClipboard() {
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            ClipData.Item item = clipData.getItemAt(0);
            if (item != null) {
                CharSequence text = item.coerceToText(getApplicationContext());
                if (text != null) {
                    return text.toString();
                }
            }
        }
        return "Clipboard is empty" ;
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
        String data = textedit.getEditText().getText().toString();


        if (data.equals("")) {
            textedit.setError("Please enter Text");
        } else {
            if (history) {
                History textHistory = new History(data, "clipboard");
                historyVM.insertHistory(textHistory);
            }
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
                    textedit.getEditText().setText(null);
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