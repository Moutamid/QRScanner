package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;

import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    TextView type1,type2,type3,type4;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        type1 = findViewById(R.id.type1);
        type2 = findViewById(R.id.type2);
        type3 = findViewById(R.id.type3);
        type4 = findViewById(R.id.type4);
        type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type1.setBackgroundResource(R.drawable.active_background);
                type2.setBackgroundResource(R.drawable.non_active_background);
                type3.setBackgroundResource(R.drawable.non_active_background);
                type4.setBackgroundResource(R.drawable.non_active_background);
            }
        });
        type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type1.setBackgroundResource(R.drawable.non_active_background);
                type2.setBackgroundResource(R.drawable.active_background);
                type3.setBackgroundResource(R.drawable.non_active_background);
                type4.setBackgroundResource(R.drawable.non_active_background);
            }
        });
        type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type1.setBackgroundResource(R.drawable.non_active_background);
                type2.setBackgroundResource(R.drawable.non_active_background);
                type3.setBackgroundResource(R.drawable.active_background);
                type4.setBackgroundResource(R.drawable.non_active_background);
            }
        });
        type4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type1.setBackgroundResource(R.drawable.non_active_background);
                type2.setBackgroundResource(R.drawable.non_active_background);
                type3.setBackgroundResource(R.drawable.non_active_background);
                type4.setBackgroundResource(R.drawable.active_background);
            }
        });
        getLocale();
    }


    private void getLocale(){

        String lang = prefs.getString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }

    public void backFeedback(View view){
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