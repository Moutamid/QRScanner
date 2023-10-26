package com.moutamid.qr.scanner.generator.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
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
import com.fxn.stash.Stash;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.BusinessCard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class CardSocialActivity extends AppCompatActivity {

    private boolean isText1Selected = false;
    private boolean isText2Selected = false;
    private boolean isEventLogoSelected = false;
    EditText text1,text2;
    private CardView imageLayout,imageLayout1;
    private ImageView logo;
    private ColorSeekBar colorSeekBar;
    private SwitchCompat bold, shadow;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_card_social);
        text1 = findViewById(R.id.username);
        text2 = findViewById(R.id.url);
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

        text1.requestFocus();

        text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text1.clearFocus();
            }
        });

        text2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text2.clearFocus();
            }
        });


        imageLayout = findViewById(R.id.image_layout1);
        imageLayout1 = findViewById(R.id.image_layout2);
        //    text3 = findViewById(R.id.event_city);
        bold = findViewById(R.id.bold);
        shadow = findViewById(R.id.shadow);
        logo = findViewById(R.id.logo);
        colorSeekBar = findViewById(R.id.color_seek_bar);
        historyVM = new ViewModelProvider(CardSocialActivity.this).get(HistoryVM.class);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isText1Selected = true;
                isText2Selected = false;
                isEventLogoSelected = false;
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isText1Selected = false;
                isText2Selected = true;
                isEventLogoSelected = false;
            }
        });

        bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) { if (isText1Selected) {
                    text1.setTypeface(text1.getTypeface(), Typeface.BOLD);
                } else if (isText2Selected) {
                    text2.setTypeface(text1.getTypeface(), Typeface.BOLD);
                }
                } else {
                    if (isText1Selected) {
                        text1.setTypeface(text1.getTypeface(), Typeface.NORMAL);
                    } else if (isText2Selected) {
                        text2.setTypeface(text2.getTypeface(), Typeface.NORMAL);
                    }
                }
            }
        });
        shadow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (isText1Selected) {
                        text1.setElevation(20f);
                    } else if (isText2Selected) {
                        text2.setElevation(20f);
                    }
                }
            }
        });


        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                if (isText1Selected) {
                    text1.setTextColor(i);
                } else if (isText2Selected) {
                    text2.setTextColor(i);
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
        getLocale();
    }



    private void getLocale(){

        String lang = prefs.getString("lang","");
        String name = prefs.getString("lang_name","");
        //   languageTxt.setText(name);
        setLocale(lang,name);
    }

    private void setLocale(String lng,String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());

    }

    public void SaveTxt(View view) {

        try {
            if (history) {
                BusinessCard businessCard = new BusinessCard();
                businessCard.setTitle(text1.getText().toString());
                businessCard.setContent(text2.getText().toString());
                businessCard.setTimestamp(System.currentTimeMillis());
                History urlHistory = new History(businessCard.generateString(), "card", false);
                ArrayList<History> historyList = Stash.getArrayList(Constants.CARD, History.class);
                historyList.add(urlHistory);
                Stash.put(Constants.CARD, historyList);
            }
            text1.setBackgroundResource(0);
            text2.setBackgroundResource(0);
            Intent intent = new Intent(getApplicationContext(), CardGeneratedResult.class);
            intent.putExtra("image1", savedBitmapFromViewToFile());
            intent.putExtra("image2", savedBitmapFromViewToFile2());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    private byte[] savedBitmapFromViewToFile() {
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
            Toast.makeText(getApplicationContext(), "You have not picked any Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        exitActivity();
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

    public void backPress(View view) {
        onBackPressed();
    }
}