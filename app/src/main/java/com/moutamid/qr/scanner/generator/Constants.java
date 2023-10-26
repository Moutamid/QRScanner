package com.moutamid.qr.scanner.generator;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Constants {
    public static final String SCAN = "scan";
    public static final String CREATE = "create";
    public static final String CARD = "card";
    public static final String RESULT_BATCH = "RESULT_BATCH";
    private static final String TAG = "Constants";

    public static boolean getPurchaseSharedPreference(Context context){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.adsubscribed), false);
    }

    public static void adjustFontScale(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.fontScale > 1.00) {
            Log.d(TAG, "fontScale=" + configuration.fontScale); //Custom Log class, you can use Log.w
            Log.d(TAG, "font too big. scale down..."); //Custom Log class, you can use Log.w
            configuration.fontScale = 1.00f;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }

}
