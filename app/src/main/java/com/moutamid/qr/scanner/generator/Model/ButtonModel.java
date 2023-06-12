package com.moutamid.qr.scanner.generator.Model;

public class ButtonModel {

    private final String bt_name;
    private String color;

    public ButtonModel(String bt_name, String color) {
        this.bt_name = bt_name;
        this.color = color;
    }

    public String getBt_name() {
        return bt_name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
