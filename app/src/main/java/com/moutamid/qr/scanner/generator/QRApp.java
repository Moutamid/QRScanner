package com.moutamid.qr.scanner.generator;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.multidex.MultiDex;

import com.moutamid.qr.scanner.generator.utils.Stash;
import com.onesignal.OneSignal;

public class QRApp extends Application {
    private static final String ONESIGNAL_APP_ID = "03be6a67-98b3-4cb6-b862-4c654a55619d";

    private LifecycleRegistry lifecycleRegistry;

    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

//        lifecycleRegistry = new LifecycleRegistry((LifecycleOwner) this);
//        lifecycleRegistry.addObserver(new DefaultLifecycleObserver() {
//            @Override
//            public void onResume(@NonNull LifecycleOwner owner) {
//                MapboxNavigationApp.attach(owner);
//            }
//
//            @Override
//            public void onPause(@NonNull LifecycleOwner owner) {
//                MapboxNavigationApp.detach(owner);
//            }
//        });

//        if (!MapboxNavigationApp.isSetup()) {
//            MapboxNavigationApp.setup(new MapboxNavigationApp.SetupCallback() {
//                @Override
//                public void onSetup() {
//                    NavigationOptions.Builder builder = new NavigationOptions.Builder(MyActivity.this);
//                    builder.accessToken("YOUR_ACCESS_TOKEN");
//                    NavigationOptions options = builder.build();
//                }
//            });
//        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
