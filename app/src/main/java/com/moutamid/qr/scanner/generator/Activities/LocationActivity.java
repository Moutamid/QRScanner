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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;



import com.moutamid.qr.scanner.generator.utils.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextInputLayout latitude, longitude, locationname;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    private View mapView;
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment mapFragment;
    GeoInfo passed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
//        Plugin.Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_location);
        
        if (!getPurchaseSharedPreference()) {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, LocationActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
        }
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
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        mapView = findViewById(R.id.mapView);

        mapView.setVisibility(View.GONE);
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }

        locationname = findViewById(R.id.location_name);

        passed = (GeoInfo) getIntent().getSerializableExtra(Constants.passed);

        historyVM = new ViewModelProvider(LocationActivity.this).get(HistoryVM.class);
        getLocale();
    }

    private void dialog() {

        if (!isLocationEnabled()) {
            showAlert();
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(mapReady);
            mapView.setVisibility(View.VISIBLE);

            if (passed != null) {
                locationname.getEditText().setText(passed.getPoints().get(0));
                latitude.getEditText().setText(passed.getPoints().get(1));
                longitude.getEditText().setText(passed.getPoints().get(2));

                if (mapView.getVisibility() == View.VISIBLE && mMap != null){
                    LatLng sydney = new LatLng(Double.parseDouble(passed.getPoints().get(1)), Double.parseDouble(passed.getPoints().get(2)));
                    mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Touch and Hold to move marker"));
                }
            }

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

    private boolean hasLocationPermission() {
        // Check if the app has location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // If targeting API level lower than 23, permissions are granted at installation time
    }

    private void requestLocationPermission() {
        // Request location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean isLocationEnabled() {
        // Check if location services are enabled on the device
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
                    if (passed != null) {
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getData().equals(passed.generateString())) {
                                historyList.set(i, locHistory);
                            }
                        }
                    } else
                        historyList.add(locHistory);
                    Stash.put(Constants.CREATE, historyList);
                }

                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "GeoInfo");
                intent.putExtra("geoInfo", location);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
//                    //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
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
        //if (!getPurchaseSharedPreference()) {
  //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
    //    }
        finish();
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    OnMapReadyCallback mapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;


            mMap.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            Task<Location> task = fusedLocationProviderClient.getLastLocation();

            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    if (passed == null) {
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Touch and Hold to move marker"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        latitude.getEditText().setText(currentLocation.getLatitude() + "");
                        longitude.getEditText().setText("" + currentLocation.getLongitude());
                    }
                }
            }).addOnFailureListener(e -> {
                latitude.getEditText().setText("40.7128");
                longitude.getEditText().setText("-74.0060");
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    latitude.getEditText().setText(getFormatted(latLng.latitude));
                    longitude.getEditText().setText(getFormatted(latLng.longitude));
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    latitude.getEditText().setText(getFormatted(latLng.latitude));
                    longitude.getEditText().setText(getFormatted(latLng.longitude));
                }
            });
        }
    };

    private String getFormatted(double pos) {
        DecimalFormat df = new DecimalFormat("#.######");
        return df.format(pos);
    }
}