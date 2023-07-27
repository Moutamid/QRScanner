package com.moutamid.qr.scanner.generator.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import java.util.ArrayList;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputLayout latitude,longitude,locationname;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_location);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, LocationActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
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
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationname=findViewById(R.id.location_name);
        historyVM = new ViewModelProvider(LocationActivity.this).get(HistoryVM.class);
        getLocale();
    }
    private void addAnnotationToMap() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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

    public void locationGenerate(View view) {

        String data1 = locationname.getEditText().getText().toString();
        String data2 = longitude.getEditText().getText().toString();
        String data3 = latitude.getEditText().getText().toString();

        if (data3.equals("")) {
            latitude.setError("Please enter Latitude");
        } else if (data2.equals("")) {
            longitude.setError("Please enter Longitude");
        } else if (data1.equals("")) {
            locationname.setError("Please enter Location Name");
        } else {
            try {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(locationname.getEditText().getText().toString());
                arrayList.add(latitude.getEditText().getText().toString());
                arrayList.add(longitude.getEditText().getText().toString());
                final GeoInfo location = new GeoInfo();
                location.setPoints(arrayList);
                if (history) {
                    History locHistory = new History(location.generateString(), "location", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    historyList.add(locHistory);
                    Stash.put(Constants.CREATE, historyList);
                }

                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "GeoInfo");
                intent.putExtra("geoInfo", location);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationname.getEditText().setText(null);
                        latitude.getEditText().setText(null);
                        longitude.getEditText().setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backLocation(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        OfflineManager offlineManager = OfflineManager.getInstance(LocationActivity.this);

// Define the region for offline download
/*
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapboxMap.getStyleUrl(),
                mapboxMap.getProjection().getVisibleRegion().latLngBounds,
                mapboxMap.getCameraPosition().zoom,
                10
        );
*/

// Generate a unique region name for storing in the database
        String regionName = "YOUR_UNIQUE_REGION_NAME";
        Log.d("MAPBOX", "regionName " + regionName);

// Kick off the download process
        /*offlineManager.createOfflineRegion(
                definition,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        // Start the download
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                    }

                    @Override
                    public void onError(String error) {
                        // Handle any errors that occur during the download process
                    }
                }
        );*/
    }
}