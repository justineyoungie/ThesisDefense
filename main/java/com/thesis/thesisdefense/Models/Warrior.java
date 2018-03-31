package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/12/18.
 */

public class Warrior extends Ally {

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Warrior(int posX, int poxY, int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 7, 2, 500, indexX, indexY, 610, 468, image, scale, 7, 4, 1);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;
    }
}
