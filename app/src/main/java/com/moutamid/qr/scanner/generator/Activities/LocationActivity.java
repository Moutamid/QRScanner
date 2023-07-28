package com.moutamid.qr.scanner.generator.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapInitOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import java.util.ArrayList;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextInputLayout latitude,longitude,locationname;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    MapView mapView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Plugin.Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
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

        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup(
                new NavigationOptions.Builder(this)
                        .accessToken(getResources().getString(R.string.mapbox_access_token))
                        .build()
            );
        }

        mapView.setVisibility(View.GONE);
        if (!hasLocationPermission()){
            requestLocationPermission();
        }


        locationname=findViewById(R.id.location_name);
        historyVM = new ViewModelProvider(LocationActivity.this).get(HistoryVM.class);
        getLocale();
    }

    private void dialog() {
        if (!isLocationEnabled()) {
            showAlert();
        } else {
            mapView.setVisibility(View.VISIBLE);
            showMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasLocationPermission()) {
            dialog();
        }
    }

    private void showAlert() {
        new AlertDialog.Builder(this)
                .setTitle("GPS is not enabled")
                .setMessage("To continue let your device turn on location.")
                .setPositiveButton("Open Settings", ((dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }))
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    dialog.dismiss();
                    mapView.setVisibility(View.GONE);
                }))
                .show();
    }

    private void addAnnotationToMap() {

    }

    private boolean hasLocationPermission() {
        // Check if the app has location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // If targeting API level lower than 23, permissions are granted at installation time
    }

    private void requestLocationPermission() {
        // Request location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean isLocationEnabled() {
        // Check if location services are enabled on the device
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted or not
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed with your location-related logic
                dialog();
            } else {
                mapView.setVisibility(View.GONE);
                // Location permission denied, handle this situation (e.g., show a message, disable location-based features)
            }
        }
    }

    private void showMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            currentLocation = location;
            latitude.getEditText().setText(currentLocation.getLatitude() + "");
            longitude.getEditText().setText("" + currentLocation.getLongitude());

            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(
                            Point.fromLngLat(
                                    currentLocation.getLongitude(),currentLocation.getLatitude()
                            )
                    )
                    .zoom(9.0)
                    .build();

            mapView = new MapView(this, new MapInitOptions(this));

        }).addOnFailureListener(e -> {
            latitude.getEditText().setText("40.7128");
            longitude.getEditText().setText("-74.0060");
        });
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Log.d("STYLELOADED", style.getStyleJSON());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
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

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
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
}