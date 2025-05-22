package com.moutamid.qr.scanner.generator.Activities;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.Model.ResultModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.Stash;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class QRScanFragment extends Fragment {

    //  private static ZXingScannerView mScannerView;
    public static final String TAG = "QRScanFragment";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    static String contents;
    static Uri picUri;
    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    ImageView flashBtn, modeBtn, galleryBtn;
    TextView qrBar, result, itemCount;
    MaterialCardView batchCard;
    CameraSource.Builder builder;
    // helper objects for detecting taps and pinches.
    BarcodeDetector barcodeDetector;
    Bitmap bitmap = null;
    private Context context;
    private HistoryVM historyVM;
    private int zoomProgress = 0;
    private SharedPreferences prefs;
    private Camera.Parameters parameters;
    private PreviewView previewView;
    private boolean isFlash = false;
    private TextView modeTxt;
    private boolean shouldShowText, multipleScan, showDrawRect, touchAsCallback, shouldFocus, showFlash = false;
    private boolean autoFocus = true;
    private boolean useFlash = false;
    private byte[] imgByte = null;
    private Integer[] rectColors;
    private int barcodeFormat, cameraFacing;

    private String textBarcode = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        assert container != null;
        context = container.getContext();

        View view = inflater.inflate(R.layout.fragment_q_rscan, container, false);
        ArrayList<ResultModel> list = Stash.getArrayList(Constants.RESULT_BATCH, ResultModel.class);
        list.clear();
        Stash.put(Constants.RESULT_BATCH, list);
        checkPermissions();

        batchCard = view.findViewById(R.id.batchCard);
        itemCount = view.findViewById(R.id.itemCount);
        qrBar = view.findViewById(R.id.qrBar);
        result = view.findViewById(R.id.result);

        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            flashBtn = activity.flashBtn;
            modeBtn = activity.modeBtn;
            galleryBtn = activity.galleryBtn;

            modeBtn.setOnClickListener(v -> btnMode());
            galleryBtn.setOnClickListener(v -> btnGallery());
        }

        batchCard.setOnClickListener(v -> {
            startActivity(new Intent(view.getContext(), BatchScanResultActivity.class));
        });
        activity.transparentStatusBar(true);

        return view;
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


