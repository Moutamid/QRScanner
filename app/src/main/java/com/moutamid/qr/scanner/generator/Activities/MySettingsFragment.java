package com.moutamid.qr.scanner.generator.Activities;

import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;

import com.moutamid.qr.scanner.generator.Fragments.ScanFragment;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

import java.util.Locale;

public class MySettingsFragment extends Fragment {

    public static String PACKAGE_NAME;
    private Switch beep,vibrate,copy,batch_scanning,manual_scanning,web_search,product_details,save_history,save_qr;
    private TextView searchTxt,languageTxt,cameraTxt,modeTxt;
    private RelativeLayout policyBtn,rateBtn,shareBtn,deleteBtn,cameraLayout,searchLayout,modelLayout;
    private boolean beepSound = false;
    private boolean vibration = false;
    private boolean copied =false;
    SharedPreferences prefs;
    private boolean theme = false;
    SharedPreferences.Editor edit;
    private String engine ="";
    private String cameraMode;
    private boolean productDetails =false;
    private boolean saveHistory = false;
    private boolean cameraStatus =false;
    private boolean saveQR =false;
    private boolean web =false;
    private HistoryVM historyVM;
    //private RelativeLayout languageTxt;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        edit = prefs.edit();
        theme = prefs.getBoolean("theme",false);
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
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        PACKAGE_NAME = getActivity().getPackageName();
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        edit = prefs.edit();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) CAMediatedBannerView mediatedBannerView = view.findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, getActivity(), mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        beepSound = prefs.getBoolean("beepsound",false);
        vibration = prefs.getBoolean("vibrate",false);
        cameraStatus = prefs.getBoolean("camera",false);
        theme = prefs.getBoolean("theme",false);
        web = prefs.getBoolean("web",false);
        copied = prefs.getBoolean("copy",false);
        engine = prefs.getString("search","Google");
        cameraMode = prefs.getString("cameraMode","normal");
        productDetails = prefs.getBoolean("product",false);
        saveHistory = prefs.getBoolean("saveHistory",false);
        saveQR = prefs.getBoolean("saveQR",false);
        beep = view.findViewById(R.id.sound_switch);
        vibrate = view.findViewById(R.id.vibration_switch);
        copy = view.findViewById(R.id.copy_switch);
        searchTxt = view.findViewById(R.id.search);
        languageTxt = view.findViewById(R.id.languageTxt);
        historyVM = new ViewModelProvider(MySettingsFragment.this).get(HistoryVM.class);
        cameraTxt = view.findViewById(R.id.camera_mode);
        modeTxt = view.findViewById(R.id.mode);
        languageTxt.setText("English");
        policyBtn = view.findViewById(R.id.general_policy);
        shareBtn = view.findViewById(R.id.general_tell);
        deleteBtn = view.findViewById(R.id.general_delete);
        rateBtn = view.findViewById(R.id.general_about);
        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
            modeTxt.setText(getActivity().getResources().getString(R.string.theme1));
        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
            modeTxt.setText(getActivity().getResources().getString(R.string.theme2));
        }

        cameraLayout = view.findViewById(R.id.camera_status);
        searchLayout = view.findViewById(R.id.search_engine);
        modelLayout = view.findViewById(R.id.theme_status);

        batch_scanning = view.findViewById(R.id.batch_switch);
        manual_scanning = view.findViewById(R.id.manual_switch);
        web_search = view.findViewById(R.id.web_switch);
        product_details = view.findViewById(R.id.product_switch);
        save_history = view.findViewById(R.id.save_history_switch);
        save_qr = view.findViewById(R.id.duplicate_switch);


        getLocale();
        if (cameraStatus){
            cameraTxt.setText(getActivity().getResources().getString(R.string.camera_txt2));
        }else {
            cameraTxt.setText(getActivity().getResources().getString(R.string.camera_txt1));
        }

        save_qr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putBoolean("saveQR",true);
                    edit.apply();
                }else {
                    edit.putBoolean("saveQR",false);
                    edit.apply();
                }
            }
        });
        web_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putBoolean("web",true);
                    edit.apply();
                }else {
                    edit.putBoolean("web",false);
                    edit.apply();
                }
            }
        });
        save_history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putBoolean("saveHistory",true);
                    edit.apply();
                }else {
                    edit.putBoolean("saveHistory",false);
                    edit.apply();
                }
            }
        });
        product_details.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putBoolean("product",true);
                    edit.apply();
                }else {
                    edit.putBoolean("product",false);
                    edit.apply();
                }
            }
        });

        batch_scanning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putString("cameraMode","batch");
                    edit.apply();
                }else {
                    edit.putString("cameraMode","normal");
                    edit.apply();
                }
            }
        });
        manual_scanning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edit.putString("cameraMode","manual");
                    edit.apply();
                }else {
                    edit.putString("cameraMode","normal");
                    edit.apply();
                }
            }
        });
        if (cameraMode.equals("batch")){
            batch_scanning.setChecked(true);
            manual_scanning.setChecked(false);
        }else if (cameraMode.equals("manual")){
            batch_scanning.setChecked(false);
            manual_scanning.setChecked(true);
        }else {

            batch_scanning.setChecked(false);
            manual_scanning.setChecked(false);
        }

        beep.setChecked(beepSound);
        vibrate.setChecked(vibration);
        copy.setChecked(copied);
        searchTxt.setText(engine);
        product_details.setChecked(productDetails);
        save_history.setChecked(saveHistory);
        save_qr.setChecked(saveQR);
        web_search.setChecked(web);

        beep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!beepSound) {
                    edit.putBoolean("beepsound", Boolean.TRUE);
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                }else {
                    edit.putBoolean("beepsound", Boolean.FALSE);
                }
                edit.apply();
            }
        });
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!vibration) {
                    edit.putBoolean("vibrate", Boolean.TRUE);
                    Vibrator v = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(300);
                }else {
                    edit.putBoolean("vibrate", Boolean.FALSE);
                }
                edit.apply();
            }
        });
        copy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!copied) {
                    edit.putBoolean("copy", Boolean.TRUE);
                 //   Vibrator v = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);
                   // v.vibrate(300);
                }else {
                    edit.putBoolean("copy", Boolean.FALSE);
                }
                edit.apply();
            }
        });
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchEngineDialogBox();
            }
        });
        languageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  languageOptionsDialog();
                startActivity(new Intent(getActivity(),LanguageOptions.class));
            }
        });

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraDialog();
            }
        });

        modelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeDialog();
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try
                {
                    getActivity().startActivity(myAppLinkToMarket);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getActivity(), " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getActivity().getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                getActivity().startActivity(sendIntent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Do you want to delete?");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("OK", (dialog, which) -> {
                    historyVM.deleteAllHistory();
                    if (!getPurchaseSharedPreference()) {
                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                    }
                });
                adb.setNegativeButton("Cancel", (dialog, which) -> {
                });
                adb.show();
            }
        });
        policyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse("https://whatsappwebapp.blogspot.com/2019/08/privacy-policy-whatscan-for-whatsapp-web.html");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                try {
                    startActivity(webIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void cameraDialog() {
        String[] listItems = {getActivity().getResources().getString(R.string.camera_txt1),getActivity().getResources().getString(R.string.camera_txt2)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.camera));
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    cameraTxt.setText(listItems[i]);
                    edit.putBoolean("camera",Boolean.FALSE);
                    edit.apply();
                }
                else if (i == 1){
                    cameraTxt.setText(listItems[i]);
                    edit.putBoolean("camera",Boolean.TRUE);
                    edit.apply();

                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void ThemeDialog() {
        String[] listItems = {getActivity().getResources().getString(R.string.theme1),
                getActivity().getResources().getString(R.string.theme2)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){

                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                    // it will set isDarkModeOn
                    // boolean to false
                    modeTxt.setText(listItems[i]);
                    edit.putBoolean("theme",Boolean.TRUE);
                    edit.apply();
                }
                else if (i == 1){
                    modeTxt.setText(listItems[i]);
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false

                    edit.putBoolean("theme",Boolean.FALSE);
                    edit.apply();
                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /*private void languageOptionsDialog() {
        String[] listItems = {"Default English (USA)","English (USA)",
                "Chinese","French","Germany","Hindi","Italian","Malaysian","Turkish"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){

                    languageTxt.setText("Default English (USA)");
                    setLocale("en","Default English (USA)");
                    getActivity().recreate();

                }
                else if (i == 1){
                    languageTxt.setText("English (USA)");
                    setLocale("en","English (USA)");
                    getActivity().recreate();

                }
                else if (i == 2){
                    languageTxt.setText("Chinese");
                    setLocale("zh","Chinese");
                    getActivity().recreate();

                }
                else if (i == 3){
                    languageTxt.setText("French");
                    setLocale("fr","French");
                    getActivity().recreate();
                }
                else if (i == 4){
                    languageTxt.setText("Germany");
                    setLocale("de","Germany");
                    getActivity().recreate();

                }
                else if (i == 5){
                    languageTxt.setText("Hindi");
                    setLocale("hi","Hindi");
                    getActivity().recreate();

                }
                else if (i == 6){
                    languageTxt.setText("Italian");
                    setLocale("it","Italian");
                    getActivity().recreate();

                }
                else if (i == 7){
                    languageTxt.setText("Malaysian");
                    setLocale("ms","Malaysian");
                    getActivity().recreate();

                }
                else if (i == 8){
                    languageTxt.setText("Turkish");
                    setLocale("tr","Turkish");
                    getActivity().recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

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
        getActivity().getResources().updateConfiguration(configuration,getActivity().getResources().getDisplayMetrics());
        edit.putString("lang",lng);
        edit.putString("lang_name",name);
        edit.apply();
        languageTxt.setText(name);
    }



    private void showSearchEngineDialogBox() {
        String[] listItems = {"Google","Bing","Yahoo","DuckDuckGo","Ecosia","Yandex"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    searchTxt.setText("Google");
                    edit.putString("search","Google");
                    edit.apply();
                }
                else if (i == 1){
                    searchTxt.setText("Bing");
                    edit.putString("search","Bing");
                    edit.apply();

                }else if (i == 2){
                    searchTxt.setText("Yahoo");
                    edit.putString("search","Yahoo");
                    edit.apply();

                }
                else if (i == 3){
                    searchTxt.setText("DuckDuckGo");
                    edit.putString("search","DuckDuckGo");
                    edit.apply();

                }
                else if (i == 4){
                    searchTxt.setText("Ecosia");
                    edit.putString("search","Ecosia");
                    edit.apply();

                }
                else if (i == 5){
                    searchTxt.setText("Yandex");
                    edit.putString("search","Yandex");
                    edit.apply();

                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}
