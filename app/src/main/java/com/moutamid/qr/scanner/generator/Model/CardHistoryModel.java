package com.moutamid.qr.scanner.generator.Model;

import android.graphics.Bitmap;

import com.moutamid.qr.scanner.generator.qrscanner.History;

    public class CardHistoryModel {
    private History history;
    private Bitmap bitmap, bitmap2;

    public CardHistoryModel(History history, Bitmap bitmap, Bitmap bitmap2) {
        this.history = history;
        this.bitmap = bitmap;
        this.bitmap2 = bitmap2;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }
}
