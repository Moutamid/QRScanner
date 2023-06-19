package com.moutamid.qr.scanner.generator.Activities;

import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;

import com.moutamid.qr.scanner.generator.R;

import java.util.Locale;

public class MySettingsFragment extends Fragment {

    public static String PACKAGE_NAME;
    private Switch beep,vibrate,copy;
    private TextView searchTxt,languageTxt;
    private RelativeLayout policyBtn,feedbackBtn;
    private boolean beepSound = false;
    private boolean vibration = false;
    private boolean copied =false;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private String engine ="";
    //private RelativeLayout languageTxt;

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
        copied = prefs.getBoolean("copy",false);
        engine = prefs.getString("search","Google");
        beep = view.findViewById(R.id.switch1);
        vibrate = view.findViewById(R.id.switch2);
        copy = view.findViewById(R.id.switch3);
        searchTxt = view.findViewById(R.id.search);
        languageTxt = view.findViewById(R.id.language);
        languageTxt.setText("English");
        policyBtn = view.findViewById(R.id.policy);
        feedbackBtn = view.findViewById(R.id.feedback);
        beep.setChecked(beepSound);
        vibrate.setChecked(vibration);
        copy.setChecked(copied);
        searchTxt.setText(engine);
        getLocale();
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
        searchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchEngineDialogBox();
            }
        });
        languageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageOptionsDialog();
            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FeedbackActivity.class);
                startActivity(intent);
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

    private void languageOptionsDialog() {
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
