package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import androidmads.library.qrgenearator.BuildConfig;
import com.moutamid.qr.scanner.generator.Model.ButtonModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;
import com.moutamid.qr.scanner.generator.adapter.ButtonResultAdapter;
import com.moutamid.qr.scanner.generator.adapter.ResultAdapter;
import com.moutamid.qr.scanner.generator.utils.QRCode;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Spotify;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

import static java.io.File.separator;

public class ScanResultActivity extends AppCompatActivity {
    private final ArrayList<String> resultdatalist = new ArrayList<>();
   // private final ArrayList<ButtonModel> buttonResultdatalist = new ArrayList<>();
    private String contactNumber;
    private Bitmap bmp;
    private Wifi wifi;
    private SharedPreferences prefs;
    private Bitmap bitmap;
    private AppCompatButton saveBtn,shareBtn,dialBtn,emailBtn,contactBtn,deleteBtn;

    @SuppressLint({"ResourceAsColor", "ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        ImageView imageView = findViewById(R.id.imageView4);
        ImageView icon = findViewById(R.id.imageView);
    //    ConstraintLayout constraintResult = findViewById(R.id.constraintLayout_result);
     //   ConstraintLayout constraintHead = findViewById(R.id.constraintLayout_head);
        TextView tvHead = findViewById(R.id.tv_head);
        TextView tvTitle = findViewById(R.id.tv_title);
        saveBtn = findViewById(R.id.save);
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        dialBtn = findViewById(R.id.dial);
        emailBtn = findViewById(R.id.email);
        contactBtn = findViewById(R.id.add_contact);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
       // byte[] image = getIntent().getByteArrayExtra("image");
       // bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
      //  RecyclerView recyclerView = findViewById(R.id.recycler_result);
       // RecyclerView recyclerViewButtons = findViewById(R.id.recycler_button);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ScanResultActivity.this, mediatedBannerView);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContent();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContent();
            }
        });

        dialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContent();
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailContent();
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactContent();

            }
        });

        switch (getIntent().getStringExtra("type")) {
            case "VCard": {
                VCard vCard = (VCard) getIntent().getSerializableExtra("vCard");
                if (!(vCard.getName() == null)) {
                    resultdatalist.add(vCard.getName());
                }
                if (!(vCard.getEmail() == null)) {
                    resultdatalist.add(vCard.getEmail());
                }
                if (!(vCard.getPhoneNumber() == null)) {
                    resultdatalist.add(vCard.getPhoneNumber());
                }
                if (!(vCard.getCompany() == null)) {
                    resultdatalist.add(vCard.getCompany());
                }
                if (!(vCard.getAddress() == null)) {
                    resultdatalist.add(vCard.getAddress());
                }
                if (!(vCard.getNote() == null)) {
                    resultdatalist.add(vCard.getNote());
                }
                if (!(vCard.getTitle() == null)) {
                    resultdatalist.add(vCard.getTitle());
                }
                if (!(vCard.getWebsite() == null)) {
                    resultdatalist.add(vCard.getWebsite());
                }
                bmp = QRCode.from(vCard.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                icon.setImageResource(R.drawable.contact);
                tvHead.setText(R.string.contact);
                tvTitle.setText(vCard.getName());
      //          constraintHead.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.contactColor));
        //        constraintResult.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.contactColor));
                contactNumber = vCard.getPhoneNumber();
                contactBtn.setVisibility(View.VISIBLE);
                break;
            }
            case "EMail": {

                EMail eMail = (EMail) getIntent().getSerializableExtra("eMail");
                bmp = QRCode.from(eMail.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(eMail.getEmail() == null)) {
                    resultdatalist.add(eMail.getEmail());
                }
                if (!(eMail.getMailSubject() == null)) {
                    resultdatalist.add(eMail.getMailSubject());
                }
                if (!(eMail.getMailBody() == null)) {
                    resultdatalist.add(eMail.getMailBody());
                }
                icon.setImageResource(R.drawable.email);
                tvHead.setText(R.string.email);
                tvTitle.setText(eMail.getEmail());
                contactBtn.setVisibility(View.GONE);

                break;
            }
            case "wifi": {

                wifi = (Wifi) getIntent().getSerializableExtra("Wifi");
                bmp = QRCode.from(wifi.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(wifi.getSsid() == null)) {
                    resultdatalist.add(wifi.getSsid());
                }
                if (!(wifi.getPsk() == null)) {
                    resultdatalist.add(wifi.getPsk());
                }
                if (!(wifi.getAuthentication() == null)) {
                    resultdatalist.add(wifi.getAuthentication());
                }
                icon.setImageResource(R.drawable.wifi);
                tvHead.setText(R.string.wifi);
                tvTitle.setText(wifi.getSsid());
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                break;
            }
            case "telephone": {
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                icon.setImageResource(R.drawable.phone);
                tvHead.setText(R.string.phone);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.VISIBLE);
                contactNumber = telephone.getTelephone();
                tvTitle.setText(contactNumber);
                break;
            }
            case "spotify": {
                Spotify telephone = (Spotify) getIntent().getSerializableExtra("spotify");
                bmp = QRCode.from(telephone.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(telephone.getName() == null)) {
                    resultdatalist.add(telephone.getName());
                }
                if (!(telephone.getSong() == null)) {
                    resultdatalist.add(telephone.getSong());
                }
                icon.setImageResource(R.drawable.spotify);
                tvHead.setText(R.string.spotify);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                contactNumber = telephone.getName();
                tvTitle.setText(contactNumber);
                break;
            }
            case "whatsapp": {
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                icon.setImageResource(R.drawable.whatsapp);
                tvHead.setText(R.string.whatsapp);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.VISIBLE);
                contactNumber = telephone.getTelephone();
                tvTitle.setText(contactNumber);
                break;
            }
            case "viber": {
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                icon.setImageResource(R.drawable.viber);
                tvHead.setText(R.string.viber);
                contactBtn.setVisibility(View.GONE);
                dialBtn.setVisibility(View.VISIBLE);
                contactNumber = telephone.getTelephone();
                tvTitle.setText(contactNumber);
                break;
            }
            case "url": {

                Url url = (Url) getIntent().getSerializableExtra("Url");
                bmp = QRCode.from(url.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(url.getUrl() == null)) {
                    resultdatalist.add(url.getUrl());
                }
                icon.setImageResource(R.drawable.url);
                tvHead.setText(R.string.url);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(url.getUrl());
                break;
            }
            case "youtube": {

                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                icon.setImageResource(R.drawable.youtube);
                tvHead.setText(R.string.youtube);

                contactBtn.setVisibility(View.GONE);
                tvTitle.setText(social.getUrl());
                dialBtn.setVisibility(View.GONE);
                break;
            }
            case "insta": {

                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                icon.setImageResource(R.drawable.instagram);
                tvHead.setText(R.string.insta);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(social.getUrl());
                break;
            }
            case "twitter": {

                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                icon.setImageResource(R.drawable.twitter);
                tvHead.setText(R.string.twitter);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(social.getUrl());
                break;
            }
            case "facebook": {

                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                icon.setImageResource(R.drawable.facebook);
                tvHead.setText(R.string.facebook);
                tvHead.setTextColor(R.color.white);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(social.getUrl());
                break;
            }
            case "paypal": {

                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                icon.setImageResource(R.drawable.paypal);
                tvHead.setText(R.string.paypal);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(social.getUrl());
                break;
            }
            case "GeoInfo": {

                GeoInfo geoInfo = (GeoInfo) getIntent().getSerializableExtra("geoInfo");
                bmp = QRCode.from(geoInfo.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(geoInfo.getPoints().get(0) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(0));
                }
                if (!(geoInfo.getPoints().get(1) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(1));
                }
                if (!(geoInfo.getPoints().get(2) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(2));
                }
                icon.setImageResource(R.drawable.ic_location);
                tvHead.setText(R.string.location);
                tvTitle.setText(""+geoInfo.getPoints());
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                break;
            }
            case "Sms": {

                SMS sms = (SMS) getIntent().getSerializableExtra("sms");
                bmp = QRCode.from(sms.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(sms.getNumber() == null)) {
                    resultdatalist.add(sms.getNumber());
                }
                if (!(sms.getSubject() == null)) {
                    resultdatalist.add(sms.getSubject());
                }
                icon.setImageResource(R.drawable.ic_sms_01_01);
                tvHead.setText(R.string.sms);
                contactBtn.setVisibility(View.GONE);
                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(sms.getNumber());
                break;
            }
            case "Text": {

                String text = getIntent().getStringExtra("text");
                bmp = QRCode.from(text).bitmap();
                imageView.setImageBitmap(bmp);
                if (null != text) {
                    resultdatalist.add(text);
                }
                icon.setImageResource(R.drawable.ic_text);
                tvHead.setText(R.string.text);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(text);
                break;
            }
            case "clipboard": {

                String text = getIntent().getStringExtra("text");
                bmp = QRCode.from(text).bitmap();
                imageView.setImageBitmap(bmp);
                if (null != text) {
                    resultdatalist.add(text);
                }
                icon.setImageResource(R.drawable.clipboard);
                tvHead.setText(R.string.clipboard);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);
                tvTitle.setText(text);
                break;
            }

            case "Barcode": {

                String textBarcode = getIntent().getStringExtra("barcode");

         /*       try {
                    Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    Writer codeWriter;
                    codeWriter = new Code128Writer();
                    BitMatrix byteMatrix = codeWriter.encode(textBarcode, BarcodeFormat.CODE_128, 400, 200, hintMap);
                    int width = byteMatrix.getWidth();
                    int height = byteMatrix.getHeight();
                    bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            bmp.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    if (image != null){
                        imageView.setImageBitmap(bitmap);
                    }else {
                        imageView.setImageBitmap(bmp);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }*/
                if (textBarcode != null) {
                    resultdatalist.add(textBarcode);
                }

                bmp = QRCode.from(textBarcode).bitmap();
                imageView.setImageBitmap(bmp);
                icon.setImageResource(R.drawable.ic_barcode);
                tvHead.setText(R.string.barcode);
                tvTitle.setText(textBarcode);
                contactBtn.setVisibility(View.GONE);

                dialBtn.setVisibility(View.GONE);

                break;
            }

            case "Event": {
                IEvent iEvent = (IEvent) getIntent().getSerializableExtra("event");
                bmp = QRCode.from(iEvent.toString()).bitmap();
                imageView.setImageBitmap(bmp);
                if (!(iEvent.getUid() == null)) {
                    resultdatalist.add(iEvent.getUid());
                }
                if (!(iEvent.getSummary() == null)) {
                    resultdatalist.add(iEvent.getSummary());
                }
                if (!(iEvent.getStart() == null)) {
                    resultdatalist.add(iEvent.getStart());
                }
                if (!(iEvent.getEnd() == null)) {
                    resultdatalist.add(iEvent.getEnd());
                }
                if (!(iEvent.getStamp() == null)) {
                    resultdatalist.add(iEvent.getStamp());
                }
                if (!(iEvent.getOrganizer() == null)) {
                    resultdatalist.add(iEvent.getOrganizer());
                }
                contactBtn.setVisibility(View.GONE);
                dialBtn.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_event);
                tvHead.setText(R.string.event);
                tvTitle.setText(iEvent.getOrganizer());
            //    constraintHead.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.eventColor));
              //  constraintResult.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.eventColor));
                break;
            }

            default:
                Toast.makeText(this, "Not Vilad type", Toast.LENGTH_SHORT).show();
                break;
        }


       // ResultAdapter messageAdapter = new ResultAdapter(resultdatalist);
       // recyclerView.setAdapter(messageAdapter);

        //ButtonResultAdapter buttonResultAdapter = new ButtonResultAdapter(this, buttonResultdatalist);
        //recyclerViewButtons.setAdapter(buttonResultAdapter);
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

    private void contactContent() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if (resultdatalist.size() >= 1) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, resultdatalist.get(0));
        }
        if (resultdatalist.size() >= 2) {
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, resultdatalist.get(1));
        }
        if (resultdatalist.size() >= 4) {
            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, resultdatalist.get(3));
        }
        if (resultdatalist.size() >= 6) {
            intent.putExtra(ContactsContract.Intents.Insert.NOTES, resultdatalist.get(5));
        }
        if (resultdatalist.size() >= 5) {
            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, resultdatalist.get(4));
        }
        if (resultdatalist.size() >= 7) {
            intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, resultdatalist.get(6));
        }
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        if (resultdatalist.size() >= 3) {
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, resultdatalist.get(2));
        }
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

        startActivity(intent);

    }

    private void emailContent() {
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), separator + "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/PNG");

            startActivity(Intent.createChooser(intent, "Share image via Email"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dialContent() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contactNumber));
        startActivity(intent);
    }

    private void deleteContent() {
        finish();
    }

    private void shareContent() {
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), separator + "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/PNG");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveContent() {
        saveToGallery();
    }


    private void connectToWiFi(String ssid, String password) {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = "\"" + ssid + "\"";
                wifiConfig.preSharedKey = "\"" + password + "\"";
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsid(ssid)
                            .setWpa2Passphrase(password)
                            .build();

            final NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            final ConnectivityManager connectivityManager = (ConnectivityManager)
                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    // do success processing here..
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    // do failure processing here..
                }
            };
            connectivityManager.requestNetwork(request, networkCallback);

