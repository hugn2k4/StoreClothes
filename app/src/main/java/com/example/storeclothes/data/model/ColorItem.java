package com.example.storeclothes.data.model;

public class ColorItem {
    private String name;
    private int colorInt;

    public ColorItem(String name, int colorInt) {
        this.name = name;
        this.colorInt = colorInt;
    }

    public String getName() {
        return name;
    }

    public int getColorInt() {
        return colorInt;
    }
}