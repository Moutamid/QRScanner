package free.qr.code.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import free.qr.code.scanner.generator.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
      if(consoliAds != null){
          consoliAds.setConsoliAdsInterstitialListener(null);
      }
    }
    ConsoliAds consoliAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(!getPurchaseSharedPreference()) {
            consoliAds = ConsoliAds.Instance();
            consoliAds.initialize(false, true, SplashActivity.this, getString(R.string.Signature_ads));
            ConsoliAds.Instance().LoadInterstitial();
        }
        //consoliAds.setConsoliAdsListener(this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
                if (!getPurchaseSharedPreference()) {
                    consoliAds.ShowInterstitial(NativePlaceholderName.Default, SplashActivity.this);
                }
            }
        }, 5000);

    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}