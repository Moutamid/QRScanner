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
import free.qr.code.scanner.generator.utils.formates.Telephone;


public class PhoneActivity extends AppCompatActivity {

    private EditText phonenumber;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, PhoneActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        phonenumber=findViewById(R.id.edit_phone);
        historyVM = new ViewModelProvider(PhoneActivity.this).get(HistoryVM.class);

    }

    public void phoneGenerate(View view) {

        String data = phonenumber.getText().toString();

        if (data.equals("")) {
            phonenumber.setError("Please enter Number");
        } else {

            try {
                final Telephone telephone = new Telephone();
                telephone.setTelephone(data);
                History phoneHistory = new History(telephone.generateString(), "phone");
                historyVM.insertHistory(phoneHistory);

            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("type", "telephone");
            intent.putExtra("phone", telephone);
            startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
            finish();

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    phonenumber.setText(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(PhoneActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void backPhone(View view) {
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