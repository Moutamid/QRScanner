package com.moutamid.qr.scanner.generator.interfaces;


import android.view.View;

public interface ButtonItemClickListener {
    void clickedItem(View view,int position);
    void clickedItemButton(View view,int position,String type);
}
