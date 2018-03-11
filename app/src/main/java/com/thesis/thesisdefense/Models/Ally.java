package com.thesis.thesisdefense.Models;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by justine on 3/5/18.
 */

public abstract class Ally extends Fighter{

    protected int indexX; //index in map array
    protected int indexY;

    public Ally(int posX, int poxY, int sizeX, int sizeY, int maxHealth, int damage, int indexX, int indexY, BitmapDrawable image) {
        super(posX, poxY, sizeX, sizeY, maxHealth, damage, image);
        this.indexX = indexX;
        this.indexY = indexY;
    }

}
