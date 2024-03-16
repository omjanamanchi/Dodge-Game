package com.example.dodgegame;

import android.graphics.Bitmap;

public class Meatball {
    public Bitmap image;
    public int left, bottom;

    public Meatball(Bitmap image, int left, int bottom) {
        this.image = image;
        this.left = left;
        this.bottom = bottom;

    }
}
