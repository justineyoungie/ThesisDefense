package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/5/18.
 */

public abstract class Ally extends Fighter{

    protected int indexX; //index in map array
    protected int indexY;

    /*
        maxHealth, damage, attackPause are hardcoded in constructors of child classes and is dependent on class
        idleFrame, numberOfFrames are hardcoded in constructors but highly dependent on asset design
    */

    public Ally(int posX, int poxY, int maxHealth, int damage, long attackPause,
                int indexX, int indexY, Bitmap image, float scale, int idleFrame,
                int numberOfFrames) {
        super(posX, poxY, maxHealth, damage, image, scale, idleFrame, numberOfFrames, attackPause);
        this.indexX = indexX;
        this.indexY = indexY;
    }

}
