package free.qr.code.scanner.generator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.interfaces.ZoomChangeListener;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.EMail;
import free.qr.code.scanner.generator.utils.formates.GeoInfo;
import free.qr.code.scanner.generator.utils.formates.IEvent;
import free.qr.code.scanner.generator.utils.formates.SMS;
import free.qr.code.scanner.generator.utils.formates.Social;
import free.qr.code.scanner.generator.utils.formates.Telephone;
import free.qr.code.scanner.generator.utils.formates.Url;
import free.qr.code.scanner.generator.utils.formates.VCard;
import free.qr.code.scanner.generator.utils.formates.Wifi;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.content.Context.VIBRATOR_SERVICE;

public class QRScanFragment extends Fragment implements ZXingScannerView.ResultHandler, ZoomChangeListener {

    private static ZXingScannerView mScannerView;
    private Context context;
    private HistoryVM historyVM;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        assert container != null;
        context = container.getContext();
        mScannerView = new ZXingScannerView(getActivity());
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().LoadInterstitial();
        }
        ((MainActivity)getActivity()).setOnZoomChangeListener(this);
        return inflater.inflate(R.layout.fragment_q_rscan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       mScannerView = view.findViewById(R.id.scanner_view);
        historyVM = new ViewModelProvider(getActivity()).get(HistoryVM.class);
        mScannerView.startCamera();
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

    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}