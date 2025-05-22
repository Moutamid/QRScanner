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


import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.ButtonMainAdapter;
import com.moutamid.qr.scanner.generator.databinding.ActivityLanguageOptionsBinding;
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;

import java.util.Locale;

public class LanguageOptions extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private String name;
    ActivityLanguageOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        binding = ActivityLanguageOptionsBinding.inflate(getLayoutInflater());
        prefs = PreferenceManager.getDefaultSharedPreferences(LanguageOptions.this);
        edit = prefs.edit();
        setContentView(binding.getRoot());
//        recyclerView = findViewById(R.id.recyclerView);
//        submitImg = findViewById(R.id.submit);
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

        binding.english.setOnClickListener(v -> {
            binding.english.setChecked(true);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.chinese.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(true);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.french.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(true);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.germany.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(true);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.hindi.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(true);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.italian.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(true);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(false);
        });

        binding.malaysian.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(true);
            binding.turkish.setChecked(false);
        });

        binding.turkish.setOnClickListener(v -> {
            binding.english.setChecked(false);
            binding.chinese.setChecked(false);
            binding.french.setChecked(false);
            binding.germany.setChecked(false);
            binding.hindi.setChecked(false);
            binding.italian.setChecked(false);
            binding.malaysian.setChecked(false);
            binding.turkish.setChecked(true);
        });


        binding.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.english.isChecked()){
                    setLocale("en","Default English (USA)");
                    recreate();
                }
                else if (binding.chinese.isChecked()){
                    setLocale("zh","Chinese");
                    recreate();
                }
                else if (binding.french.isChecked()){
                    setLocale("fr","French");
                    recreate();
                }
                else if (binding.germany.isChecked()){
                    setLocale("de","German");
                    recreate();
                }
                else if (binding.hindi.isChecked()){
                    setLocale("hi","Hindi");
                    recreate();
                }
                else if (binding.italian.isChecked()){
                    setLocale("it","Italian");
                    recreate();
                }
                else if (binding.malaysian.isChecked()){
                    setLocale("ms","Malaysian");
                    recreate();
                }
                else if (binding.turkish.isChecked()){
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

        if (name.equals("Default English (USA)")){
            binding.english.setChecked(true);
        }
        else if (name.equals("Chinese")){
            binding.chinese.setChecked(true);
        }
        else if (name.equals("French")){
            binding.french.setChecked(true);
        }
        else if (name.equals("German")){
            binding.germany.setChecked(true);
        }
        else if (name.equals("Hindi")){
            binding.hindi.setChecked(true);
        }
        else if (name.equals("Italian")){
            binding.italian.setChecked(true);
        }
        else if (name.equals("Malaysian")){
            binding.malaysian.setChecked(true);
        }
        else if (name.equals("Turkish")){
            binding.turkish.setChecked(true);
        }

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
        if (!Constants.getPurchaseSharedPreference(LanguageOptions.this)) {
//            //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();
    }
}