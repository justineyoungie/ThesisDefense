package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */


import android.graphics.drawable.BitmapDrawable;

/**
 * Literally anything drawable in the Canvas
 */

public abstract class Drawable extends Object {
    protected int posX;
    protected int posY;
    protected int sizeX;
    protected int sizeY;
    protected BitmapDrawable image;

    public Drawable(int posX, int posY, int sizeX, int sizeY, BitmapDrawable image) {
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.image = image;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPoxY(int poxY) {
        this.posY = poxY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }
}
