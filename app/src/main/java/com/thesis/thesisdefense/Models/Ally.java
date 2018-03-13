package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by justine on 3/5/18.
 */

public abstract class Ally extends Fighter{

    protected int indexX; //index in map array
    protected int indexY;

    protected ArrayList<Enemy> enemies;

    /*
        maxHealth, damage, attackPause are hardcoded in constructors of child classes and is dependent on class
        idleFrame, numberOfFrames are hardcoded in constructors but highly dependent on asset design
    */

    public Ally(int posX, int poxY, int maxHealth, int damage, long attackPause,
                int indexX, int indexY, Bitmap image, float scale, int idleFrame,
                int numberOfFrames, double range) {
        super(posX, poxY, maxHealth, damage, image, scale, idleFrame, numberOfFrames, attackPause, range);
        this.indexX = indexX;
        this.indexY = indexY;
        enemies = new ArrayList<>();
    }

    public void updateAlly(){
        while(enemies.size() != 0 && enemies.get(0).getCurrentHealth() == 0){
            enemies.remove(0);
        }
        if (enemies.size() == 0) {
            this.isAttacking = false;
        }
        else {
            if(!this.isAttacking){
                this.isAttacking = true;
            }
            if(this.kill){
                int eHealth = enemies.get(0).takeDamage(this.damage); //get health after current dmg, idk wat to do with it xd it was handled by the loop
                kill = false;
            }
        }
    }
    public void encounterEnemy(Enemy enemy){ //when enemy is in range,
        enemies.add(enemy);
    }
}
