package com.moutamid.qr.scanner.generator.qrscanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {

    @PrimaryKey(autoGenerate = true)
    private int id=0;

    private final String data;
    private final String type;
    private final boolean scan;

    public History(String data, String type, boolean scan) {
        this.data = data;
        this.type = type;
        this.scan = scan;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public boolean isScan() {
        return scan;
    }
}
