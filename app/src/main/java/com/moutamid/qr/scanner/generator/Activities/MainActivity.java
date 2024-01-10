package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //    private RelativeLayout cardViewHide;
    static Uri picUri;
    private HistoryVM historyVM;
    static String contents;
    private BottomSheetDialog bottomSheetDialog;

    private List<String> skuList;
    private BottomSheetDialog bottomSheetSubscription;
    private int selectSubscription = 1;
    private BillingClient billingClient;
    private ImageView imgRemoveAd;
    private RadioButton radioButton2;
    private RadioGroup radioGroup;
    private CAMediatedBannerView mediatedBannerView;
    private SharedPreferences prefs;
    private static final int MY_REQUEST_CODE = 1000; // You can choose any code you like
    BottomNavigationView bottomNavigationView;
    CardView cardView;
    View navLay;
    ImageButton flashBtn, modeBtn, galleryBtn;
    private AppUpdateManager appUpdateManager;


    @SuppressLint({"ResourceAsColor", "MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(MainActivity.this);
        setContentView(R.layout.activity_main);
        Constants.checkApp(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        imgRemoveAd = findViewById(R.id.img_ad);
        // RecyclerView recyclerViewMain = findViewById(R.id.recycler_main_btn);
        // cardViewHide = findViewById(R.id.cardView_seekbar);

        boolean theme = prefs.getBoolean("theme", false);
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

        bottomSheetSubscription = new BottomSheetDialog(this);
        bottomSheetSubscription.setContentView(R.layout.subscription_layout);
        radioGroup = bottomSheetSubscription.findViewById(R.id.rgRight);
        radioButton2 = bottomSheetSubscription.findViewById(R.id.radio2);
        mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, MainActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        getLocale();
        historyVM = new ViewModelProvider(this).get(HistoryVM.class);
        inAppPurchases();
        //checkPermissions();
        Stash.put("ONBACKPRESS", 0);

        navLay = findViewById(R.id.navLay);
        flashBtn = navLay.findViewById(R.id.flashBtn);
        modeBtn = navLay.findViewById(R.id.modeBtn);
        galleryBtn = navLay.findViewById(R.id.galleryBtn);
        bottomNavigationView = navLay.findViewById(R.id.bottomNavigationView);
        cardView = navLay.findViewById(R.id.buttonsLayout);
        cardView.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new QRScanFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.scan);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.scan) {
                Stash.put("ONBACKPRESS", 0);
                cardView.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new QRScanFragment()).addToBackStack(QRScanFragment.TAG).commit();
            } else if (itemId == R.id.generate_qr) {
                Stash.put("ONBACKPRESS", 1);
                cardView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CreateFragment()).addToBackStack(CreateFragment.TAG).commit();
            } else if (itemId == R.id.history) {
                Stash.put("ONBACKPRESS", 2);
                cardView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HistoryActivity()).addToBackStack(HistoryActivity.TAG).commit();
            } else if (itemId == R.id.settings) {
                Stash.put("ONBACKPRESS", 3);
                cardView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MySettingsFragment()).addToBackStack(MySettingsFragment.TAG).commit();
            }
            return true;
        });
    }

