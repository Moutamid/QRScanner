package com.moutamid.qr.scanner.generator.Activities;

import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
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

public class MySettingsFragment extends Fragment {

    public static String PACKAGE_NAME;
    private Switch beep,vibrate,copy;
    private TextView searchTxt;
    private RelativeLayout languageBtn,policyBtn,feedbackBtn;
    private boolean beepSound = false;
    private boolean vibration = false;
    private boolean copied =false;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private String engine ="";

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
        languageBtn = view.findViewById(R.id.language);
        policyBtn = view.findViewById(R.id.policy);
        feedbackBtn = view.findViewById(R.id.feedback);
        beep.setChecked(beepSound);
        vibrate.setChecked(vibration);
        copy.setChecked(copied);
        searchTxt.setText(engine);
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
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageOptionsDialog();
            }
        });

        return view;
    }

    private void languageOptionsDialog() {
        String[] listItems = {"Default English (USA)","English (USA)","Arabic",
                "Chinese","French","Germany","Hindi","Italian","Malaysian","Turkish","Urdu"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){

                    edit.putString("lang","Default English (USA)");
                    edit.apply();
                }
                else if (i == 1){

                    edit.putString("lang","English (USA)");
                    edit.apply();

                }else if (i == 2){

                    edit.putString("lang","Arabic");
                    edit.apply();

                }
                else if (i == 3){

                    edit.putString("lang","Chinese");
                    edit.apply();

                }
                else if (i == 4){

                    edit.putString("lang","French");
                    edit.apply();

                }
                else if (i == 5){

                    edit.putString("lang","Germany");
                    edit.apply();

                }
                else if (i == 6){

                    edit.putString("lang","Hindi");
                    edit.apply();

                }
                else if (i == 7){

                    edit.putString("lang","Italian");
                    edit.apply();

                }
                else if (i == 8){

                    edit.putString("lang","Malaysian");
                    edit.apply();

                }
                else if (i == 9){

                    edit.putString("lang","Turkish");
                    edit.apply();

                }
                else if (i == 10){

                    edit.putString("lang","Urdu");
                    edit.apply();

                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
