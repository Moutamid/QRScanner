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
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.Spotify;

import java.util.ArrayList;
import java.util.Locale;

public class SpotifyActivity extends AppCompatActivity {

    private TextInputLayout name, song;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_spotify);
        name = findViewById(R.id.artist_name);
        song = findViewById(R.id.song);
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
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, SpotifyActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(SpotifyActivity.this).get(HistoryVM.class);
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

    public void spotifyGenerate(View view) {
        if (name.getEditText().getText().toString().equals("")) {
            name.setError("Please enter Artist Name");
        } else if (song.getEditText().getText().toString().equals("")) {
            song.setError("Please enter Song");
        } else {
            try {
                final Spotify spotify = new Spotify();
                spotify.setName(name.getEditText().getText().toString());
                if (history) {
                    History emailHistory = new History(spotify.generateString(), "spotify", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    historyList.add(emailHistory);
                    Stash.put(Constants.CREATE, historyList);
                }
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
                        name.getEditText().setText(null);
                        song.getEditText().setText(null);
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

    public void backSpotify(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    public void openSpotify(View view) {
        PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled = false;
        try {
            isSpotifyInstalled = pm.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!isSpotifyInstalled) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com"));
            startActivity(browserIntent);
        } else {
            Intent spotifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify://"));
            startActivity(spotifyIntent);
        }
    }
}