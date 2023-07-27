package com.moutamid.qr.scanner.generator.Model;

public class ResultModel {
    int format;
    String rawData;

    public ResultModel(int format, String rawData) {
        this.format = format;
        this.rawData = rawData;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
