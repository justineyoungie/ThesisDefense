package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;
import android.util.Log;

import static com.thesis.thesisdefense.Activities.MapView.TAG;
import static com.thesis.thesisdefense.Activities.MapView.m_BlockSize;

/**
 * Created by justine on 3/10/18.
 */

public class Panelist extends Enemy{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Panelist(int posX, int poxY, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 7, 1, 750, indexY, 471, 494, image, scale, 7, 7, 5, 1, 25);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;

    }

    @Override
    public void updateEnemy(Ally[][] allyMap){

        this.nextFrame();
        //checks every ally in the Lane

        if(this.Rival == null) {
            for(int i = 0; i < allyMap[this.LaneY].length; i++){
                Ally ally = allyMap[this.LaneY][i];
                if(ally != null){
                    if( this.posX <= ally.posX+m_BlockSize*range-30 && this.posX + (m_BlockSize + 90) / 2 > ally.posX + 30 &&
                        this.Rival == null){ //add ally to enemys encounter if ally is in range

                        encounterAlly(ally);
                    }
                }
            }
            this.posX -= this.speed;
        }
        else{
            if(Rival.currentHealth == 0){
                Rival = null;
            }
            else {
                if (!this.isAttacking && this.readyToAttack) {
                    this.isAttacking = true;
                }
                if (this.kill) {
                    boolean isDead = Rival.calculateDamage(this.damage); //get health after current dmg, idk wat to do with it xd it was handled by the loop
                    kill = false;
                    System.out.println("KIIIIIILLLL");
                    if(isDead){
                        Rival = null;
                    }
                }
            }
        }
    }
}