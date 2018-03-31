package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/10/18.
 */

public class Wizard extends Ally{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Wizard(int posX, int poxY, int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 5, 1, 1000, indexX, indexY,
                478, 484, image, scale,
                7, 7, 3);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;

    }


}
