package com.moutamid.qr.scanner.generator;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
    public static final String SCAN = "scan";
    public static final String CREATE = "create";
    public static final String CARD = "card";
    public static final String RESULT_BATCH = "RESULT_BATCH";

    public static boolean getPurchaseSharedPreference(Context context){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.adsubscribed), false);
    }

}
