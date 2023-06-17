package com.moutamid.qr.scanner.generator.Activities;

import static java.io.File.separator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.moutamid.qr.scanner.generator.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import androidmads.library.qrgenearator.BuildConfig;

public class CardGeneratedResult extends AppCompatActivity {

    private ImageView imageView,imageView1;
    private AppCompatButton saveBtn,shareBtn;
    private Bitmap bmp;
    private byte[] imageByte,imageByte1;
    private SharedPreferences prefs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_generated_result);
        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView2);
        saveBtn = findViewById(R.id.save);
        shareBtn = findViewById(R.id.share);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        imageByte = getIntent().getByteArrayExtra("image1");
        imageByte1 = getIntent().getByteArrayExtra("image2");
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(bitmap);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageByte1, 0, imageByte1.length);
        imageView1.setImageBitmap(bitmap1);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContent();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent();
            }
        });
        getLocale();
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

    private void shareContent() {
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), separator + "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/PNG");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveContent() {
        saveToGallery();
    }

    private void saveToGallery() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Photo Lab");
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);
            if (uri != null) {
                try {
                    saveImageToStream(bmp, getContentResolver().openOutputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString()
                    + separator + "Photo Lab");

            //getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bmp, new FileOutputStream(file));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Toast.makeText(this, "QR Save to Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}