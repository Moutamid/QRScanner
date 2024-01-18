package com.moutamid.qr.scanner.generator.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Locale;

public class ContactGenActivity extends AppCompatActivity {
    private TextInputLayout name, phone, email, org, address;
    private HistoryVM historyVM;
    private SharedPreferences prefs;
    private boolean history;
    VCard passed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        setContentView(R.layout.activity_contact_gen);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = prefs.getBoolean("theme", false);
        history = prefs.getBoolean("saveHistory", true);
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

        historyVM = new ViewModelProvider(ContactGenActivity.this).get(HistoryVM.class);
        name = findViewById(R.id.contact_name);
        phone = findViewById(R.id.contact_phone);
        email = findViewById(R.id.contact_email);
        org = findViewById(R.id.contact_organization);
        address = findViewById(R.id.contact_address);

        passed = (VCard) getIntent().getSerializableExtra(Constants.passed);

        if (passed != null){
            name.getEditText().setText(passed.getName());
            phone.getEditText().setText(passed.getPhoneNumber());
            email.getEditText().setText(passed.getEmail());
            org.getEditText().setText(passed.getCompany());
            address.getEditText().setText(passed.getAddress());
        }

        CAMediatedBannerView mediatedBannerView = findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, ContactGenActivity.this, mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        getLocale();

        phone.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ContactGenActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                    }
                    ActivityCompat.requestPermissions(ContactGenActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 2);
                } else {
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContact, 1);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, 1);
            } else {
                Toast.makeText(this, "Permission is required to get the Email, Organization and Address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int emailIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

                if (emailIndex >= 0) {
                    String contactId = c.getString(emailIndex);
                    Cursor emailCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );

                    if (emailCursor != null && emailCursor.moveToFirst()) {
                        int i = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                        if (i >= 0) {
                            String email = emailCursor.getString(i);
                            this.email.getEditText().setText(email);
                        }
                    }

                    Cursor organizationCursor = getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
                            null
                    );

                    if (organizationCursor != null && organizationCursor.moveToFirst()) {
                        int o = organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY);
                        if (o >= 0) {
                            String organizationName = organizationCursor.getString(o);
                            this.org.getEditText().setText(organizationName);
                        }
                        Cursor addressCursor = getContentResolver().query(
                                ContactsContract.Data.CONTENT_URI,
                                null,
                                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                                new String[]{contactId, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                                null
                        );

                        String organizationAddress = "";
                        if (addressCursor != null && addressCursor.moveToFirst()) {
                            int a = addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
                            if (a >= 0) {
                                organizationAddress = addressCursor.getString(a);
                                this.address.getEditText().setText(organizationAddress);
                            }
                            addressCursor.close();
                        }
                        organizationCursor.close();
                    }

                    emailCursor.close();
                }

                String num = c.getString(phoneIndex);
                String name = c.getString(nameIndex);
                phone.getEditText().setText(num);
                this.name.getEditText().setText(name);
            }
        }
    }

    private void getLocale() {

        String lang = prefs.getString("lang", "");
        String name = prefs.getString("lang_name", "");
        //   languageTxt.setText(name);
        setLocale(lang, name);
    }

    private void setLocale(String lng, String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    public void contactGenerate(View view) {
        if (name.getEditText().getText().toString().equals("")) {
            name.getEditText().setError("Please enter Name");
        } else if (phone.getEditText().getText().toString().equals("")) {
            phone.getEditText().setError("Please enter Phone");
        } else if (email.getEditText().getText().toString().equals("")) {
            email.getEditText().setError("Please enter Email");
        } else if (org.getEditText().getText().toString().equals("")) {
            org.getEditText().setError("Please enter Organization");
        } else if (address.getEditText().getText().toString().equals("")) {
            address.getEditText().setError("Please enter Address");
        } else {
            try {
                final VCard vCard = new VCard(
                        name.getEditText().getText().toString())
                        .setEmail(email.getEditText().getText().toString())
                        .setAddress(address.getEditText().getText().toString())
                        .setCompany(org.getEditText().getText().toString())
                        .setPhoneNumber(phone.getEditText().getText().toString());
                if (history) {
                    History contactHistory = new History(vCard.generateString(), "contact", false);
                    ArrayList<History> historyList = Stash.getArrayList(Constants.CREATE, History.class);
                    if (passed != null) {
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getData().equals(passed.generateString())){
                                historyList.set(i, contactHistory);
                            }
                        }
                    } else
                        historyList.add(contactHistory);
                    Stash.put(Constants.CREATE, historyList);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        address.getEditText().setText(null);
                        org.getEditText().setText(null);
                        email.getEditText().setText(null);
                        name.getEditText().setText(null);
                        phone.getEditText().setText(null);
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

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}