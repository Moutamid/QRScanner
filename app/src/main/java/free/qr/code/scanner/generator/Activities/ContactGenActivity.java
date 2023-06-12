package free.qr.code.scanner.generator.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.VCard;

public class ContactGenActivity extends AppCompatActivity {
    private EditText name,phone,email,org,address;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_gen);

        historyVM = new ViewModelProvider(ContactGenActivity.this).get(HistoryVM.class);
        name=findViewById(R.id.contact_name);
        phone=findViewById(R.id.contact_phone);
        email=findViewById(R.id.contact_email);
        org=findViewById(R.id.contact_organization);
        address=findViewById(R.id.contact_address);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ContactGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
    }

    public void contactGenerate(View view) {
        if (name.getText().toString().equals("")) {
            name.setError("Please enter Name");
        }
        else if (phone.getText().toString().equals("")) {
            phone.setError("Please enter Phone");
        }
        else if (email.getText().toString().equals("")) {
            email.setError("Please enter Email");
        }
        else if (org.getText().toString().equals("")) {
            org.setError("Please enter Organization");
        }
        else if (address.getText().toString().equals("")) {
            address.setError("Please enter Address");
        }
        else {
            try {
                final VCard vCard = new VCard(
                        name.getText().toString())
                        .setEmail(email.getText().toString())
                        .setAddress(address.getText().toString())
                        .setCompany(org.getText().toString())
                        .setPhoneNumber(phone.getText().toString());
                History contactHistory = new History(vCard.generateString(), "contact");
                historyVM.insertHistory(contactHistory);


                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        address.setText(null);
                        org.setText(null);
                        email.setText(null);
                        name.setText(null);
                        phone.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, ScanResultActivity.class);
                    intent.putExtra("type", "VCard");
                    intent.putExtra("vCard", vCard);
                    startActivity(intent);
                    if (!getPurchaseSharedPreference()) {
                        ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                    }
                    finish();
                } else {
                    ActivityCompat.requestPermissions(ContactGenActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backContact(View view) {
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