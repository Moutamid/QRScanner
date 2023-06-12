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
import free.qr.code.scanner.generator.utils.formates.Wifi;


public class    WifiGenActivity extends AppCompatActivity {

    private EditText wifiname,wifipassword;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_gen);
        wifiname=findViewById(R.id.wifi_name);
        wifipassword=findViewById(R.id.wfi_password);
        historyVM = new ViewModelProvider(WifiGenActivity.this).get(HistoryVM.class);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, WifiGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
    }

    public void wifiGenerate(View view) {

        String data1 = wifiname.getText().toString();
        String data2 = wifipassword.getText().toString();
        if (data1.equals("")) {
            wifiname.setError("Please enter WifiName");
        } else if (data2.equals("")) {
            wifipassword.setError("Please enter WifiPassword");
        } else {
            try {
                final Wifi wifi = new Wifi();
                wifi.setSsid(wifiname.getText().toString());
                wifi.setPsk(wifipassword.getText().toString());
                History wifiHistory = new History(wifi.generateString(), "wifi");
                historyVM.insertHistory(wifiHistory);


                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "wifi");
                intent.putExtra("Wifi", wifi);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        wifipassword.setText(null);
                        wifiname.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(WifiGenActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void backWifi(View view) {
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