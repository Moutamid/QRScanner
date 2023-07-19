package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
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
                if (!getPurchaseSharedPreference()) {
                    consoliAds.ShowInterstitial(NativePlaceholderName.Default, SplashActivity.this);
                }
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }, 2000);

    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}