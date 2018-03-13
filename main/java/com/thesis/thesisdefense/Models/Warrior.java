package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/12/18.
 */

public class Warrior extends Ally {

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Warrior(int posX, int poxY, int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 7, 2, 500, indexX, indexY, image, scale, 4, 10, 1);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.getImageHeight();
    }
}
