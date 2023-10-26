package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.gms.vision.barcode.Barcode;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.Model.ResultModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.BatchItemsAdapter;
import com.moutamid.qr.scanner.generator.interfaces.BatchItemClick;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;

import java.util.ArrayList;

public class BatchScanResultActivity extends AppCompatActivity {
    RecyclerView listItems;
    BatchItemsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_batch_scan_result);

        listItems = findViewById(R.id.listItems);

        listItems.setLayoutManager(new LinearLayoutManager(this));
        listItems.setHasFixedSize(false);

        ArrayList<ResultModel> list = Stash.getArrayList(Constants.RESULT_BATCH, ResultModel.class);
        for (int i = 0; i< list.size()-1; i++){
            if (list.get(i).getRawData().equals(list.get(i+1).getRawData())){
                list.remove(i+1);
            }
        }

        adapter = new BatchItemsAdapter(this, list, new BatchItemClick() {
            @Override
            public void onClick(ResultModel resultModel) {
                if (resultModel.getFormat() == Barcode.CODE_128 || resultModel.getFormat()  == Barcode.EAN_13 || resultModel.getFormat()  == Barcode.EAN_8 || resultModel.getFormat()  == Barcode.CODE_93) {
                    processResultBarcode(resultModel.getRawData(), resultModel.getFormat() );
                } else {
                    processRawResult(resultModel.getRawData());
                }
            }
        });
        listItems.setAdapter(adapter);

    }

    public void processResultBarcode(String text, int barcodeFormat) {
        Intent intent = new Intent(this, ScanResultActivity.class);
        try {
            History contactHistory = new History(text, "barcode", true);
            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
            historyList.add(contactHistory);
            Stash.put(Constants.SCAN, historyList);
//            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcodeFormat", barcodeFormat);
            intent.putExtra("barcode", text);
            //intent.putExtra("image", savedBitmapFromViewToFile());
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }

        } catch (Exception t) {
            Toast.makeText(this, "not scan", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        } finally {
            startActivity(intent);
        }
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }


    public void processRawResult(String text) {

        Intent intent = new Intent(this, ScanResultActivity.class);

        try {
            VCard vCard = new VCard();
            vCard.parseSchema(text);
            History contactHistory = new History(vCard.generateString(), "contact", true);
            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
            historyList.add(contactHistory);
            Stash.put(Constants.SCAN, historyList);
            //    historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "VCard");
            intent.putExtra("vCard", vCard);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
        } catch (IllegalArgumentException vcard) {
            try {
                EMail eMail = new EMail();
                eMail.parseSchema(text);
                History contactHistory = new History(eMail.generateString(), "email", true);
                ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                historyList.add(contactHistory);
                Stash.put(Constants.SCAN, historyList);
//                historyVM.insertHistory(contactHistory);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);

                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
            } catch (IllegalArgumentException email) {
                try {
                    Wifi wifi = new Wifi();
                    wifi.parseSchema(text);
                    History contactHistory = new History(wifi.generateString(), "wifi", true);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                    historyList.add(contactHistory);
                    Stash.put(Constants.SCAN, historyList);
//                    historyVM.insertHistory(contactHistory);
                    intent.putExtra("type", "wifi");
                    intent.putExtra("Wifi", wifi);

                    if (!getPurchaseSharedPreference()) {
                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                    }
                } catch (IllegalArgumentException wifi) {
                    try {
                        Telephone telephone = new Telephone();
                        telephone.parseSchema(text);
                        History contactHistory = new History(telephone.generateString(), "phone", true);
                        ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                        historyList.add(contactHistory);
                        Stash.put(Constants.SCAN, historyList);
//                        historyVM.insertHistory(contactHistory);
                        intent.putExtra("type", "telephone");
                        intent.putExtra("phone", telephone);

                        if (!getPurchaseSharedPreference()) {
                            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                        }
                    } catch (IllegalArgumentException telephone) {
                        try {
                            Url url = new Url();
                            url.parseSchema(text);
                            History contactHistory = new History(url.generateString(), "url", true);
                            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                            historyList.add(contactHistory);
                            Stash.put(Constants.SCAN, historyList);
//                            historyVM.insertHistory(contactHistory);
                            intent.putExtra("type", "url");
                            intent.putExtra("Url", url);

                            if (!getPurchaseSharedPreference()) {
                                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                            }
                        } catch (IllegalArgumentException url) {
                            try {
                                final Social social = new Social();
                                social.parseSchema(text);
                                History urlHistory = new History(social.generateString(), "social", true);
                                ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                historyList.add(urlHistory);
                                Stash.put(Constants.SCAN, historyList);
//                                historyVM.insertHistory(urlHistory);
                                intent.putExtra("type", "Social");
                                intent.putExtra("social", social);

                                if (!getPurchaseSharedPreference()) {
                                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                }
                            } catch (IllegalArgumentException youtube) {
                                try {
                                    GeoInfo geoInfo = new GeoInfo();
                                    geoInfo.parseSchema(text);
                                    History contactHistory = new History(geoInfo.generateString(), "location", true);
                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                    historyList.add(contactHistory);
                                    Stash.put(Constants.SCAN, historyList);
//                                    historyVM.insertHistory(contactHistory);
                                    intent.putExtra("type", "GeoInfo");
                                    intent.putExtra("geoInfo", geoInfo);

                                    if (!getPurchaseSharedPreference()) {
                                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                    }
                                } catch (IllegalArgumentException geoinfo) {
                                    try {
                                        SMS sms = new SMS();
                                        sms.parseSchema(text);
                                        History contactHistory = new History(sms.generateString(), "sms", true);
                                        ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                        historyList.add(contactHistory);
                                        Stash.put(Constants.SCAN, historyList);
//                                        historyVM.insertHistory(contactHistory);
                                        intent.putExtra("type", "Sms");
                                        intent.putExtra("sms", sms);

                                        if (!getPurchaseSharedPreference()) {
                                            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                        }
                                    } catch (IllegalArgumentException sms) {
                                        try {
                                            IEvent iEvent = new IEvent();
                                            iEvent.parseSchema(text);
                                            History contactHistory = new History(iEvent.generateString(), "event", true);
                                            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                            historyList.add(contactHistory);
                                            Stash.put(Constants.SCAN, historyList);
//                                            historyVM.insertHistory(contactHistory);
                                            intent.putExtra("type", "Event");
                                            intent.putExtra("event", iEvent);

                                            if (!getPurchaseSharedPreference()) {
                                                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                            }
                                        } catch (IllegalArgumentException event) {
                                            try {
                                                try {
                                                    Log.d("EMAILCHEC", "TEXT  " + text);
                                                    int i = Integer.parseInt(text);
                                                    History contactHistory = new History(text, "barcode", true);
                                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                                    historyList.add(contactHistory);
                                                    Stash.put(Constants.SCAN, historyList);
//                                                    historyVM.insertHistory(contactHistory);
                                                    intent.putExtra("type", "Barcode");
                                                    intent.putExtra("barcode", text);
                                                    //intent.putExtra("image", savedBitmapFromViewToFile());
                                                    if (!getPurchaseSharedPreference()) {
                                                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                                    }
                                                } catch (NumberFormatException e) {
                                                    Log.d("EMAILCHEC", "Error  " + e.toString());
                                                    History contactHistory = new History(text, "text", true);
                                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                                    historyList.add(contactHistory);
                                                    Stash.put(Constants.SCAN, historyList);
//                                                   historyVM.insertHistory(contactHistory);
                                                    intent.putExtra("type", "Text");
                                                    intent.putExtra("text", text);
                                                    if (!getPurchaseSharedPreference()) {
                                                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                                                    }
                                                }
                                            } catch (Exception txt) {
                                                Toast.makeText(this, "not scan", Toast.LENGTH_SHORT).show();
                                                txt.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            startActivity(intent);
        }

//        try {
//            History contactHistory = new History(text, "barcode");
//            historyVM.insertHistory(contactHistory);
//            intent.putExtra("type", "Barcode");
//            intent.putExtra("barcode", text);
//            //intent.putExtra("image", savedBitmapFromViewToFile());
//            startActivity(intent);
//            if (!getPurchaseSharedPreference()) {
//                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
//            }
//        } catch (IllegalArgumentException t) {
//
//        }
    }

    public void backResult(View view){
        Stash.clear(Constants.RESULT_BATCH);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Stash.clear(Constants.RESULT_BATCH);
    }
}