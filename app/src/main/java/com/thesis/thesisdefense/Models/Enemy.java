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
    protected Ally Rival; // The ally the enemy is currently fighting
    protected int score; // coins earned when defeated
    protected int scoreDisplayLength = 10;

    public Enemy(int posX, int poxY, int maxHealth, int damage, long attackPause,
                int LaneY, int incrementX, int incrementY, Bitmap image, float scale, int idleFrames,
                int attackFrames, double speed, double range, int score) {
        super(posX, poxY, maxHealth, damage, incrementX, incrementY, image, scale, idleFrames, attackFrames, attackPause, range);

        this.LaneY = LaneY; //which lane the enemey is
        this.speed = speed;
        Rival = null;
        this.score = score;
        //FPS is how fast it moves and attacks
    }

    public abstract void updateEnemy(Ally[][] allyMap);

    public int getScore(){
        return score;
    }

    public int getLane(){
        return LaneY;
    }

    public boolean checkDisplayScore(){
        scoreDisplayLength--;
        return scoreDisplayLength == 0;
    }


    public void encounterAlly(Ally ally){
        this.Rival = ally;
    }
}