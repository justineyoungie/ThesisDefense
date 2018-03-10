package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/5/18.
 */

public abstract class Ally extends Fighter{

    protected int indexX; //index in map array
    protected int indexY;
    protected long attackPause;
    protected long pauseCountdown;
    protected boolean isPaused = false;

    public Ally(int posX, int poxY, int maxHealth, int damage, long attackPause,
                int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, maxHealth, damage, image, scale);
        this.attackPause = attackPause;
        this.pauseCountdown = attackPause;
        this.indexX = indexX;
        this.indexY = indexY;
    }

    public long getAttackPause(){
        return attackPause;
    }

    public long getPauseCountdown(){
        return pauseCountdown;
    }

    public boolean pauseCountdown(long FPS){
        if(isPaused) {
            pauseCountdown -= 1000 / FPS; // 1000 here is number of millis in a second
            if (pauseCountdown <= 0) {
                pauseCountdown = attackPause;
                isPaused = false;
            }
        }
        else{
            isPaused = true;
        }

        return !isPaused;
    }

    public boolean isPaused(){
        return isPaused;
    }

}
