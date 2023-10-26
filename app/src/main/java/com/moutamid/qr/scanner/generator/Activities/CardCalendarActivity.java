package com.moutamid.qr.scanner.generator.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.BusinessCard;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CardCalendarActivity extends AppCompatActivity {

    
    private boolean isEventNameSelected = false;
    private boolean isEventDateSelected = false;
    private boolean isEventCitySelected = false;
    private boolean isEventLogoSelected = false;
    TextView eventDate;
    EditText eventName,eventCity;
    private SharedPreferences prefs;
    private CardView imageLayout,imageLayout1;
    private ImageView logo;
    private ColorSeekBar colorSeekBar;
    private SwitchCompat bold,shadow;
    private HistoryVM historyVM;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_card_calendar);
        eventCity = findViewById(R.id.event_city);
        eventName = findViewById(R.id.event_name);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = prefs.getBoolean("theme",false);
        history = prefs.getBoolean("saveHistory",true);
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
        eventDate = findViewById(R.id.event_date);
        imageLayout = findViewById(R.id.image_layout1);
        imageLayout1 = findViewById(R.id.image_layout2);
        eventCity = findViewById(R.id.event_city);
        bold = findViewById(R.id.bold);
        shadow = findViewById(R.id.shadow);
        logo = findViewById(R.id.logo);
        getLocale();
        colorSeekBar = findViewById(R.id.color_seek_bar);
        historyVM = new ViewModelProvider(CardCalendarActivity.this).get(HistoryVM.class);

        eventName.requestFocus();
        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                eventName.clearFocus();
            }
        });

        eventCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                eventCity.clearFocus();
            }
        });

        eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventNameSelected = true;
                isEventDateSelected = false;
                isEventCitySelected = false;
                isEventLogoSelected = false;

                if (eventName.getTypeface().getStyle() == Typeface.BOLD){
                    bold.setChecked(true);
                } else {
                    bold.setChecked(false);
                }

            }
        });
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventDateSelected = true;
                isEventNameSelected = false;
                isEventCitySelected = false;
                isEventLogoSelected = false;

                DatePickerdialog();

                if (eventDate.getTypeface().getStyle() == Typeface.BOLD){
                    bold.setChecked(true);
                } else {
                    bold.setChecked(false);
                }

            }
        });
        eventCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventCitySelected = true;
                isEventNameSelected = false;
                isEventDateSelected = false;
                isEventLogoSelected = false;

                if (eventCity.getTypeface().getStyle() == Typeface.BOLD){
                    bold.setChecked(true);
                } else {
                    bold.setChecked(false);
                }

            }
        });

        bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (isEventNameSelected){
                        eventName.setTypeface(eventName.getTypeface(), Typeface.BOLD);
                    }else if (isEventDateSelected){
                        eventDate.setTypeface(eventDate.getTypeface(), Typeface.BOLD);
                    }else if (isEventCitySelected){
                        eventCity.setTypeface(eventCity.getTypeface(), Typeface.BOLD);
                    }
                } else {
                    if (isEventNameSelected){
                        eventName.setTypeface(eventName.getTypeface(), Typeface.NORMAL);
                    }else if (isEventDateSelected){
                        eventDate.setTypeface(eventDate.getTypeface(), Typeface.NORMAL);
                    }else if (isEventCitySelected){
                        eventCity.setTypeface(eventCity.getTypeface(), Typeface.NORMAL);
                    }
                }
            }
        });
        shadow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (isEventNameSelected){
                        eventName.setElevation(20f);
                    }else if (isEventDateSelected){
                        eventDate.setElevation(20f);
                    }else if (isEventCitySelected){
                        eventCity.setElevation(20f);
                    }
                }
            }
        });

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                if (isEventNameSelected){
                    eventName.setTextColor(i);
                }else if (isEventDateSelected){
                    eventDate.setTextColor(i);
                }else if (isEventCitySelected){
                    eventCity.setTextColor(i);
                }
            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventLogoSelected = true;
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 2);
            }
        });
    }

    private void DatePickerdialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            // Retrieving the selected start and end dates
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Formating the selected dates as strings
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            // Creating the date range string
            String selectedDateRange = startDateString + "  To  " + endDateString;

            // Displaying the selected date range in the TextView
            eventDate.setText(selectedDateRange);
        });

        // Showing the date picker dialog
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
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
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }

    public void ClearTxt(View view) {

    }


    public void SaveTxt(View view) {

        try {
            BusinessCard businessCard = new BusinessCard();
            businessCard.setTitle(eventName.getText().toString());
            businessCard.setContent(eventCity.getText().toString());
            businessCard.setTimestamp(System.currentTimeMillis());
            History urlHistory = new History(businessCard.generateString(), "card", false);
            historyVM.insertHistory(urlHistory);
            eventName.setBackgroundResource(0);
            eventCity.setBackgroundResource(0);
            eventDate.setBackgroundResource(0);
            Intent intent = new Intent(getApplicationContext(), CardGeneratedResult.class);
            intent.putExtra("image1", savedBitmapFromViewToFile());
            intent.putExtra("image2", savedBitmapFromViewToFile2());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] savedBitmapFromViewToFile(){
        //inflate layout

        //reference View with image
        /*imageLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(imageLayout.getMeasuredWidth(), imageLayout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageLayout.layout(0, 0, imageLayout.getMeasuredWidth(), imageLayout.getMeasuredHeight());
        imageLayout.draw(canvas);*/

        Bitmap bitmap = Bitmap.createBitmap(imageLayout.getWidth(), imageLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        imageLayout.draw(c);
        //save to File
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String nameFile = "calender.jpg";
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + nameFile);
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            Log.d("Error File:"  , "" + e);
        }
        return bytes.toByteArray();
    }
    private byte[] savedBitmapFromViewToFile2() {
        //inflate layout

        Bitmap bitmap = Bitmap.createBitmap(imageLayout1.getWidth(), imageLayout1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        imageLayout1.draw(c);
        //save to File
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String nameFile = "calender.jpg";
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + nameFile);
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            Log.d("Error File:", "" + e);
        }
        return bytes.toByteArray();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                assert data != null;
                Uri picUri = data.getData();
                logo.setImageURI(picUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(CardCalendarActivity.this, "You have not picked any Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    public void backPress(View view){
        onBackPressed();
    }

    private void exitActivity(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle(R.string.dialog_title);
        // Icon Of Alert Dialog
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage(R.string.dialog_message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}