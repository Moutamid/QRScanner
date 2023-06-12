package com.moutamid.qr.scanner.generator.interfaces;

import android.view.View;

import com.moutamid.qr.scanner.generator.qrscanner.History;

public interface HistoryItemClickListner {
    void clickedItem(View view, int position,String type,String data);
    void deleteSingleItem(History history, int i);
}
