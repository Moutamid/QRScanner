package com.moutamid.qr.scanner.generator.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

public class TextGenActivity extends AppCompatActivity {


    private EditText textedit;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_gen);
        textedit=findViewById(R.id.text_edit);
        historyVM = new ViewModelProvider(TextGenActivity.this).get(HistoryVM.class);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, TextGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
    }

    public void textgenerate(View view) {
        String data = textedit.getText().toString();


        if (data.equals("")) {
            textedit.setError("Please enter Text");
        } else {
            History textHistory = new History(data, "text");
            historyVM.insertHistory(textHistory);
            Intent intent = new Intent(TextGenActivity.this, ScanResultActivity.class);
            intent.putExtra("type", "Text");
            intent.putExtra("text", data);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
            finish();
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    textedit.setText(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(TextGenActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }


    public void backText(View view) {
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