package com.moutamid.qr.scanner.generator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
public class BarCodeActivity extends AppCompatActivity {

    private EditText editText;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

        editText=findViewById(R.id.edit_barcode);
        historyVM = new ViewModelProvider(BarCodeActivity.this).get(HistoryVM.class);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, BarCodeActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
    }

    public void barcodeGenerate(View view) {
        String barcodeText = editText.getText().toString();
        if (barcodeText.equals("")) {
            editText.setError("Please enter text");
        } else {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("type", "Barcode");
            intent.putExtra("barcode", barcodeText);
            History contactHistory = new History(barcodeText, "barcode");
            historyVM.insertHistory(contactHistory);
            startActivity(intent);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
            }
            finish();
        }
    }

    public void backBarcode(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
        }
        finish();

    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }

}