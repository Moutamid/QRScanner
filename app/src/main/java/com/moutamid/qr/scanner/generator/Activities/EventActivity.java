package com.moutamid.qr.scanner.generator.Activities;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    private EditText eventname,subject;
    private  TextView startdate,starttime,enddate,endtime;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventname=findViewById(R.id.event_name);
        subject=findViewById(R.id.event_location);
        startdate=findViewById(R.id.tv_start_date);
        starttime=findViewById(R.id.tv_start_time);
        enddate=findViewById(R.id.tv_end_date);
        endtime=findViewById(R.id.tv_end_time);
        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, EventActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        historyVM = new ViewModelProvider(EventActivity.this).get(HistoryVM.class);

    }

    public void eventGenerate(View view) {
        String data2 = subject.getText().toString();
        String data3 = startdate.getText().toString() + "     " + starttime.getText().toString();
        String data4 = enddate.getText().toString() + "      " + endtime.getText().toString();
        if (eventname.getText().toString().equals("")) {
            eventname.setError("Please enter Name");
        } else if (subject.getText().toString().equals("")) {
            subject.setError("Please enter Subject");
        }

        else {
            try {
                final IEvent iEvent = new IEvent();
                iEvent.setUid(eventname.getText().toString());
                iEvent.setStart(data3);
                iEvent.setEnd(data4);
                iEvent.setStamp(data2);
                History eventHistory = new History(iEvent.generateString(), "event");
                historyVM.insertHistory(eventHistory);

                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", "Event");
                intent.putExtra("event", iEvent);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, this);
                }
                finish();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        starttime.setText(null);
                        startdate.setText(null);
                        endtime.setText(null);
                        enddate.setText(null);
                        subject.setText(null);
                        eventname.setText(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startDate(View view) {


        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, dayOfMonth) -> {

            TextView datePickerValueTextView = findViewById(R.id.tv_start_date);
            String strBuf = year +
                    "-" +
                    (month + 1) +
                    "-" +
                    dayOfMonth;
            datePickerValueTextView.setText(strBuf);
        };

        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(EventActivity.this,R.style.DialogTheme, onDateSetListener, year, month, day);

        // Set dialog icon and title.

        datePickerDialog.setTitle("Please select date.");
        // Popup the dialog.
        datePickerDialog.show();
    }

    public void startTime(View view) {

                TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, hour, minute) -> {

                    TextView timePickerValueTextView = findViewById(R.id.tv_start_time);
                    String strBuf = hour +
                            ":" +
                            minute;
                    timePickerValueTextView.setText(strBuf);
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this,R.style.DialogTheme,
                        onTimeSetListener, hour, minute, false);

                timePickerDialog.setTitle("Please select time.");
                timePickerDialog.show();
    }

    public void endDate(View view) {
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, dayOfMonth) -> {

            TextView datePickerValueTextView = findViewById(R.id.tv_end_date);
            String strBuf = year +
                    "-" +
                    (month + 1) +
                    "-" +
                    dayOfMonth;
            datePickerValueTextView.setText(strBuf);
        };

        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(EventActivity.this,R.style.DialogTheme, onDateSetListener, year, month, day);

        // Set dialog icon and title.

        datePickerDialog.setTitle("Please select date.");
        // Popup the dialog.
        datePickerDialog.show();
    }

    public void endTime(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, hour, minute) -> {

            TextView timePickerValueTextView = findViewById(R.id.tv_end_time);
            String strBuf = hour +
                    ":" +
                    minute;
            timePickerValueTextView.setText(strBuf);
        };

        Calendar now = Calendar.getInstance();
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this,R.style.DialogTheme,
                onTimeSetListener, hour, minute, false);

        timePickerDialog.setTitle("Please select time.");
        timePickerDialog.show();
    }

    public void backEvent(View view) {
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