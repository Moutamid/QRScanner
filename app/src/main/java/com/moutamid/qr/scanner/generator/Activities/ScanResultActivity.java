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
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.model.content.Mask;
import com.bumptech.glide.Glide;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;

import androidmads.library.qrgenearator.BuildConfig;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code93Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.oned.UPCEWriter;
import com.moutamid.qr.scanner.generator.Model.ButtonModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.databinding.ActivityScanResultBinding;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static java.io.File.separator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScanResultActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    ActivityScanResultBinding binding;
    private final ArrayList<String> resultdatalist = new ArrayList<>();
    // private final ArrayList<ButtonModel> buttonResultdatalist = new ArrayList<>();
    private String contactNumber;
    private Bitmap bmp;
    private Wifi wifi;
    private SharedPreferences prefs;
    private Bitmap bitmap;
    private String engine;
    String type;
    private boolean web = false;

    @SuppressLint({"ResourceAsColor", "ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = prefs.getBoolean("theme", false);
        engine = prefs.getString("search", "Google");
        web = prefs.getBoolean("web", false);
        if (theme) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

        } else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }

        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ScanResultActivity.this, mediatedBannerView);
        }

        binding.save.setOnClickListener(view -> saveToGallery());

        progressDialog = new ProgressDialog(ScanResultActivity.this);
        progressDialog.setMessage("Fetching Product Details....");
        progressDialog.setCancelable(false);

        /*
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
        });*/
        type = getIntent().getStringExtra("type");

        switch (type) {
            case "VCard": {
                binding.vcardLayout.setVisibility(View.VISIBLE);
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
                binding.imageView4.setImageBitmap(bmp);
                binding.cardName.setText(vCard.getName());
                binding.cardEmail.setText(vCard.getEmail());
                binding.cardNumb.setText(vCard.getPhoneNumber());
                binding.cardOrg.setText(vCard.getCompany());
                binding.cardAdd.setText(vCard.getAddress());

                copyClipboard = vCard.getName() + "\n" + vCard.getEmail() + "\n" + vCard.getPhoneNumber() + "\n" + vCard.getCompany() + "\n" + vCard.getAddress();

//                icon.setImageResource(R.drawable.contact);
//                tvHead.setText(R.string.contact);
//                tvTitle.setText(vCard.getName());
//                //          constraintHead.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.contactColor));
//                //        constraintResult.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.contactColor));
//                contactNumber = vCard.getPhoneNumber();
//                contactBtn.setVisibility(View.VISIBLE);
                break;
            }

            case "EMail": {

                binding.emailLayout.setVisibility(View.VISIBLE);

                EMail eMail = (EMail) getIntent().getSerializableExtra("eMail");
                bmp = QRCode.from(eMail.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(eMail.getEmail() == null)) {
                    resultdatalist.add(eMail.getEmail());
                }
                if (!(eMail.getMailSubject() == null)) {
                    resultdatalist.add(eMail.getMailSubject());
                }
                if (!(eMail.getMailBody() == null)) {
                    resultdatalist.add(eMail.getMailBody());
                }
                link = eMail.getEmail();
                copyClipboard = eMail.getEmail() + "\nSubject : " + eMail.getMailSubject() + "\n" + eMail.getMailBody();
                binding.email.setText(eMail.getEmail() + "");
                binding.emailBody.setText(eMail.getMailBody() + "");
                binding.emailSubject.setText(eMail.getMailSubject() + "");

                break;
            }
            case "wifi": {

                binding.wifiLayout.setVisibility(View.VISIBLE);

                wifi = (Wifi) getIntent().getSerializableExtra("Wifi");
                bmp = QRCode.from(wifi.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(wifi.getSsid() == null)) {
                    resultdatalist.add(wifi.getSsid());
                }
                if (!(wifi.getPsk() == null)) {
                    resultdatalist.add(wifi.getPsk());
                }
                if (!(wifi.getAuthentication() == null)) {
                    resultdatalist.add(wifi.getAuthentication());
                }

                binding.wifiName.setText(wifi.getSsid());
                binding.wifiPassword.setText(wifi.getPsk());
                break;
            }
            case "telephone": {
                binding.phoneLayout.setVisibility(View.VISIBLE);
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                copyClipboard = telephone.getTelephone();
                link = telephone.getTelephone();
                contactNumber = telephone.getTelephone();
                binding.phone.setText(contactNumber);
                break;
            }
            case "spotify": {
                binding.spotifyLayout.setVisibility(View.VISIBLE);
                Spotify telephone = (Spotify) getIntent().getSerializableExtra("spotify");
                bmp = QRCode.from(telephone.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (telephone.getName() != null) {
                    resultdatalist.add(telephone.getName());
                }
                if (telephone.getSong() != null) {
                    resultdatalist.add(telephone.getSong());
                }
                copyClipboard = telephone.getName();
                link = telephone.getName();
                binding.spotifyName.setText(telephone.getName());
                binding.spotifySong.setText(telephone.getSong());
                break;
            }
            case "whatsapp": {
                binding.phoneLayout.setVisibility(View.VISIBLE);
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                binding.phoneImage.setImageResource(R.drawable.whatsapp);
                binding.phoneBtnImage.setImageResource(R.drawable.whatsapp);
                binding.phoneTITLE.setText(R.string.whatsapp);
                binding.phoneBtn.setText(R.string.whatsapp);
                contactNumber = telephone.getTelephone();
                copyClipboard = contactNumber;
                link = contactNumber;
                binding.phone.setText(contactNumber);
                break;
            }
            case "viber": {
                binding.phoneLayout.setVisibility(View.VISIBLE);
                Telephone telephone = (Telephone) getIntent().getSerializableExtra("phone");
                bmp = QRCode.from(telephone.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(telephone.getTelephone() == null)) {
                    resultdatalist.add(telephone.getTelephone());
                }
                binding.phoneImage.setImageResource(R.drawable.viber);
                binding.phoneBtnImage.setImageResource(R.drawable.viber);
                binding.phoneTITLE.setText(R.string.viber);
                binding.phoneBtn.setText(R.string.viber);
                contactNumber = telephone.getTelephone();
                copyClipboard = contactNumber;
                link = contactNumber;
                binding.phone.setText(contactNumber);
                break;
            }
            case "url": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Url url = (Url) getIntent().getSerializableExtra("Url");
                bmp = QRCode.from(url.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(url.getUrl() == null)) {
                    resultdatalist.add(url.getUrl());
                }
                binding.url.setText(url.getUrl());
                copyClipboard = url.getUrl();
                link = url.getUrl();
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(url.getUrl());
                break;
            }
            case "youtube": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                binding.urlTitleImage.setImageResource(R.drawable.youtube);
                binding.urlTitle.setText(R.string.youtube);
                binding.urlImage.setImageResource(R.drawable.youtube);
                binding.urlTypeName.setText(R.string.youtube);
                copyClipboard = social.getUrl();
                link = social.getUrl();
                binding.url.setText(social.getUrl());
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(social.getUrl());

                binding.urlType.setVisibility(View.VISIBLE);

                break;
            }
            case "insta": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                binding.urlTitleImage.setImageResource(R.drawable.instagram);
                binding.urlTitle.setText(R.string.insta);
                binding.urlImage.setImageResource(R.drawable.instagram);
                binding.urlTypeName.setText(R.string.insta);

                binding.url.setText(social.getUrl());
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(social.getUrl());
                copyClipboard = social.getUrl();
                link = social.getUrl();
                binding.urlType.setVisibility(View.VISIBLE);
                break;
            }
            case "twitter": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                binding.urlTitleImage.setImageResource(R.drawable.twitter);
                binding.urlTitle.setText(R.string.twitter);
                binding.urlImage.setImageResource(R.drawable.twitter);
                binding.urlTypeName.setText(R.string.twitter);
                copyClipboard = social.getUrl();
                link = social.getUrl();
                binding.url.setText(social.getUrl());
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(social.getUrl());

                binding.urlType.setVisibility(View.VISIBLE);
                break;
            }
            case "facebook": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                binding.urlTitleImage.setImageResource(R.drawable.facebook);
                binding.urlTitle.setText(R.string.facebook);
                binding.urlImage.setImageResource(R.drawable.facebook);
                binding.urlTypeName.setText(R.string.facebook);

                binding.url.setText(social.getUrl());
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(social.getUrl());
                copyClipboard = social.getUrl();
                link = social.getUrl();
                binding.urlType.setVisibility(View.VISIBLE);
                break;
            }
            case "paypal": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                Social social = (Social) getIntent().getSerializableExtra("social");
                bmp = QRCode.from(social.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(social.getUrl() == null)) {
                    resultdatalist.add(social.getUrl());
                }
                binding.urlTitleImage.setImageResource(R.drawable.paypal);
                binding.urlTitle.setText(R.string.paypal);
                binding.urlImage.setImageResource(R.drawable.paypal);
                binding.urlTypeName.setText(R.string.paypal);

                binding.url.setText(social.getUrl());
                Linkify.addLinks(binding.url, Linkify.WEB_URLS);
                autoSearch(social.getUrl());

                copyClipboard = social.getUrl();
                link = social.getUrl();
                binding.urlType.setVisibility(View.VISIBLE);
                break;
            }
            case "GeoInfo": {
                binding.geoLayout.setVisibility(View.VISIBLE);
                GeoInfo geoInfo = (GeoInfo) getIntent().getSerializableExtra("geoInfo");
                bmp = QRCode.from(geoInfo.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(geoInfo.getPoints().get(0) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(0));
                }
                if (!(geoInfo.getPoints().get(1) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(1));
                }
                if (!(geoInfo.getPoints().get(2) == null)) {
                    resultdatalist.add(geoInfo.getPoints().get(2));
                }
                List<String> getPoints = geoInfo.getPoints();
                binding.longitutde.setText("" + getPoints.get(1));
                binding.latitude.setText("" + getPoints.get(2));
                binding.geoName.setText("" + getPoints.get(0));

                lat = getPoints.get(2);
                lon = getPoints.get(1);
                nam = getPoints.get(0);

                break;
            }
            case "Sms": {
                binding.smsLayout.setVisibility(View.VISIBLE);
                SMS sms = (SMS) getIntent().getSerializableExtra("sms");
                bmp = QRCode.from(sms.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (!(sms.getNumber() == null)) {
                    resultdatalist.add(sms.getNumber());
                }
                if (!(sms.getSubject() == null)) {
                    resultdatalist.add(sms.getSubject());
                }

                copyClipboard = sms.getNumber();
                link = sms.getNumber();
                binding.subject.setText(sms.getSubject());
                binding.number.setText(sms.getNumber());

                break;
            }
            case "Text": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                binding.browserBtn.setVisibility(View.GONE);
                String text = getIntent().getStringExtra("text");
                bmp = QRCode.from(text).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (null != text) {
                    resultdatalist.add(text);
                }
                binding.urlTitleImage.setImageResource(R.drawable.ic_text);
                binding.urlTitle.setText(R.string.text);
                binding.url.setText(text);
                copyClipboard = text;
                link = text;
                checkSearchEngine(text);
                break;
            }
            case "clipboard": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                binding.browserBtn.setVisibility(View.GONE);
                String text = getIntent().getStringExtra("text");
                bmp = QRCode.from(text).bitmap();
                binding.imageView4.setImageBitmap(bmp);
                if (null != text) {
                    resultdatalist.add(text);
                }
                binding.urlTitleImage.setImageResource(R.drawable.clipboard);
                binding.urlTitle.setText(R.string.clipboard);
                checkSearchEngine(text);
                copyClipboard = text;
                link = text;
                binding.url.setText(text);
                break;
            }
            case "Barcode": {
                binding.urlLayout.setVisibility(View.VISIBLE);
                String textBarcode = getIntent().getStringExtra("barcode");
                int barcodeFormat = getIntent().getIntExtra("barcodeFormat", Barcode.EAN_13);
                try {
                    Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    Writer codeWriter;
                    BitMatrix byteMatrix;
                    if (barcodeFormat == Barcode.EAN_8){
                        codeWriter = new EAN8Writer();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.EAN_8, 700, 300, hintMap);
                    } else if (barcodeFormat == Barcode.CODE_128) {
                        codeWriter = new Code128Writer();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.CODE_128, 700, 300, hintMap);
                    } else if (barcodeFormat == Barcode.CODE_93) {
                        codeWriter = new Code93Writer();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.CODE_93, 700, 300, hintMap);
                    } else if (barcodeFormat == Barcode.UPC_A) {
                        codeWriter = new UPCAWriter();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.UPC_A, 700, 300, hintMap);
                    }  else if (barcodeFormat == Barcode.UPC_E) {
                        codeWriter = new UPCEWriter();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.UPC_E, 700, 300, hintMap);
                    } else {
                        codeWriter = new EAN13Writer();
                        byteMatrix  = codeWriter.encode(textBarcode, BarcodeFormat.EAN_13, 700, 300, hintMap);
                    }
                    int width = byteMatrix.getWidth();
                    int height = byteMatrix.getHeight();
                    bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            bmp.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    binding.imageView4.setImageBitmap(bmp);
