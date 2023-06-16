package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import java.util.ArrayList;
import java.util.List;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class QRScanFragment extends Fragment implements BarcodeGraphicTracker.BarcodeUpdateListener {

  //  private static ZXingScannerView mScannerView;
    private Context context;
    private int cameraId = CameraSource.CAMERA_FACING_BACK;
    private HistoryVM historyVM;

    private int zoomProgress = 5;
    static String contents;
    static Uri picUri;

    private boolean barcodeFormatUpdate = false, pause = false;
    private Detector<Barcode> customBarcodeDetector;
    private BarcodeDetector barcodeDetector;
    private Camera.Parameters parameters;
    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private BarcodeCapture barcodeCapture;
    private boolean isFlash = false;
    private static final int RC_HANDLE_GMS = 9001;
    //private final ArrayList<ButtonMainModel> mainDataList = new ArrayList<>();
    private ImageView flashon,zoom_minus,zoom_plus,switchBtn, galleryBtn,modeBtn;
    private SeekBar seekBar;
    private boolean shouldShowText, multipleScan, showDrawRect, touchAsCallback, shouldFocus, showFlash = false;

    private boolean autoFocus = false;
    private boolean useFlash = false;

    private Integer[] rectColors;

    private int barcodeFormat, cameraFacing;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        assert container != null;
        context = container.getContext();
       // mScannerView = new ZXingScannerView(getActivity());
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
        switchBtn = view.findViewById(R.id.camera_switch);
        galleryBtn = view.findViewById(R.id.beep_check);
        seekBar = view.findViewById(R.id.zoom_sb);
        /*barcodeCapture = (BarcodeCapture) getActivity().getSupportFragmentManager().findFragmentById(R.id.scan_view_id);
        if (barcodeCapture != null) {
            barcodeCapture.setRetrieval(QRScanFragment.this);
        }*/
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) view.findViewById(R.id.graphicOverlay);
        mGraphicOverlay.setShowText(isShouldShowText());
        mGraphicOverlay.setRectColors(getRectColors());
        mGraphicOverlay.setDrawRect(isShowDrawRect());

        // read parameters from the intent used to launch the activity.


        gestureDetector = new GestureDetector(getContext(), new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                boolean b = scaleGestureDetector.onTouchEvent(e);

                boolean c = gestureDetector.onTouchEvent(e);
                return b || c || view.onTouchEvent(e);
            }
        });
        historyVM = new ViewModelProvider(getActivity()).get(HistoryVM.class);
        if (cameraPermissionGranted()) {
            createCameraSource(autoFocus,useFlash,cameraId);

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
                /*if (barcodeCapture != null) {
                    zoomProgress = progress;
                    Camera retrieveCamera = barcodeCapture.retrieveCamera();
                    if (retrieveCamera != null) {
                        try {
                            parameters = retrieveCamera.getParameters();
                            int maxZoom = parameters.getMaxZoom();
                            seekBar.setMax(maxZoom);
                            if (parameters.isZoomSupported() && zoomProgress >= 0 && zoomProgress < maxZoom) {
                                parameters.setZoom(zoomProgress);
                            }
                            retrieveCamera.setParameters(parameters);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }*/
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
    }
    public int getBarcodeFormat() {
        return this.barcodeFormat;
    }

    public QRScanFragment setBarcodeFormat(int barcodeFormat) {
        //barcodeFormatUpdate = getBarcodeFormat() != barcodeFormat;
        this.barcodeFormat = barcodeFormat;
        return this;
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
                createCameraSource(autoFocus, useFlash, cameraId);

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
    }


    private boolean isSwitch = false;
    @SuppressLint("WrongConstant")
    private void btnSwitch() {

        if (isSwitch) {
            mCameraSource.setCameraFacing(CameraSource.CAMERA_FACING_BACK);
            isSwitch = false;
        }else {

            mCameraSource.setCameraFacing(CameraSource.CAMERA_FACING_FRONT);
            isSwitch = true;
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
   //     mScannerView.stopCamera();
        mCameraSource.stop();
        super.onDestroy();
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
        //mScannerView.setFlash(b);
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }



    protected BarcodeRetriever barcodeRetriever;

    public void setRetrieval(BarcodeRetriever retrieval) {
        barcodeRetriever = retrieval;
    }

    protected QRScanFragment setBarcodeFormatUpdate(boolean barcodeFormatUpdate) {
        this.barcodeFormatUpdate = barcodeFormatUpdate;
        return this;
    }

    public boolean isBarcodeFormatUpdate() {
        return barcodeFormatUpdate;
    }

    public void stopScanning() {
    }

    public QRScanFragment setCustomDetector(Detector<Barcode> customDetector) {
        this.customBarcodeDetector = customDetector;
        return this;
    }


    public Detector<Barcode> getCustomBarcodeDetector() {
        if (barcodeDetector == null)
            barcodeDetector = new BarcodeDetector.Builder(getContext())
                    .setBarcodeFormats(getBarcodeFormat())
                    .build();
        return customBarcodeDetector == null ? barcodeDetector : customBarcodeDetector;
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


    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, int cameraId) {
        Context context = getActivity();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, context);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
          //  IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
           /* boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
              //  Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w("TAG", getString(R.string.low_storage_error));
            }*/
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getActivity(), barcodeDetector)
                .setFacing(cameraId)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
        startCameraSource();
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

    public void refresh() {
        mGraphicOverlay.setDrawRect(isShowDrawRect());
        mGraphicOverlay.setRectColors(getRectColors());
        mGraphicOverlay.setShowText(isShouldShowText());
        mCameraSource.setFocusMode(isAutoFocus() ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        mCameraSource.setFlashMode(
                isShowFlash() ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
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

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellationt
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */


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

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        ArrayList<Barcode> allRetrieved = new ArrayList<>();
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            allRetrieved.add(barcode);
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy); // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            if (barcodeRetriever != null)
                if (supportMultipleScan()) {
                    barcodeRetriever.onRetrievedMultiple(best, mGraphicOverlay.getGraphics());
                } else {
                    barcodeRetriever.onRetrieved(best);
                }
            return true;
        }
        return false;
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
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
            String rawData = barcode.rawValue;
            int i = barcode.format;
            processResultBarcode(rawData);
        });
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}