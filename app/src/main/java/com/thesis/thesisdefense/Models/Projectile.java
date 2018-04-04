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

    public Projectile(int posX, int posY, Bitmap image, float scale, Fighter launcher, int width, int speed){
        super(posX, posY, image, scale);
        this.launcher = launcher;
        this.width = width;
        this.speed = speed;
    }

    //for allies with projectiles
    public int checkContact(ArrayList<Enemy> enemies){
        // move projectile
        posX += speed;

        for(int i = 0; i < enemies.size(); i ++){
            Enemy enemy = enemies.get(i);

            // if projectile hits enemy; I'm not good at contact between two objects I'm sorry :(
            if( enemy.posX <= this.posX + width * scale &&
                this.posX >= enemy.posX - MapView.m_BlockSize / 2 &&
                enemy.getLane() == ((Ally)launcher).indexY){ // and enemy is same lane as launcher of attack
                if(((Ally) launcher).getEnemies().size() != 0) { // band-aid to unexpected IndexOutOfBoundsException
                    hasEncountered = true;
                    if (((Ally) launcher).getEnemies().get(0).calculateDamage(launcher.getDamage())) {
                        return enemies.get(0).getScore();
                    }
                }
            }
            else if(posX > MapView.m_ScreenWidth){
                hasEncountered = true;
            }
        }
        return 0;
    }

    //for enemies with projectiles
    public int checkContact(Ally[][] allyMap){
        posX -= speed;
        for(int y = 0; y < allyMap.length; y++){
            for(int x = 0; x < allyMap[y].length; x++){
                Ally ally = allyMap[y][x];
                if( this.posX <= ally.posX + MapView.m_BlockSize + 90 &&
                    ally.posX + (MapView.m_BlockSize + 90) / 2 >= this.posX + width / 2){
                    ((Enemy) launcher).encounterAlly(ally);
                    hasEncountered = true;
                }
            }
        }
        return 0;
    }

    public boolean hasEncountered(){
        return hasEncountered;
    }
}
