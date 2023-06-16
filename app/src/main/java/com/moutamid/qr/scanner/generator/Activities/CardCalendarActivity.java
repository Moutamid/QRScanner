package com.moutamid.qr.scanner.generator.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.divyanshu.colorseekbar.ColorSeekBar;
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
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.BusinessCard;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CardCalendarActivity extends AppCompatActivity {

    
    private boolean isEventNameSelected = false;
    private boolean isEventDateSelected = false;
    private boolean isEventCitySelected = false;
    private boolean isEventLogoSelected = false;
    TextView eventName,eventDate,eventCity;
    private EditText edittext;
    private RelativeLayout imageLayout,imageLayout1;
    private ImageView logo;
    private ColorSeekBar colorSeekBar;
    private Switch bold,shadow;
    private HistoryVM historyVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_calendar);
        eventCity = findViewById(R.id.event_city);
        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        edittext = findViewById(R.id.edittext);
        imageLayout = findViewById(R.id.image_layout1);
        imageLayout1 = findViewById(R.id.image_layout2);
        eventCity = findViewById(R.id.event_city);
        bold = findViewById(R.id.bold);
        shadow = findViewById(R.id.shadow);
        logo = findViewById(R.id.logo);
        colorSeekBar = findViewById(R.id.color_seek_bar);
        historyVM = new ViewModelProvider(CardCalendarActivity.this).get(HistoryVM.class);
        eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventNameSelected = true;
                isEventDateSelected = false;
                isEventCitySelected = false;
                isEventLogoSelected = false;
                edittext.setText(eventName.getText().toString());
            }
        });
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventDateSelected = true;
                isEventNameSelected = false;
                isEventCitySelected = false;
                isEventLogoSelected = false;
                edittext.setText(eventDate.getText().toString());
            }
        });
        eventCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventCitySelected = true;
                isEventNameSelected = false;
                isEventDateSelected = false;
                isEventLogoSelected = false;
                edittext.setText(eventCity.getText().toString());
            }
        });

        bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (isEventNameSelected){
                        eventName.setTypeface(eventName.getTypeface(), Typeface.BOLD);
                    }else if (isEventDateSelected){
                        eventName.setTypeface(eventDate.getTypeface(), Typeface.BOLD);
                    }else if (isEventCitySelected){
                        eventName.setTypeface(eventCity.getTypeface(), Typeface.BOLD);
                    }
                }else {
                    if (isEventNameSelected){
                        eventName.setTypeface(eventName.getTypeface(), Typeface.NORMAL);
                    }else if (isEventDateSelected){
                        eventName.setTypeface(eventDate.getTypeface(), Typeface.NORMAL);
                    }else if (isEventCitySelected){
                        eventName.setTypeface(eventCity.getTypeface(), Typeface.NORMAL);
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
        
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    if (isEventNameSelected){
                        eventName.setText(charSequence.toString());
                    }else if (isEventDateSelected){
                        eventDate.setText(charSequence.toString());
                    }else if (isEventCitySelected){
                        eventCity.setText(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    public void ClearTxt(View view) {
        edittext.setText("");
    }


    public void SaveTxt(View view) {

        try {
            BusinessCard businessCard = new BusinessCard();
            businessCard.setTitle(eventName.getText().toString());
            businessCard.setContent(eventCity.getText().toString());
            businessCard.setTimestamp(System.currentTimeMillis());
            History urlHistory = new History(businessCard.generateString(), "card");
            historyVM.insertHistory(urlHistory);

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

}