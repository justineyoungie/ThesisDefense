package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/10/18.
 */

public class Wizard extends Ally{
    private boolean forwardAnimation = true;

    public Wizard(int posX, int poxY, int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 5, 1, 1000, indexX, indexY, image, scale);

    }

    public boolean isForward(){
        return forwardAnimation;
    }

    public void toggleForwardAnimation(){
        forwardAnimation = !forwardAnimation;
    }

    public void previousFrame(){
        currentFrame -= incrementX;
    }

}
