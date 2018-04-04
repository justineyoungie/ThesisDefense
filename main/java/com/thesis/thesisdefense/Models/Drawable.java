package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */


import android.graphics.Bitmap;

/**
 * Literally anything drawable in the Canvas
 */

public abstract class   Drawable extends Object {
    protected int posX;
    protected int posY;
    protected int sizeX;
    protected int sizeY;
    protected Bitmap image;
    protected float scale;
    protected int AllowanceX = 0;
    public Drawable(int posX, int posY, Bitmap image, float scale) {
        this.posX = posX;
        this.posY = posY;
        this.sizeX = (int) (image.getWidth() * scale);
        this.sizeY = (int) (image.getHeight() * scale);
        this.image = image;
        this.scale = scale;
    }
    public Drawable(int posX, int posY, Bitmap image, float scale, int AllowanceX) {
        this.posX = posX;
        this.posY = posY;
        this.sizeX = (int) (image.getWidth() * scale);
        this.sizeY = (int) (image.getHeight() * scale);
        this.image = image;
        this.scale = scale;
        this.AllowanceX =AllowanceX;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getScaledWidth() {
        return sizeX;
    }

    public int getScaledHeight() {
        return sizeY;
    }

    public int getImageWidth() { return image.getWidth(); }

    public int getImageHeight() { return image.getHeight(); }

    public Bitmap getImage() { return image; }

    public int getAllowanceX(){ return AllowanceX; }
}
