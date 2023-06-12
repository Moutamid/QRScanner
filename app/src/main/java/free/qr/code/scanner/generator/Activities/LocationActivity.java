package free.qr.code.scanner.generator.Activities;

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
import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.GeoInfo;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    private EditText latitude,longitude,locationname;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, LocationActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);
        locationname=findViewById(R.id.location_name);
        historyVM = new ViewModelProvider(LocationActivity.this).get(HistoryVM.class);
    }

    public void locationGenerate(View view) {

        String data1 = locationname.getText().toString();
        String data2 = longitude.getText().toString();
        String data3 = latitude.getText().toString();

        if (data3.equals("")) {
            latitude.setError("Please enter Latitude");
        } else if (data2.equals("")) {
            longitude.setError("Please enter Longitude");
        } else if (data1.equals("")) {
            locationname.setError("Please enter Location Name");
        } else {
            try {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(locationname.getText().toString());
                arrayList.add(latitude.getText().toString());
                arrayList.add(longitude.getText().toString());
                final GeoInfo location = new GeoInfo();
                location.setPoints(arrayList);
                History locHistory = new History(location.generateString(), "location");
                historyVM.insertHistory(locHistory);

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
                        locationname.setText(null);
                        latitude.setText(null);
                        longitude.setText(null);
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