//                    if (image != null) {
//                        binding.imageView4.setImageBitmap(bitmap);
//                    } else {
//                        binding.imageView4.setImageBitmap(bmp);
//                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (textBarcode != null) {
                    resultdatalist.add(textBarcode);
                }
                binding.urlTitleImage.setImageResource(R.drawable.barcode);
                binding.urlTitle.setText(R.string.barcode);
                binding.url.setText(textBarcode);
                binding.storesBtn.setVisibility(View.VISIBLE);

                ISBN = textBarcode;

                if (getProductPreference()) {
                    //progressDialog.show();

                    String url = "https://go-upc.com/search?q=" + textBarcode;

                    Log.d("HTMLCHE" , url);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Document doc = Jsoup.connect(url).get();
                           //     Log.d("HTMLCHE", doc.html().toString());
                                Elements name = doc.getElementsByClass("product-name");
                                String n = name.get(0).text();
                                Elements image = doc.getElementsByClass("product-image mobile");
                                Element img = image.get(0).child(0);
                                String link = img.attr("src");

                                Log.d("HTMLCHE" , "LINK    " + link);
                                Log.d("HTMLCHE" ,"NAME    " +  n);

                                Elements table = doc.getElementsByClass("table-striped");
                                Element body = table.get(0);
                                Element tr = body.child(body.childrenSize() - 1);
                                Element td = tr.child(tr.childrenSize() - 1);
                                String cat = td.text().toString();

                                Log.d("HTMLCHE" ,"CAT    " +  cat);
                                runOnUiThread(() -> {
                                    Glide.with(ScanResultActivity.this).load(link).into(binding.productImage);
                                    binding.productName.setText(n);
                                    binding.productISBN.setText(textBarcode);
                                    binding.productCategory.setText(cat);
                                    binding.productLayout.setVisibility(View.VISIBLE);
                                });

                            } catch (IOException | IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Log.d("HTMLCHE",  "ERROR " +  e.getMessage());
                                runOnUiThread(() -> {
                                   // Toast.makeText(ScanResultActivity.this, "Product detail not found", Toast.LENGTH_SHORT).show();
                                    binding.productLayout.setVisibility(View.GONE);
                                });
                            }
                        }
                    }).start();

                } else {
                    binding.productLayout.setVisibility(View.GONE);
                }

                copyClipboard = ISBN;
