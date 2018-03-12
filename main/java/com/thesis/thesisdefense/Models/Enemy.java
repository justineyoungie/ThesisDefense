package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by Josh on 3/12/18.
 */

public abstract class Enemy extends Fighter{

    //protected int indexX; //index in map array
    //protected int indexY;

    /*
        maxHealth, damage, attackPause are hardcoded in constructors of child classes and is dependent on class
        idleFrame, numberOfFrames are hardcoded in constructors but highly dependent on asset design
    */

    protected int LaneY;
    protected double speed;
    protected boolean inCombat = false;

    public Enemy(int posX, int poxY, int maxHealth, int damage, long attackPause,
                int LaneY, Bitmap image, float scale, int idleFrame,
                int numberOfFrames, double speed) {
        super(posX, poxY, maxHealth, damage, image, scale, idleFrame, numberOfFrames, attackPause);

        this.LaneY = LaneY; //which lane the enemey is
        this.speed = speed;
        //FPS is how fast it moves and attacks
    }

    public void updateEnemy(Ally[][] allyMap, int m_BlockSize){
        //must @Override this method
    }
}