//        modeBtn = view.findViewById(R.id.mode_check);
        modeTxt = view.findViewById(R.id.mode_status);

        /*barcodeCapture = (BarcodeCapture) getActivity().getSupportFragmentManager().findFragmentById(R.id.scan_view_id);
        if (barcodeCapture != null) {
            barcodeCapture.setRetrieval(QRScanFragment.this);
        }*/
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        previewView = view.findViewById(R.id.previewView);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }


        //barcodeGraphic = new com.moutamid.qr.scanner.generator.Activities.BarcodeGraphic(mGraphicOverlay);
        //BarcodeGraphicTracker tracker = new BarcodeGraphicTracker(mGraphicOverlay,barcodeGraphic,getActivity());

        // read parameters from the intent used to launch the activity.


        historyVM = new ViewModelProvider(getActivity()).get(HistoryVM.class);

        //mScannerView.startCamera();

    }

    public boolean isShouldShowText() {
        return this.shouldShowText;
    }

    public boolean isShowDrawRect() {
        return this.showDrawRect;
    }

    public Integer[] getRectColors() {
        return this.rectColors;
    }

    public boolean cameraPermissionGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void startCamera() {
        Context context = requireContext(); // safer than getContext(), will throw if null (and help debugging early)
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), imageProxy -> {
                    processImageProxy(scanner, imageProxy);
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void processImageProxy(BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (com.google.mlkit.vision.barcode.common.Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        Log.d("Barcode", "Scanned: " + rawValue);
                        result.setText(rawValue);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> imageProxy.close());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void btnGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 2);
    }

    public void btnMode() {
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
                        // Toast.makeText(context, barcodeFormat.name().toString(), Toast.LENGTH_SHORT).show();
//                        if (barcodeFormat.equals(BarcodeFormat.QR_CODE)) {
//                            processRawResult(result.getText());
//                        } else {
//                            processResultBarcode(result.getText());
//                        }

                        if (barcodeFormat.equals(BarcodeFormat.CODE_128) || barcodeFormat.equals(BarcodeFormat.EAN_13) || barcodeFormat.equals(BarcodeFormat.EAN_8) || barcodeFormat.equals(BarcodeFormat.CODE_93)) {
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
    public void processResultBarcode(String text, int barcodeFormat) {
        Intent intent = new Intent(getActivity(), ScanResultActivity.class);
        try {
            History contactHistory = new History(text, "barcode", true);
            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
            boolean check = false;
            for (History model : historyList) {
                if (model.getData().equals(contactHistory.getData())) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                historyList.add(contactHistory);
            }
            Stash.put(Constants.SCAN, historyList);
//            historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcodeFormat", barcodeFormat);
            intent.putExtra("barcode", text);
            //intent.putExtra("image", savedBitmapFromViewToFile());
            //if (!getPurchaseSharedPreference()) {
            //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            //    }

        } catch (Exception t) {
            Toast.makeText(context, "not scan", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        } finally {
            startActivity(intent);
        }
    }

    public void processRawResult(String text) {

        Intent intent = new Intent(getActivity(), ScanResultActivity.class);

        try {
            VCard vCard = new VCard();
            vCard.parseSchema(text);
            History contactHistory = new History(vCard.generateString(), "contact", true);
            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
            boolean check = false;
            for (History model : historyList) {
                if (model.getData().equals(contactHistory.getData())) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                historyList.add(contactHistory);
            }
            Stash.put(Constants.SCAN, historyList);
            //    historyVM.insertHistory(contactHistory);
            intent.putExtra("type", "VCard");
            intent.putExtra("vCard", vCard);
            //if (!getPurchaseSharedPreference()) {
            //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            //    }
        } catch (IllegalArgumentException vcard) {
            try {
                EMail eMail = new EMail();
                eMail.parseSchema(text);
                History contactHistory = new History(eMail.generateString(), "email", true);
                ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);

                boolean check = false;
                for (History model : historyList) {
                    if (model.getData().equals(contactHistory.getData())) {
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    historyList.add(contactHistory);
                }

                Stash.put(Constants.SCAN, historyList);
//                historyVM.insertHistory(contactHistory);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);

                //if (!getPurchaseSharedPreference()) {
                //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                //    }
            } catch (IllegalArgumentException email) {
                try {
                    Wifi wifi = new Wifi();
                    wifi.parseSchema(text);
                    History contactHistory = new History(wifi.generateString(), "wifi", true);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                    boolean check = false;
                    for (History model : historyList) {
                        if (model.getData().equals(contactHistory.getData())) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        historyList.add(contactHistory);
                    }
                    Stash.put(Constants.SCAN, historyList);
//                    historyVM.insertHistory(contactHistory);
                    intent.putExtra("type", "wifi");
                    intent.putExtra("Wifi", wifi);

                    //if (!getPurchaseSharedPreference()) {
                    //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                    //    }
                } catch (IllegalArgumentException wifi) {
                    try {
                        Telephone telephone = new Telephone();
                        telephone.parseSchema(text);
                        History contactHistory = new History(telephone.generateString(), "phone", true);
                        ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                        boolean check = false;
                        for (History model : historyList) {
                            if (model.getData().equals(contactHistory.getData())) {
                                check = true;
                                break;
                            }
                        }
                        if (!check) {
                            historyList.add(contactHistory);
                        }
                        Stash.put(Constants.SCAN, historyList);
//                        historyVM.insertHistory(contactHistory);
                        intent.putExtra("type", "telephone");
                        intent.putExtra("phone", telephone);

                        //if (!getPurchaseSharedPreference()) {
                        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                        //    }
                    } catch (IllegalArgumentException telephone) {
                        try {
                            Url url = new Url();
                            url.parseSchema(text);
                            History contactHistory = new History(url.generateString(), "url", true);
                            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                            boolean check = false;
                            for (History model : historyList) {
                                if (model.getData().equals(contactHistory.getData())) {
                                    check = true;
                                    break;
                                }
                            }
                            if (!check) {
                                historyList.add(contactHistory);
                            }
                            Stash.put(Constants.SCAN, historyList);
//                            historyVM.insertHistory(contactHistory);
                            intent.putExtra("type", "url");
                            intent.putExtra("Url", url);

                            if (!getPurchaseSharedPreference()) {

                                //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                            }
                        } catch (IllegalArgumentException url) {
                            try {
                                final Social social = new Social();
                                social.parseSchema(text);
                                History urlHistory = new History(social.generateString(), "social", true);
                                ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                boolean check = false;
                                for (History model : historyList) {
                                    if (model.getData().equals(urlHistory.getData())) {
                                        check = true;
                                        break;
                                    }
                                }
                                if (!check) {
                                    historyList.add(urlHistory);
                                }
                                Stash.put(Constants.SCAN, historyList);
//                                historyVM.insertHistory(urlHistory);
                                intent.putExtra("type", "Social");
                                intent.putExtra("social", social);

                                if (!getPurchaseSharedPreference()) {

                                    //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                                }
                            } catch (IllegalArgumentException youtube) {
                                try {
                                    GeoInfo geoInfo = new GeoInfo();
                                    geoInfo.parseSchema(text);
                                    History contactHistory = new History(geoInfo.generateString(), "location", true);
                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                    boolean check = false;
                                    for (History model : historyList) {
                                        if (model.getData().equals(contactHistory.getData())) {
                                            check = true;
                                            break;
                                        }
                                    }
                                    if (!check) {
                                        historyList.add(contactHistory);
                                    }
                                    Stash.put(Constants.SCAN, historyList);
//                                    historyVM.insertHistory(contactHistory);
                                    intent.putExtra("type", "GeoInfo");
                                    intent.putExtra("geoInfo", geoInfo);

                                    if (!getPurchaseSharedPreference()) {

                                        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                                    }
                                } catch (IllegalArgumentException geoinfo) {
                                    try {
                                        SMS sms = new SMS();
                                        sms.parseSchema(text);
                                        History contactHistory = new History(sms.generateString(), "sms", true);
                                        ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                        boolean check = false;
                                        for (History model : historyList) {
                                            if (model.getData().equals(contactHistory.getData())) {
                                                check = true;
                                                break;
                                            }
                                        }
                                        if (!check) {
                                            historyList.add(contactHistory);
                                        }
                                        Stash.put(Constants.SCAN, historyList);
//                                        historyVM.insertHistory(contactHistory);
                                        intent.putExtra("type", "Sms");
                                        intent.putExtra("sms", sms);

                                        if (!getPurchaseSharedPreference()) {

                                            //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                                        }
                                    } catch (IllegalArgumentException sms) {
                                        try {
                                            IEvent iEvent = new IEvent();
                                            iEvent.parseSchema(text);
                                            History contactHistory = new History(iEvent.generateString(), "event", true);
                                            ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                            boolean check = false;
                                            for (History model : historyList) {
                                                if (model.getData().equals(contactHistory.getData())) {
                                                    check = true;
                                                    break;
                                                }
                                            }
                                            if (!check) {
                                                historyList.add(contactHistory);
                                            }
                                            Stash.put(Constants.SCAN, historyList);
//                                            historyVM.insertHistory(contactHistory);
                                            intent.putExtra("type", "Event");
                                            intent.putExtra("event", iEvent);

                                            if (!getPurchaseSharedPreference()) {

                                                //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                                            }
                                        } catch (IllegalArgumentException event) {
                                            try {
                                                try {
                                                    Log.d("EMAILCHEC", "TEXT  " + text);
                                                    int i = Integer.parseInt(text);
                                                    History contactHistory = new History(text, "barcode", true);
                                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                                    boolean check = false;
                                                    for (History model : historyList) {
                                                        if (model.getData().equals(contactHistory.getData())) {
                                                            check = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!check) {
                                                        historyList.add(contactHistory);
                                                    }
                                                    Stash.put(Constants.SCAN, historyList);
//                                                    historyVM.insertHistory(contactHistory);
                                                    intent.putExtra("type", "Barcode");
                                                    intent.putExtra("barcode", text);
                                                    //intent.putExtra("image", savedBitmapFromViewToFile());
                                                    if (!getPurchaseSharedPreference()) {

                                                        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

                                                    }
                                                } catch (NumberFormatException e) {
                                                    Log.d("EMAILCHEC", "Error  " + e.toString());
                                                    History contactHistory = new History(text, "text", true);
                                                    ArrayList<History> historyList = Stash.getArrayList(Constants.SCAN, History.class);
                                                    boolean check = false;
                                                    for (History model : historyList) {
                                                        if (model.getData().equals(contactHistory.getData())) {
                                                            check = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!check) {
                                                        historyList.add(contactHistory);
                                                    }
                                                    Stash.put(Constants.SCAN, historyList);
//                                                   historyVM.insertHistory(contactHistory);
                                                    intent.putExtra("type", "Text");
                                                    intent.putExtra("text", text);
                                                    if (!getPurchaseSharedPreference()) {

                                                        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

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
//                
        //          //ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);

//            }
//        } catch (IllegalArgumentException t) {
//
//        }
    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }

    private boolean checkSoundPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean beepSound = prefs.getBoolean("beepsound", Boolean.TRUE);

        return beepSound;
    }

    //Preferences for Vibrate in App
    private boolean checkVibratePreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean vibrate = prefs.getBoolean("vibrate", Boolean.TRUE);
        return vibrate;
    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                    (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES);
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            if (
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();

    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, boolean b, String mode) {
        Context context = getActivity();
        final boolean[] processingBarcode = {true};
        barcodeDetector =
                new BarcodeDetector.Builder(context)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)//QR_CODE)
                        .build();
        if (b) {
            builder = new CameraSource.Builder(getActivity(), barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(15.0f);
        } else {
            builder = new CameraSource.Builder(getActivity(), barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(15.0f);
        }

        // make sure that auto focus is an available option
        builder = builder.setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        modeTxt.setText(R.string.mode3);


        if (mode.equals("batch")) {
            Log.d("CAMRA", "Batch");
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                    Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (processingBarcode[0]) {
                        if (barcodes.size() > 0) {

/*                            runOnUiThread(() -> {
                               // mPreview.stop();
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
                                for (int i = 0; i < barcodes.size(); i++) {
                                    String rawData = barcodes.valueAt(i).rawValue;
                                    if (format == Barcode.CODE_128 || format == Barcode.EAN_13 || format == Barcode.EAN_8 || format == Barcode.CODE_93) {
                                        processResultBarcode(rawData, format);
                                    } else {
                                        processRawResult(rawData);
                                    }
                                }
                            });
                            processingBarcode[0] = false;*/
                        }
                    }
                }
            });
            modeTxt.setText(R.string.mode1);
        } else {
            Log.d("CAMRA", "NORMAL ELSE");
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                    Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (processingBarcode[0]) {
                        if (barcodes.size() > 0) {
                            Log.d("CAMRA", "receiveDetections");
                            Barcode barcode = barcodes.valueAt(0);
                            int format = barcode.format;
                            Log.d("CAMRA", "format " + format);
                            processingBarcode[0] = false;
                        }
                    }
                }
            });
            modeTxt.setText(R.string.mode3);
        }

        startCameraSource();

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGallery();
            }
        });
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMode();
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


    }

}