//                bmp = QRCode.from(textBarcode).bitmap();
//                binding.imageView4.setImageBitmap(bmp);
//                icon.setImageResource(R.drawable.ic_barcode);
//                tvHead.setText(R.string.barcode);
//                tvTitle.setText(textBarcode);
//                if (textBarcode.contains("http:") || textBarcode.contains("https:") || textBarcode.contains("www")) {
//                    Linkify.addLinks(tvTitle, Linkify.WEB_URLS);
//                    autoSearch(textBarcode);
//                }
//                // if (textBarcode.contains()){
//                checkSearchEngine(textBarcode);
//                // }

                break;
            }
            case "Event": {
                binding.eventLayout.setVisibility(View.VISIBLE);
                IEvent iEvent = (IEvent) getIntent().getSerializableExtra("event");
                bmp = QRCode.from(iEvent.toString()).bitmap();
                binding.imageView4.setImageBitmap(bmp);
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
                binding.eventName.setText(iEvent.getUid());
                binding.eventSubject.setText(iEvent.getStamp());
                binding.eventStart.setText(iEvent.getStart());
                binding.eventEnd.setText(iEvent.getEnd());

                copyClipboard = iEvent.getUid() + "\n" + iEvent.getStamp() + "\n" + iEvent.getStart() + "\n" + iEvent.getEnd();
                break;
            }

            default:
                Toast.makeText(this, "Not Valid type", Toast.LENGTH_SHORT).show();
                break;
        }


        // ResultAdapter messageAdapter = new ResultAdapter(resultdatalist);
        // recyclerView.setAdapter(messageAdapter);

        //ButtonResultAdapter buttonResultAdapter = new ButtonResultAdapter(this, buttonResultdatalist);
        //recyclerViewButtons.setAdapter(buttonResultAdapter);
        getLocale();
    }

    private void autoSearch(String url) {
        if (web) {
            Uri webpage = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            try {
                startActivity(webIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String customUrl = "";
    String copyClipboard = "";

    private void checkSearchEngine(String text) {
        if (web) {
            if (engine.equals("Google")) {
                customUrl = "https://www.google.com/search?q=" + text;
            } else if (engine.equals("Bing")) {
                customUrl = "https://www.bing.com/search?q=" + text;
            } else if (engine.equals("Yahoo")) {
                customUrl = "https://search.yahoo.com/search?p=" + text;
            } else if (engine.equals("DuckDuckGo")) {
                customUrl = "https://duckduckgo.com/?q=" + text;
            } else if (engine.equals("Yandex")) {
                customUrl = "https://yandex.com/?q" + text;
            } else if (engine.equals("Qwant")) {
                customUrl = "https://www.qwant.com/?q=" + text;
            }
            Uri webpage = Uri.parse(customUrl);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            try {
                startActivity(webIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String ISBN = "";

    public void ebayOpen(View view){
        String isbnNumber = ISBN;
        String searchUrl = "https://www.ebay.com/sch/i.html?_nkw=" + isbnNumber;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        boolean ebayAppInstalled = isPackageInstalled("com.ebay.mobile");

        if (ebayAppInstalled) {
            intent.setPackage("com.ebay.mobile");
        } else {
            intent.setPackage(null);
        }

        startActivity(intent);
    }

    public void amazonOpen(View view){
        String isbnNumber = ISBN;
        String searchUrl = "https://www.amazon.com/s?k=" + isbnNumber;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        boolean amazonAppInstalled = isPackageInstalled("com.amazon.mobile.shopping");

        if (amazonAppInstalled) {
            intent.setPackage("com.amazon.mobile.shopping");
        } else {
            intent.setPackage(null);
        }
        startActivity(intent);
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void getLocale() {

        String lang = prefs.getString("lang", "");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    String  lat, lon, nam;

    public void shareGeo(View view){
        String latitude = lat;
        String longitude = lon;
        String label = nam;

        String uri = "geo:" + latitude + "," + longitude + "?q=" + Uri.encode(latitude + "," + longitude + "(" + label + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "You don\'t have any apps install", Toast.LENGTH_SHORT).show();
        }

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

    String link = "";

    public void openBrowser(View view){
        switch (type) {
            case "EMail": {
                String recipientEmail = "recipient@example.com";
                String subject = "Hello";
                String body = "This is the body of the email.";
                Uri uri = Uri.parse("mailto:" + recipientEmail)
                        .buildUpon()
                        .appendQueryParameter("subject", subject)
                        .appendQueryParameter("body", body)
                        .build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Send email"));
                break;
            }
            case "telephone": {
                String phoneNumber = link;
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
            }
            case "spotify": {
                String spotifyUrl = link;
                Intent spotifyIntent = new Intent(Intent.ACTION_VIEW);
                spotifyIntent.setData(Uri.parse(spotifyUrl));
                startActivity(spotifyIntent);
                break;
            }
            case "whatsapp": {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", link, ""))));
                break;
            }
            case "viber": {
                try {
                    String viberContact = link;
                    Uri viberUri = Uri.parse("tel:" + viberContact);
                    Intent viberIntent = new Intent("android.intent.action.VIEW");
                    viberIntent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                    viberIntent.setData(viberUri);
                    startActivity(viberIntent);
                } catch (Exception e){
                    String viberNumber = link;
                    String viberUrl = "viber://chat?number=" + viberNumber;
                    Intent viberIntent = new Intent(Intent.ACTION_VIEW);
                    viberIntent.setData(Uri.parse(viberUrl));
                    startActivity(viberIntent);
                }
                break;
            }
            case "url":
            case "paypal":
            case "twitter":
            case "facebook":
            case "insta":
            case "youtube": {
                String url = link;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;
            }

            case "Sms": {
                String phoneNumber = link;
                String message = "";
                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                smsIntent.putExtra("sms_body", message);
                startActivity(smsIntent);

                break;
            }

            default:
                Toast.makeText(this, "Not Valid type", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setCopyClipboard(View view){
        String str = copyClipboard;
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", str));
        Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
    }

    public void shareContent(View view) {
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), separator + "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),  "com.moutamid.qr.scanner.generator.provider", file);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/PNG");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareText(View view){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = link;
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "Share Using"));
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
        onBackPressed();
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

    public void openWIFI(View view){
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    public boolean getProductPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean("product", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dismiss the toast if it is being shown
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }


    @Override
    public void onBackPressed() {
       // startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}