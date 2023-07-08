package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.ButtonMainAdapter;
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;

import java.util.Locale;

public class LanguageOptions extends AppCompatActivity {

    private Integer[] images = {R.drawable.us,R.drawable.us,R.drawable.china,
            R.drawable.france,R.drawable.germany,
            R.drawable.india,R.drawable.italy,R.drawable.malaysia,R.drawable.turkey};
    String[] listItems = {"Default English (USA)","English (USA)",
            "Chinese","French","Germany","Hindi","Italian","Malaysian","Turkish"};

    private RecyclerView recyclerView;
    private ButtonMainAdapter adapter;
    private ImageView submitImg;
    private SharedPreferences prefs;

    private SharedPreferences.Editor edit;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(LanguageOptions.this);
        edit = prefs.edit();
        setContentView(R.layout.activity_language_options);
        recyclerView = findViewById(R.id.recyclerView);
        submitImg = findViewById(R.id.submit);
        boolean theme = prefs.getBoolean("theme",false);

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
         getLocale();
        adapter = new ButtonMainAdapter(LanguageOptions.this,images,listItems);
        recyclerView.setAdapter(adapter);
        adapter.setButtonItemClickListener(new ButtonItemClickListener() {
            @Override
            public void clickedItem(View view, int position) {
                name = listItems[position];

                }


            @Override
            public void clickedItemButton(View view, int position, String type) {

            }
        });
        submitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.equals("Default English (USA)")){
                    setLocale("en","Default English (USA)");
                    recreate();
                }
                else if (name.equals("English (USA)")){

                    setLocale("en","English (USA)");
                    recreate();

                }
                else if (name.equals("Chinese")){

                    setLocale("zh","Chinese");
                    recreate();

                }
                else if (name.equals("French")){

                    setLocale("fr","French");
                    recreate();
                }
                else if (name.equals("German")){

                    setLocale("de","German");
                    recreate();

                }
                else if (name.equals("Hindi")){

                    setLocale("hi","Hindi");
                    recreate();

                }
                else if (name.equals("Italian")){

                    setLocale("it","Italian");
                    recreate();

                }
                else if (name.equals("Malaysian")){

                    setLocale("ms","Malaysian");
                    recreate();

                }
                else if (name.equals("Turkish")){

                    setLocale("tr","Turkish");
                    recreate();
                }

                startActivity(new Intent(LanguageOptions.this,MainActivity.class));
                finish();
            }
        });
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
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        edit.putString("lang",lng);
        edit.putString("lang_name",name);
        edit.apply();
    }

    public void backButton(View view) {
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