/*
    public void flashButton(View view) {
        if (isFlash) {
            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            flashBtn.setImageResource(R.drawable.flashon);
            isFlash = false;
        } else {
            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            isFlash = true;
            flashBtn.setImageResource(R.drawable.flashoff);
        }
    }

    public void btnGallery(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 2);
    }

    public void btnMode(View view) {
        mPreview.stop();
        if (checkSoundPreferences()) {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        }
        if (checkVibratePreferences()) {
            Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(100);
            }
        }
        if (cameraMode.equals("normal")) {
            cameraMode = "batch";
            createCameraSource(true, false, false, cameraMode);
            modeTxt.setText(R.string.mode1);
        } else {
            cameraMode = "normal";
            createCameraSource(true, false, false, cameraMode);
            modeTxt.setText(R.string.mode3);
        }
    }
*/

    private void getLocale() {

        String lang = prefs.getString("lang", "");
        setLocale(lang);
    }

    public void transparentStatusBar(boolean transparent) {
        if (transparent) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple));
        }
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bottomNavigationView.setSelectedItemId(R.id.scan);
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void processResultBarcode(String text) {
        Intent intent = new Intent(this, ScanResultActivity.class);
        try {
            History contactHistory = new History(text, "barcode", false);
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcode", text);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
        } catch (Exception t) {
            Toast.makeText(this, "not scan", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }

/*    public void processRawResult(String text) {

        Intent intent = new Intent(this, ScanResultActivity.class);
//Vcard Scanning
        try {
            VCard vCard = new VCard();
            vCard.parseSchema(text);
            History contactHistory = new History(vCard.generateString(), "contact", scan);
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "VCard");
            intent.putExtra("vCard", vCard);
            startActivity(intent);
        } catch (IllegalArgumentException e) {

            try {
                EMail eMail = new EMail();
                eMail.parseSchema(text);
                History contactHistory = new History(eMail.generateString(), "email", scan);
                historyVM.insertHistory(contactHistory);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
            } catch (IllegalArgumentException email) {
                try {
                    Wifi wifi = new Wifi();
                    wifi.parseSchema(text);
                    History contactHistory = new History(wifi.generateString(), "wifi", scan);
                    historyVM.insertHistory(contactHistory);
                    intent.putExtra("type", "wifi");
                    intent.putExtra("Wifi", wifi);
                    startActivity(intent);
                } catch (IllegalArgumentException wifi) {
                    try {
                        Telephone telephone = new Telephone();
                        telephone.parseSchema(text);
                        History contactHistory = new History(telephone.generateString(), "phone", scan);
                        historyVM.insertHistory(contactHistory);
                        intent.putExtra("type", "telephone");
                        intent.putExtra("phone", telephone);
                        startActivity(intent);
                    } catch (IllegalArgumentException telephone) {
                        try {
                            Url url = new Url();
                            url.parseSchema(text);
                            History contactHistory = new History(url.generateString(), "url", scan);
                            historyVM.insertHistory(contactHistory);
                            intent.putExtra("type", "url");
                            intent.putExtra("Url", url);
                            startActivity(intent);
                        } catch (IllegalArgumentException url) {
                            try {
                                YouTube youTube = new YouTube();
                                youTube.parseSchema(text);
                                History contactHistory = new History(youTube.generateString(), "youtube", scan);
                                historyVM.insertHistory(contactHistory);
                                intent.putExtra("type", "YouTube");
                                intent.putExtra("youtube", youTube);
                                startActivity(intent);
                            } catch (IllegalArgumentException youtube) {
                                try {
                                    GeoInfo geoInfo = new GeoInfo();
                                    geoInfo.parseSchema(text);
                                    History contactHistory = new History(geoInfo.generateString(), "location", scan);
                                    historyVM.insertHistory(contactHistory);
                                    intent.putExtra("type", "GeoInfo");
                                    intent.putExtra("geoInfo", geoInfo);
                                    startActivity(intent);
                                } catch (IllegalArgumentException geoinfo) {
                                    try {
                                        SMS sms = new SMS();
                                        sms.parseSchema(text);
                                        History contactHistory = new History(sms.generateString(), "sms", scan);
                                        historyVM.insertHistory(contactHistory);
                                        intent.putExtra("type", "Sms");
                                        intent.putExtra("sms", sms);
                                        startActivity(intent);
                                    } catch (IllegalArgumentException sms) {
                                        try {
                                            IEvent iEvent = new IEvent();
                                            iEvent.parseSchema(text);
                                            History contactHistory = new History(iEvent.generateString(), "event", scan);
                                            historyVM.insertHistory(contactHistory);
                                            intent.putExtra("type", "Event");
                                            intent.putExtra("event", iEvent);
                                            startActivity(intent);
                                        } catch (IllegalArgumentException event) {
                                            try {
                                                intent.putExtra("type", "Text");
                                                intent.putExtra("text", text);
                                                startActivity(intent);

                                            } catch (Exception t) {
                                                Toast.makeText(this, "not scan", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/

  /*  @SuppressLint("ResourceAsColor")
    @Override
    public void clickedItem(View view, int position) {
        if (position == 0) {
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
            loadQRfragment();

        } else if (position == 1) {
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
            loadMenuFragment();
        } else if (position == 2) {

            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
        } else if (position == 3) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
        }
    }

    @Override
    public void clickedItemButton(View view, int position, String type) {

    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*int i = Stash.getInt("ONBACKPRESS", 0);
        if (i == 0) {
            super.onBackPressed();
        } else if (i == 1) {
            bottomNavigationView.setSelectedItemId(R.id.scan);
        } else if (i == 2) {
            bottomNavigationView.setSelectedItemId(R.id.generate_qr);
        } else if (i == 3) {
            bottomNavigationView.setSelectedItemId(R.id.history);
        }*/


/*        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_rating_bar);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyshow = prefs.getBoolean("dialogpref", false);
        if (!previouslyshow) {
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        else {
            TextView title, subtitle, body, sponsored;
            CANativeAdView adView;
            CAAdChoicesView adChoicesView;
            CAAppIconView appIconView;
            CAMediaView mediaView;
            CACallToActionView actionView;
            final MediatedNativeAd[] mediatedNativeAd = new MediatedNativeAd[1];
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(R.layout.exitdialog);
            title = bottomSheetDialog.findViewById(R.id.native_ad_title);
            subtitle = bottomSheetDialog.findViewById(R.id.native_ad_sub_title);
            body = bottomSheetDialog.findViewById(R.id.native_ad_body);
            sponsored = bottomSheetDialog.findViewById(R.id.native_ad_sponsored_label);
            adView = bottomSheetDialog.findViewById(R.id.native_ad_frame);
            adChoicesView = bottomSheetDialog.findViewById(R.id.ad_choices_container);
            appIconView = bottomSheetDialog.findViewById(R.id.native_ad_icon);
            mediaView = bottomSheetDialog.findViewById(R.id.native_ad_media);
            actionView = bottomSheetDialog.findViewById(R.id.native_ad_call_to_action);

            if (isNetworkConnected()) {
                ConsoliAds.Instance().loadNativeAd(NativePlaceholderName.Activity1, MainActivity.this, new ConsoliAdsNativeListener() {
                    @Override
                    public void onNativeAdLoaded(MediatedNativeAd ad) {
                        Log.i("ConsoliAdsListners", "onNativeAdLoaded");
                        mediatedNativeAd[0] = ad;
                        assert adView != null;
                        adView.setVisibility(View.VISIBLE);
                        assert actionView != null;
                        actionView.setTextColor("#ffffff");
                        actionView.setTextSize_UNIT_SP(12);
                        mediatedNativeAd[0].setSponsered(sponsored);
                        mediatedNativeAd[0].setAdTitle(title);
                        mediatedNativeAd[0].setAdSubTitle(subtitle);
                        mediatedNativeAd[0].setAdBody(body);
                        mediatedNativeAd[0].registerViewForInteraction(MainActivity.this, appIconView, mediaView, actionView, adView, adChoicesView);
                        bottomSheetDialog.show();
                    }

                    @Override
                    public void onNativeAdLoadFailed() {
                        finishAffinity();
                        finish();
                    }

                    @Override
                    public void onNativeAdFailedToShow() {
                        finishAffinity();
                        finish();
                    }
                });
            } else {
                finish();
            }
        }
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {

            if (ratingBar1.getRating() > 0 && ratingBar1.getRating() <= 3) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.our_email_id)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT   , getString(R.string.email_body));
                startActivity(emailIntent);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("dialogpref", Boolean.TRUE);
                edit.apply();
                dialog.dismiss();
            } else if (ratingBar1.getRating() > 3) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("dialogpref", Boolean.TRUE);
                edit.apply();
                dialog.dismiss();

            } else {
                Toast.makeText(getApplicationContext(), "Please give rating", Toast.LENGTH_SHORT).show();
            }
            finish();
        });*/

    }

    public void removeAds(View view) {
        Spannable spannable = new SpannableString(getString(R.string._13_99_year));
        spannable.setSpan(new ForegroundColorSpan(Color.GRAY),
                14, 29,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5ED9EF")),
                29, 47,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(28), 29, 47, 0);

        assert radioButton2 != null;
        radioButton2.setText(spannable);

        SpannableString ss = new SpannableString(getString(R.string.subscription_policy));
        StyleSpan bss1 = new StyleSpan(android.graphics.Typeface.BOLD);
        ClickableSpan privacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(privacyPolicy, 412, 426, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(bss1, 412, 426, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLACK),
                412, 426,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = bottomSheetSubscription.findViewById(R.id.tv_ad_terms);
        assert textView != null;
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        bottomSheetSubscription.show();

        Button continueBt = bottomSheetSubscription.findViewById(R.id.buttonContinue);
        assert continueBt != null;
        continueBt.setOnClickListener(v -> {
            if (billingClient.isReady()) {
                skuQueryOnContinue();
                bottomSheetSubscription.dismiss();
            }
        });

    }

    public void exitApp(View view) {
        finish();
    }

    public void dismiss(View view) {
        bottomSheetDialog.dismiss();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void skuQueryOnContinue() {
        try {
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            if (skuList.size() > 0) {
                if (selectSubscription <= 1)
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                else {
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    selectSubscription = 0;
                }
                billingClient.querySkuDetailsAsync(params.build(), (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        try {
                            assert list != null;
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(list.get(selectSubscription))
                                    .build();
                            int responseCode = billingClient.launchBillingFlow(MainActivity.this, flowParams).getResponseCode();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void skuProductList() {
        skuList = new ArrayList<>();
        skuList.add(getResources().getString(R.string.per_month_subscription));
        skuList.add(getResources().getString(R.string.per_year_subscription));
        skuList.add(getResources().getString(R.string.one_time_purchase));

    }

    public void inAppPurchases() {
        // To be implemented in a later section.
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getBaseContext());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(MainActivity.this.getString(R.string.adsubscribed), Boolean.TRUE);
                edit.apply();
                imgRemoveAd.setImageResource(R.drawable.pro);
                mediatedBannerView.setVisibility(View.INVISIBLE);
            }
        };
        ///create client billing
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        //make connect to googlonrese play store
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    boolean value = checkUserSubscription();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(getString(R.string.adsubscribed), value);
                    edit.apply();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
        skuProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (getPurchaseSharedPreference()){
//            mediatedBannerView.setVisibility(View.INVISIBLE);
//            imgRemoveAd.setImageResource(R.drawable.pro);
//            imgRemoveAd.setEnabled(false);
//        }
//        else {
//            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, MainActivity.this, mediatedBannerView);
//            ConsoliAds.Instance().LoadInterstitial();
//            mediatedBannerView.setVisibility(View.VISIBLE);
//            imgRemoveAd.setImageResource(R.drawable.ad);
//            imgRemoveAd.setEnabled(true);
//        }

        // super.onResume();
    }

    private boolean checkUserSubscription() {
        assert billingClient != null;
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        List<Purchase> purchase = purchasesResult.getPurchasesList();
        if (purchase != null) {
            for (int i = 0; i < purchase.size(); i++) {
                String sku = purchase.get(i).getSku();
                if (sku.equals(getString(R.string.per_month_subscription))) {
                    if (purchase.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        return true;
                    }
                } else if (sku.equals(getString(R.string.per_year_subscription))) {
                    if (purchase.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        return true;
                    }
                }
            }
        }

        purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        purchase = purchasesResult.getPurchasesList();
        if (purchase != null) {
            for (int i = 0; i < purchase.size(); i++) {
                String sku = purchase.get(i).getSku();
                if (sku.equals(getString(R.string.one_time_purchase))) {
                    if (purchase.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void checkButton(View view) {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        selectSubscription = radioGroup.indexOfChild(radioButton);
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}