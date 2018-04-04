package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import com.thesis.thesisdefense.Activities.MapView;

import java.util.ArrayList;

/**
 * Created by justine on 4/1/18.
 */

public class Projectile extends Drawable{

    private Fighter launcher; //whoever launched the projectile
    private int width; //for contact with enemy purposes
    private boolean hasEncountered = false;
    private int speed;
    private int m_blockSize;
    private int m_ScreenWidth;
    public Projectile(int posX, int posY, Bitmap image, float scale, Fighter launcher, int width, int speed, int m_blocksize, int m_ScreenWidth){
        super(posX, posY, image, scale);
        this.launcher = launcher;
        this.width = width;
        this.speed = speed;
        this.m_blockSize = m_blocksize;
        this.m_ScreenWidth = m_ScreenWidth;
    }

    //for allies with projectiles
    public int checkContact(ArrayList<Enemy> enemies){
        // move projectile
        posX += speed;

        for(int i = 0; i < enemies.size(); i ++){
            Enemy enemy = enemies.get(i);

            // if projectile hits enemy; I'm not good at contact between two objects I'm sorry :(
            if( enemy.posX <= this.posX + width * scale &&
                this.posX >= enemy.posX - m_blockSize / 2 &&
                enemy.getLane() == ((Ally)launcher).indexY){ // and enemy is same lane as launcher of attack
                if(((Ally) launcher).getEnemies().size() != 0) { // band-aid to unexpected IndexOutOfBoundsException
                    hasEncountered = true;
                    if (((Ally) launcher).getEnemies().get(0).calculateDamage(launcher.getDamage())) {
                        return enemies.get(0).getScore();
                    }
                }
            }
            else if(posX > m_ScreenWidth){
                hasEncountered = true;
            }
        }
        return 0;
    }

    //for enemies with projectiles
    public void checkContact(Ally[][] allyMap){
        posX -= speed;
        for(int y = 0; y < allyMap.length; y++){
            for(int x = 0; x < allyMap[y].length; x++){
                Ally ally = allyMap[y][x];
                if( this.posX <= ally.posX + m_blockSize+ 90 &&
                    ally.posX + (m_blockSize + 90) / 2 >= this.posX + width / 2){
                    if(((Ally) launcher).getEnemies().size() != 0) {
                        hasEncountered = true;
                        if(((Enemy) launcher).Rival.calculateDamage(launcher.getDamage())){
                            ((Enemy) launcher).Rival = null;
                        }
                    }
                }
            }
        }
    }

    public boolean hasEncountered(){
        return hasEncountered;
    }
}