//            WifiNetworkSuggestion.Builder builder = new WifiNetworkSuggestion.Builder()
//                    .setSsid(ssid)
//                    .setWpa2Passphrase(password);
//            WifiNetworkSuggestion suggestion = builder.build();
//
//            ArrayList<WifiNetworkSuggestion> list = new ArrayList<>();
//            list.add(suggestion);
//
//            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            int status = manager.addNetworkSuggestions(list);
//
//            if (status == STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//                //We have successfully added our wifi for the system to consider
//
//            }

// Optional (Wait for post connection broadcast to one of your suggestions)
//            final IntentFilter intentFilter =
//                    new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
//
//            final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (!intent.getAction().equals(
//                            WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
//                        return;
//                    }
//                    // do post connect processing here...
//                }
//            };
//            registerReceiver(broadcastReceiver, intentFilter);


//            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
//            builder.setSsid(ssid);
//            builder.setWpa2Passphrase(password);
//
//            WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
//
//            NetworkRequest.Builder networkRequestBuilder1 = new NetworkRequest.Builder();
//            networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
//            networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier);
//
//            NetworkRequest nr = networkRequestBuilder1.build();
//            ConnectivityManager cm = (ConnectivityManager)
//                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            ConnectivityManager.NetworkCallback networkCallback = new
//                    ConnectivityManager.NetworkCallback() {
//                        @Override
//                        public void onAvailable(Network network) {
//                            super.onAvailable(network);
//                            Log.d("TAG", "onAvailable:" + network);
//                            cm.bindProcessToNetwork(network);
//                        }
//                    };
//            cm.requestNetwork(nr, networkCallback);
        }

    }

    public void backResult(View view) {
        finish();
    }

    private void saveToGallery() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Photo Lab");
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);
            if (uri != null) {
                try {
                    saveImageToStream(bmp, getContentResolver().openOutputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString()
                    + separator + "Photo Lab");

            //getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bmp, new FileOutputStream(file));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Toast.makeText(this, "QR Save to Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}