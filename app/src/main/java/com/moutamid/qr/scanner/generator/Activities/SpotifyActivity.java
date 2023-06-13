package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.Spotify;

public class SpotifyActivity extends AppCompatActivity {

    private EditText name,song;
    private HistoryVM historyVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        name=findViewById(R.id.artist_name);
        song=findViewById(R.id.song);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, SpotifyActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(SpotifyActivity.this).get(HistoryVM.class);
    }

    public void emailGenerate(View view) {
        if (name.getText().toString().equals("")) {
            name.setError("Please enter Artist Name");
        } else if (song.getText().toString().equals("")) {
            song.setError("Please enter Song");
        } else {
            try {
                final Spotify spotify = new Spotify();
                spotify.setName(name.getText().toString());
                History emailHistory = new History(spotify.generateString(), "spotify");
                historyVM.insertHistory(emailHistory);
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "spotify");
                intent.putExtra("spotify", spotify);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        name.setText(null);
                        song.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(SpotifyActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backEmail(View view) {
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