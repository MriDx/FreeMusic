package com.mridx.freemusic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.palette.graphics.Palette;

public class Pojo {

    public static Palette posterPalette;

    public static Palette getPosterPalette() {
        Bitmap myBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_play);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            posterPalette = Palette.from(myBitmap).generate();
        }
        return posterPalette;
    }
    public static void setPosterPalette(Palette palette) {
        posterPalette = palette;
    }

    public Pojo() {

    }

}

