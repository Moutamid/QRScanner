package free.qr.code.scanner.generator.Activities;

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
import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.Url;

public class UrlGenActivity extends AppCompatActivity {
    private EditText urledit;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_gen);

        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, UrlGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        urledit = findViewById(R.id.url_edit);
        historyVM = new ViewModelProvider(UrlGenActivity.this).get(HistoryVM.class);
    }

    public void urlGenerate(View view) {
        String urlValue = "https://" + urledit.getText().toString();
        if (urledit.getText().toString().equals("")) {
            urledit.setError("PLease Enter Url");
        } else {
            try {
                final Url url = new Url();
                url.setUrl(urlValue);
                History urlHistory = new History(url.generateString(), "url");
                historyVM.insertHistory(urlHistory);


                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "url");
                intent.putExtra("Url", url);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        urledit.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(UrlGenActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void backUrl(View view) {
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