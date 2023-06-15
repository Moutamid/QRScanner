package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.ZoomChangeListener;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.moutamid.qr.scanner.generator.utils.formates.YouTube;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

import java.io.FileNotFoundException;
import java.io.InputStream;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanFragment extends Fragment implements ZXingScannerView.ResultHandler, ZoomChangeListener {

    private static ZXingScannerView mScannerView;
    private Context context;
    private HistoryVM historyVM;

    private int progress = 0;
    static String contents;
    static Uri picUri;
    private ZoomChangeListener listener;
    private boolean isFlash = false;
    //private final ArrayList<ButtonMainModel> mainDataList = new ArrayList<>();
    private ImageView flashon,zoom_minus,zoom_plus,switchBtn, galleryBtn,modeBtn;
    private SeekBar seekBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        assert container != null;
        context = container.getContext();
        mScannerView = new ZXingScannerView(getActivity());
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().LoadInterstitial();
        }
        return inflater.inflate(R.layout.fragment_q_rscan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       mScannerView = view.findViewById(R.id.scanner_view);
        zoom_minus = view.findViewById(R.id.minus_img);
        zoom_plus = view.findViewById(R.id.plus_img);
        flashon = view.findViewById(R.id.flash_check);
        modeBtn = view.findViewById(R.id.mode_check);
        switchBtn = view.findViewById(R.id.camera_switch);
        galleryBtn = view.findViewById(R.id.beep_check);
        seekBar = view.findViewById(R.id.zoom_sb);

        historyVM = new ViewModelProvider(getActivity()).get(HistoryVM.class);
        mScannerView.startCamera();
        zoom_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress +=10;
                seekBar.setProgress(progress);
            }
        });

        zoom_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress -=10;
                seekBar.setProgress(progress);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGallery();
            }
        });
        flashon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashButton();
            }
        });
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSwitch();
            }
        });
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMode();
            }
        });
    }


    private void flashButton() {
        if (!isFlash) {
            QRScanFragment.setFlashLight(true);
            flashon.setImageResource(R.drawable.ic_baseline_flash_on_24);
            isFlash = true;
        } else {
            QRScanFragment.setFlashLight(false);
            flashon.setImageResource(R.drawable.ic_baseline_flash_off_24);
            isFlash = false;
        }
    }

    private void btnGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 2);
    }

    private void btnMode() {
    }


    private boolean isSwitch = false;
    private void btnSwitch() {
        if (isSwitch) {
            mScannerView.startCamera(0);
            isSwitch = false;
        }else {
            mScannerView.startCamera(1);
            isSwitch = true;
        }
        //Toast.makeText(context, ""+cameraId, Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                assert data != null;
                final Uri imageUri = data.getData();
                picUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                try {
                    Bitmap bMap = selectedImage;
                    contents = null;
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    Result result = reader.decode(bitmap);
                    if (!result.getText().isEmpty()) {
                        BarcodeFormat barcodeFormat = result.getBarcodeFormat();
                        if (barcodeFormat.equals(BarcodeFormat.QR_CODE)) {
                            processRawResult(result.getText());
                        } else {
                            processResultBarcode(result.getText());
                        }
                    } else {
                        Toast.makeText(getActivity(), "Not supported", Toast.LENGTH_SHORT).show();
                    }
                    contents = result.getText();

                } catch (FormatException | ChecksumException | NotFoundException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "You have not picked any Image", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onDestroy() {
        mScannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mScannerView.stopCamera();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        if (!result.getText().isEmpty()) {
            BarcodeFormat barcodeFormat = result.getBarcodeFormat();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean beepSound = prefs.getBoolean("beepsound", true);
            boolean vibrate = prefs.getBoolean("vibrate", true);
            if (barcodeFormat.equals(BarcodeFormat.QR_CODE)) {

                if (beepSound) {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                }
                if (vibrate) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(300);
                }
                processRawResult(result.getText());
            } else {

                if (beepSound) {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                }
                if (vibrate) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(300);
                }
                processResultBarcode(result.getText());
            }
        }
      else {
            Toast.makeText(context, "Not supported", Toast.LENGTH_SHORT).show();
        }

    }
    public void processResultBarcode(String text){
        Intent intent = new Intent(getActivity(), ScanResultActivity.class);
        try {
            History contactHistory = new History(text, "barcode");
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcode", text);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }

        } catch (Exception t) {
            Toast.makeText(context, "not scan", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }
    public  void processRawResult(String text) {

        Intent intent = new Intent(getActivity(), ScanResultActivity.class);

        try {
            VCard vCard = new VCard();
            vCard.parseSchema(text);
            History contactHistory = new History(vCard.generateString(), "contact");
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "VCard");
            intent.putExtra("vCard", vCard);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        } catch (IllegalArgumentException e) {

            try {
                EMail eMail = new EMail();
                eMail.parseSchema(text);
                History contactHistory = new History(eMail.generateString(), "email");
                historyVM.insertHistory(contactHistory);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
            } catch (IllegalArgumentException email) {
                try {
                    Wifi wifi = new Wifi();
                    wifi.parseSchema(text);
                    History contactHistory = new History(wifi.generateString(), "wifi");
                    historyVM.insertHistory(contactHistory);
                    intent.putExtra("type", "wifi");
                    intent.putExtra("Wifi", wifi);
                    startActivity(intent);
                    if (!getPurchaseSharedPreference()) {
                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                    }
                } catch (IllegalArgumentException wifi) {
                    try {
                        Telephone telephone = new Telephone();
                        telephone.parseSchema(text);
                        History contactHistory = new History(telephone.generateString(), "phone");
                        historyVM.insertHistory(contactHistory);
                        intent.putExtra("type", "telephone");
                        intent.putExtra("phone", telephone);
                        startActivity(intent);
                        if (!getPurchaseSharedPreference()) {
                            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                        }
                    } catch (IllegalArgumentException telephone) {
                        try {
                            Url url = new Url();
                            url.parseSchema(text);
                            History contactHistory = new History(url.generateString(), "url");
                            historyVM.insertHistory(contactHistory);
                            intent.putExtra("type", "url");
                            intent.putExtra("Url", url);
                            startActivity(intent);
                            if (!getPurchaseSharedPreference()) {
                                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                            }
                        } catch (IllegalArgumentException url) {
                            try {
                                final Social social = new Social();
                                social.parseSchema(text);
                                History urlHistory = new History(social.generateString(), "social");
                                historyVM.insertHistory(urlHistory);
                                intent.putExtra("type", "Social");
                                intent.putExtra("social", social);
                                startActivity(intent);
                                if (!getPurchaseSharedPreference()) {
                                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                }
                            } catch (IllegalArgumentException youtube) {
                                try {
                                    GeoInfo geoInfo = new GeoInfo();
                                    geoInfo.parseSchema(text);
                                    History contactHistory = new History(geoInfo.generateString(), "location");
                                    historyVM.insertHistory(contactHistory);
                                    intent.putExtra("type", "GeoInfo");
                                    intent.putExtra("geoInfo", geoInfo);
                                    startActivity(intent);
                                    if (!getPurchaseSharedPreference()) {
                                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                    }
                                } catch (IllegalArgumentException geoinfo) {
                                    try {
                                        SMS sms = new SMS();
                                        sms.parseSchema(text);
                                        History contactHistory = new History(sms.generateString(), "sms");
                                        historyVM.insertHistory(contactHistory);
                                        intent.putExtra("type", "Sms");
                                        intent.putExtra("sms", sms);
                                        startActivity(intent);
                                        if (!getPurchaseSharedPreference()) {
                                            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                        }
                                    } catch (IllegalArgumentException sms) {
                                        try {
                                            IEvent iEvent = new IEvent();
                                            iEvent.parseSchema(text);
                                            History contactHistory = new History(iEvent.generateString(), "event");
                                            historyVM.insertHistory(contactHistory);
                                            intent.putExtra("type", "Event");
                                            intent.putExtra("event", iEvent);
                                            startActivity(intent);
                                            if (!getPurchaseSharedPreference()) {
                                                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                            }
                                        } catch (IllegalArgumentException event) {
                                            try {
                                                History contactHistory = new History(text, "text");
                                                historyVM.insertHistory(contactHistory);
                                                intent.putExtra("type", "Text");
                                                intent.putExtra("text", text);
                                                startActivity(intent);
                                                if (!getPurchaseSharedPreference()) {
                                                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                                }
                                            } catch (Exception t) {
                                                Toast.makeText(context, "not scan", Toast.LENGTH_SHORT).show();
                                                t.printStackTrace();
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
        }

    public static void setFlashLight(Boolean b) {
        mScannerView.setFlash(b);
    }

    @Override
    public void onZoomChangeListener(int i) {
        Toast.makeText(context, ""+i, Toast.LENGTH_SHORT).show();
    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}