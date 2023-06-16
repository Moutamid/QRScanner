package com.moutamid.qr.scanner.generator.Activities;


import android.content.Context;


import com.google.android.gms.samples.vision.barcodereader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic> mGraphicOverlay;
    private Context mContext;


    public BarcodeTrackerFactory(GraphicOverlay<com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic> mGraphicOverlay,
                                 Context context) {
        this.mGraphicOverlay = mGraphicOverlay;
        this.mContext = mContext;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic, mContext);
    }

}