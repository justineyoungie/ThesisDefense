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

    // returns 0 if no enemy was killed, returns the score of the enemy killed if otherwise
    public int updateAlly(ArrayList<Enemy> curEnemies, int m_BlockSize){
        nextFrame();
        for(int i = 0; i < curEnemies.size(); i++){
            Enemy enemy = curEnemies.get(i);
            if(!enemies.contains(enemy) && enemy.LaneY == indexY && enemy.getCurrentHealth() != 0 &&
                    enemy.getPosX() <= this.posX+m_BlockSize*range+30 && enemy.getPosX() + (m_BlockSize + 60) / 2 > this.posX +30){
                encounterEnemy(enemy);
            }
        }
        while(enemies.size() != 0 && enemies.get(0).isDead()){
            enemies.remove(0);
        }
        if (enemies.size() == 0) {
            this.isAttacking = false;
        }
        else {
            if(!this.isAttacking && this.readyToAttack){
                this.isAttacking = true;
            }
            if(this.kill){
                boolean isDead = enemies.get(0).calculateDamage(this.damage); //get health after current dmg, idk wat to do with it xd it was handled by the loop
                kill = false;

                if (isDead){
                    return enemies.get(0).getScore();
                }
            }
        }
        return 0;
    }
    public void encounterEnemy(Enemy enemy){ //when enemy is in range,
        enemies.add(enemy);
    }
}
