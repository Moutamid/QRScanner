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
import free.qr.code.scanner.generator.utils.formates.EMail;
public class EmailGenActivity extends AppCompatActivity {
    private EditText email,subject,body;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_gen);

        email=findViewById(R.id.email);
        body=findViewById(R.id.email_body);
        subject=findViewById(R.id.email_subject);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, EmailGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(EmailGenActivity.this).get(HistoryVM.class);
    }

    public void emailGenerate(View view) {
        if (email.getText().toString().equals("")) {
            email.setError("Please enter Email");
        } else if (subject.getText().toString().equals("")) {
            subject.setError("Please enter Subject");
        } else if (body.getText().toString().equals("")) {
            body.setError("Please enter Body");
        } else {
            try {
                final EMail eMail = new EMail();
                eMail.setEmail(email.getText().toString());
                eMail.setMailBody(body.getText().toString());
                eMail.setMailSubject(subject.getText().toString());
                History emailHistory = new History(eMail.generateString(), "email");
                historyVM.insertHistory(emailHistory);
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        email.setText(null);
                        body.setText(null);
                        subject.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(EmailGenActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void backEmail(View view) {
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
