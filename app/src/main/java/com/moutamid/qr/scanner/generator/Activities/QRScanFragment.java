package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSource;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSourcePreview;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.moutamid.qr.scanner.generator.R;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

import static com.unity3d.services.core.misc.Utilities.runOnUiThread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class QRScanFragment extends Fragment {

  //  private static ZXingScannerView mScannerView;
    private Context context;
    private HistoryVM historyVM;

    private int zoomProgress = 5;
    private SharedPreferences prefs;
    static String contents;
    static Uri picUri;
    private RelativeLayout imageLayout,cardLayout;

    private Camera.Parameters parameters;
    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private BarcodeCapture barcodeCapture;
    private boolean isFlash = false;
    private static final int RC_HANDLE_GMS = 9001;
    //private final ArrayList<ButtonMainModel> mainDataList = new ArrayList<>();
    private ImageView flashon,zoom_minus,zoom_plus,switchBtn, galleryBtn,modeBtn;
    private SeekBar seekBar;
    private TextView modeTxt;
    private boolean shouldShowText, multipleScan, showDrawRect, touchAsCallback, shouldFocus, showFlash = false;
    CameraSource.Builder builder;
    private boolean autoFocus = true;
    private boolean useFlash = false;
    private byte[] imgByte = null;
    private boolean cameraSwitch = false;

    private Integer[] rectColors;

    private int barcodeFormat, cameraFacing;

    private ImageView doneImg;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    BarcodeDetector barcodeDetector;
    private String cameraMode;
    private String textBarcode = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        assert container != null;
        context = container.getContext();
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().LoadInterstitial();
        }
        return inflater.inflate(R.layout.fragment_q_rscan, container, false);
    }


    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.gvb);
        showFlash = a.getBoolean(R.styleable.gvb_gvb_flash, false);
        showDrawRect = a.getBoolean(R.styleable.gvb_gvb_draw, false);
        shouldShowText = a.getBoolean(R.styleable.gvb_gvb_show_text, false);
        shouldFocus = a.getBoolean(R.styleable.gvb_gvb_auto_focus, false);
        touchAsCallback = a.getBoolean(R.styleable.gvb_gvb_touch, false);
        multipleScan = a.getBoolean(R.styleable.gvb_gvb_multiple, false);
        barcodeFormat = a.getInt(R.styleable.gvb_gvb_code_format, 0);
        cameraFacing = a.getInt(R.styleable.gvb_gvb_camera_facing, CameraSource.CAMERA_FACING_BACK);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       //mScannerView = view.findViewById(R.id.scanner_view);
        zoom_minus = view.findViewById(R.id.minus_img);
        zoom_plus = view.findViewById(R.id.plus_img);
        flashon = view.findViewById(R.id.flash_check);
        modeBtn = view.findViewById(R.id.mode_check);
        modeTxt = view.findViewById(R.id.mode_status);
        switchBtn = view.findViewById(R.id.camera_switch);
        galleryBtn = view.findViewById(R.id.beep_check);
        seekBar = view.findViewById(R.id.zoom_sb);
        imageLayout = view.findViewById(R.id.card);
        cardLayout = view.findViewById(R.id.cardView_seekbar);
        doneImg = view.findViewById(R.id.done);

        /*barcodeCapture = (BarcodeCapture) getActivity().getSupportFragmentManager().findFragmentById(R.id.scan_view_id);
        if (barcodeCapture != null) {
            barcodeCapture.setRetrieval(QRScanFragment.this);
        }*/
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        cameraMode = prefs.getString("cameraMode","normal");
        cameraSwitch = prefs.getBoolean("camera",Boolean.FALSE);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) view.findViewById(R.id.graphicOverlay);
        mGraphicOverlay.setShowText(isShouldShowText());
        mGraphicOverlay.setRectColors(getRectColors());
        mGraphicOverlay.setDrawRect(isShowDrawRect());
        //barcodeGraphic = new com.moutamid.qr.scanner.generator.Activities.BarcodeGraphic(mGraphicOverlay);
        //BarcodeGraphicTracker tracker = new BarcodeGraphicTracker(mGraphicOverlay,barcodeGraphic,getActivity());

        // read parameters from the intent used to launch the activity.


        historyVM = new ViewModelProvider(getActivity()).get(HistoryVM.class);
        if (cameraPermissionGranted()) {
            createCameraSource(autoFocus,useFlash,cameraSwitch,cameraMode);

        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        //mScannerView.startCamera();
        zoom_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Camera retrieveCamera = barcodeCapture.retrieveCamera();
                    parameters = retrieveCamera.getParameters();
                    if ((zoomProgress + 5) >= parameters.getMaxZoom()) {

                    }
                    zoomProgress = zoomProgress + 5;
                    mCameraSource.doZoom(zoomProgress);
                    seekBar.setProgress(zoomProgress);}catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        zoom_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((zoomProgress - 5) <= 1) {

                }
                //parameters.setZoom(zoomProgress = zoomProgress - 5);
                zoomProgress = zoomProgress - 5;
                mCameraSource.doZoom(zoomProgress);
                seekBar.setProgress(zoomProgress);

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zoomProgress = progress;
                mCameraSource.doZoom(zoomProgress);
                seekBar.setProgress(zoomProgress);
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
        doneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRawResult(textBarcode);
                doneImg.setVisibility(View.GONE);
            }
        });
    }
    public int getBarcodeFormat() {
        return this.barcodeFormat;
    }

    public QRScanFragment setBarcodeFormat(int barcodeFormat) {
        //barcodeFormatUpdate = getBarcodeFormat() != barcodeFormat;
        this.barcodeFormat = barcodeFormat;
        return this;
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
        getActivity().getResources().updateConfiguration(configuration,getActivity().getResources().getDisplayMetrics());
    }


    public boolean isShouldShowText() {
        return this.shouldShowText;
    }

    public QRScanFragment setShouldShowText(boolean shouldShowText) {
        this.shouldShowText = shouldShowText;
        return this;
    }

    public boolean isShowDrawRect() {
        return this.showDrawRect;
    }

    public QRScanFragment setShowDrawRect(boolean showDrawRect) {
        this.showDrawRect = showDrawRect;
        return this;
    }

    public boolean isTouchAsCallback() {
        return this.touchAsCallback;
    }

    public QRScanFragment setTouchAsCallback(boolean touchAsCallback) {
        this.touchAsCallback = touchAsCallback;
        return this;
    }


    public int getCameraFacing() {
        return cameraFacing;
    }

    public QRScanFragment setCameraFacing(int cameraFacing) {
        this.cameraFacing = cameraFacing;
        return this;
    }

    public boolean isAutoFocus() {
        return this.shouldFocus;
    }

    public QRScanFragment shouldAutoFocus(boolean shouldFocus) {
        this.shouldFocus = shouldFocus;
        return this;
    }

    public boolean isShowFlash() {
        return this.showFlash;

    }

    public QRScanFragment setShowFlash(boolean showFlash) {
        this.showFlash = showFlash;
        return this;
    }

    public boolean supportMultipleScan() {
        return this.multipleScan;
    }

    public QRScanFragment setSupportMultipleScan(boolean multipleScan) {
        this.multipleScan = multipleScan;
        return this;
    }

    public Integer[] getRectColors() {
        return this.rectColors;
    }

    public QRScanFragment setRectColors(Integer[] rectColors) {
        this.rectColors = rectColors;
        return this;
    }

    public boolean cameraPermissionGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {

            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (cameraPermissionGranted()) {
               // barcodeCapture.setSupportMultipleScan(false).setShowDrawRect(true).shouldAutoFocus(true);
                createCameraSource(autoFocus, useFlash, cameraSwitch,cameraMode);

            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshCamera() {
        /*barcodeCapture
                .setShowDrawRect(isShowDrawRect())
                .setSupportMultipleScan(supportMultipleScan())
                .setTouchAsCallback(isTouchAsCallback())
                .shouldAutoFocus(isAutoFocus())
                .setShowFlash(isShowFlash())
                .setShowFlash(isShowFlash())
                .setBarcodeFormat(Barcode.ALL_FORMATS)
                .setCameraFacing(cameraFacing)
                .setShouldShowText(isShouldShowText());*/
        barcodeCapture
                .setShowDrawRect(true)
                .setSupportMultipleScan(false)
                .setTouchAsCallback(false)
                .shouldAutoFocus(true)
                .setShowFlash(false)
                .setBarcodeFormat(Barcode.ALL_FORMATS)
                .setCameraFacing(CameraSource.CAMERA_FACING_BACK)
                .setShouldShowText(false);
    }

    private void flashButton() {
        if (isFlash) {
            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            flashon.setImageResource(R.drawable.ic_baseline_flash_off_24);
            isFlash = false;
        } else {
            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            isFlash = true;
            flashon.setImageResource(R.drawable.ic_baseline_flash_on_24);
        }
    }

    private void btnGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 2);
    }

    private void btnMode() {
        mPreview.stop();
        if (checkSoundPreferences()) {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        }
        if (checkVibratePreferences()) {
            if (Build.VERSION.SDK_INT >= 26) {

                Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {

                Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                v.vibrate(100);
            }
        }
        if (cameraMode.equals("normal")) {
            cameraMode = "batch";
            createCameraSource(true,false,false,cameraMode);
            modeTxt.setText(R.string.mode1);
        } else if (cameraMode.equals("batch")){
            cameraMode = "manual";
            createCameraSource(true,false,false,cameraMode);
            modeTxt.setText(R.string.mode2);
        }else {
            cameraMode = "normal";
            createCameraSource(true,false,false,cameraMode);
            modeTxt.setText(R.string.mode3);
        }
    }


    @SuppressLint("WrongConstant")
    private void btnSwitch() {
        mPreview.stop();

        if (cameraSwitch) {
            createCameraSource(true,false,false,cameraMode);
            cameraSwitch = false;
        }else {
            createCameraSource(true,false,true,cameraMode);
            cameraSwitch = true;
        }
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
                        Toast.makeText(context, barcodeFormat.name().toString(), Toast.LENGTH_SHORT).show();
//                        if (barcodeFormat.equals(BarcodeFormat.QR_CODE)) {
//                            processRawResult(result.getText());
//                        } else {
//                            processResultBarcode(result.getText());
//                        }

                        if (barcodeFormat.equals(BarcodeFormat.CODE_128) ||barcodeFormat.equals(BarcodeFormat.EAN_13) || barcodeFormat.equals(BarcodeFormat.EAN_8)|| barcodeFormat.equals(BarcodeFormat.CODE_93)) {
                            processResultBarcode(result.getText(), barcodeFormat.ordinal());
                        } else {
                            processRawResult(result.getText());
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
        super.onDestroy();
   //     mScannerView.stopCamera();
//        mCameraSource.stop();
    }

   /* @Override
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

    }*/
    public void processResultBarcode(String text, int barcodeFormat){
        Intent intent = new Intent(getActivity(), ScanResultActivity.class);
        try {
            History contactHistory = new History(text, "barcode", true);
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcodeFormat", barcodeFormat);
            intent.putExtra("barcode", text);
            //intent.putExtra("image", savedBitmapFromViewToFile());
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
            History contactHistory = new History(vCard.generateString(), "contact", true);
            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "VCard");
            intent.putExtra("vCard", vCard);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        } catch (IllegalArgumentException vcard) {
            try {
                EMail eMail = new EMail();
                eMail.parseSchema(text);
                History contactHistory = new History(eMail.generateString(), "email", true);
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
                    History contactHistory = new History(wifi.generateString(), "wifi", true);
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
                        History contactHistory = new History(telephone.generateString(), "phone", true);
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
                            History contactHistory = new History(url.generateString(), "url", true);
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
                                History urlHistory = new History(social.generateString(), "social", true);
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
                                    History contactHistory = new History(geoInfo.generateString(), "location", true);
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
                                        History contactHistory = new History(sms.generateString(), "sms", true);
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
                                            History contactHistory = new History(iEvent.generateString(), "event", true);
                                            historyVM.insertHistory(contactHistory);
                                            intent.putExtra("type", "Event");
                                            intent.putExtra("event", iEvent);
                                            startActivity(intent);
                                            if (!getPurchaseSharedPreference()) {
                                                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                            }
                                        } catch (IllegalArgumentException event) {
                                            try {
                                               try {
                                                   Log.d("EMAILCHEC" , "TEXT  " + text);
                                                   int i = Integer.parseInt(text);
                                                   History contactHistory = new History(text, "barcode", true);
                                                    historyVM.insertHistory(contactHistory);
                                                    intent.putExtra("type", "Barcode");
                                                    intent.putExtra("barcode", text);
                                                    //intent.putExtra("image", savedBitmapFromViewToFile());
                                                    startActivity(intent);
                                                    if (!getPurchaseSharedPreference()) {
                                                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                                    }
                                               } catch (NumberFormatException e){
                                                   Log.d("EMAILCHEC" , "Error  " + e.toString());
                                                   History contactHistory = new History(text, "text", true);
                                                   historyVM.insertHistory(contactHistory);
                                                   intent.putExtra("type", "Text");
                                                   intent.putExtra("text", text);
                                                   startActivity(intent);
                                                   if (!getPurchaseSharedPreference()) {
                                                       ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                                                   }
                                               }
                                            } catch (Exception txt) {
                                                Toast.makeText(context, "not scan", Toast.LENGTH_SHORT).show();
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
        }

//        try {
//            History contactHistory = new History(text, "barcode");
//            historyVM.insertHistory(contactHistory);
//            intent.putExtra("type", "Barcode");
//            intent.putExtra("barcode", text);
//            //intent.putExtra("image", savedBitmapFromViewToFile());
//            startActivity(intent);
//            if (!getPurchaseSharedPreference()) {
//                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
//            }
//        } catch (IllegalArgumentException t) {
//
//        }
    }

    public static void setFlashLight(Boolean b) {
        //mScannerView.setFlash(b);
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }




    private boolean checkSoundPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean beepSound = prefs.getBoolean("beepsound", true);

        return beepSound;
    }

    //Preferences for Vibrate in App
    private boolean checkVibratePreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean vibrate = prefs.getBoolean("vibrate", true);
        return vibrate;
    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();


        if (cameraPermissionGranted()) {
            startCameraSource();
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }


    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }


    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, boolean b,String mode) {
        Context context = getActivity();

        barcodeDetector =
                new BarcodeDetector.Builder(context)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)//QR_CODE)
                        .build();
        if (b){
            builder = new CameraSource.Builder(getActivity(), barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(15.0f);
        }else {
            builder = new CameraSource.Builder(getActivity(), barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(15.0f);
        }

        // make sure that auto focus is an available option
        builder = builder.setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        modeTxt.setText(R.string.mode3);

        mCameraSource = builder.setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null).build();
         if (mode.equals("batch")) {

             barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                 @Override
                 public void release() {
                     Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                 }

                 @Override
                 public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                     final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                     if (barcodes.size() != 0) {
                         Barcode barcode = barcodes.valueAt(0);
                         String barcodeValue = barcode.rawValue;
                         int format = barcode.format;

                         runOnUiThread(() -> {
                             mPreview.stop();
                             if (checkSoundPreferences()) {
                                 ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                                 toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                             }
                             if (checkVibratePreferences()) {
                                 if (Build.VERSION.SDK_INT >= 26) {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

                                     v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                 } else {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                                     v.vibrate(100);
                                 }
                             }
                             for (int i = 0; i < barcodes.size(); i++){

                                 String rawData = barcodes.valueAt(i).rawValue;
                                 if (format == Barcode.CODE_128 || format == Barcode.EAN_13 || format == Barcode.EAN_8 || format == Barcode.CODE_93) {
                                     processResultBarcode(rawData, format);
                                 } else {
                                     processRawResult(rawData);

                                     //                                 if (barcodeFormat == 32 || barcodeFormat == 64) {
//                                     processResultBarcode(rawData);
//                                 } else {
//                                     processRawResult(rawData);
//                                 }
                                 }
                             }
                         });
                     }
                 }
             });
                modeTxt.setText(R.string.mode1);
            }
         else if (mode.equals("manual")) {
                modeTxt.setText(R.string.mode2);

             barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                 @Override
                 public void release() {
                     Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                 }

                 @Override
                 public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                     final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                     if (barcodes.size() != 0) {
                         runOnUiThread(() -> {
                             mPreview.stop();
                             if (checkSoundPreferences()) {
                                 ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                                 toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                             }
                             if (checkVibratePreferences()) {
                                 if (Build.VERSION.SDK_INT >= 26) {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

                                     v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                 } else {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                                     v.vibrate(100);
                                 }
                             }
                             textBarcode = barcodes.valueAt(0).rawValue;
                             doneImg.setVisibility(View.VISIBLE);
                             //captureImage();
                         });
                     }
                 }
             });
               /* mCameraSource.getCamera().setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<Barcode> sparseArray = barcodeDetector.detect(frame);
                        for (int i = 0; i < sparseArray.size(); i++){

                            String rawData = sparseArray.valueAt(i).rawValue;

                            processResultBarcode(rawData);
                        }
                        barcodeDetector.release();
                    }
                });*/
            }
         else {
             barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                 @Override
                 public void release() {
                     Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                 }

                 @Override
                 public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                     final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                     if (barcodes.size() != 0) {
                         Barcode barcode = barcodes.valueAt(0);
                         String barcodeValue = barcode.rawValue;
                         int format = barcode.format;
                         runOnUiThread(() -> {
                             mPreview.stop();
                             if (checkSoundPreferences()) {
                                 ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                                 toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                             }
                             if (checkVibratePreferences()) {
                                 if (Build.VERSION.SDK_INT >= 26) {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

                                     v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                 } else {

                                     Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                                     v.vibrate(100);
                                 }
                             }
                             String rawData = barcodes.valueAt(0).rawValue;
                             processRawResult(rawData);
//                             Toast.makeText(context, "Barcode " + barcodeFormat, Toast.LENGTH_SHORT).show();
                             if (format == Barcode.CODE_128 || format == Barcode.EAN_13 || format == Barcode.EAN_8 || format == Barcode.CODE_93) {
                                    processResultBarcode(rawData, format);
                             } else {
                                 processRawResult(rawData);

                                 //                                 if (barcodeFormat == 32 || barcodeFormat == 64) {
//                                     processResultBarcode(rawData);
//                                 } else {
//                                     processRawResult(rawData);
//                                 }
                             }
                         });
                     }
                 }
             });
                modeTxt.setText(R.string.mode3);
            }

        startCameraSource();
    }
    Bitmap bitmap = null;
    private void captureImage(){
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data) {
                bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                doneImg.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {

                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

}