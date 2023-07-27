package com.moutamid.qr.scanner.generator.Model;

public class ResultModel {
    int format;
    String rawData;

    public ResultModel(int format, String rawData) {
        this.format = format;
        this.rawData = rawData;
    }
}
