package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/10/18.
 */

public class Panelist extends Enemy{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Panelist(int posX, int poxY, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 35, 1, 1000, indexY, image, scale, 6, 6, 5, 1);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.getImageHeight();

    }

    @Override
    public void updateEnemy(Ally[][] allyMap, int m_BlockSize){

        //checks every ally in the Lane
        for(int i = 0; i < allyMap[this.LaneY].length; i++){
            Ally ally = allyMap[this.LaneY][i];
            if(ally != null){
                if(this.posX <= ally.posX+m_BlockSize*ally.range-30 && this.posX > ally.posX + 30 && !ally.enemies.contains(this)){ //if enemy is in ally's range, add this to his encounter
                    ally.encounterEnemy(this);
                }
                if(this.posX <= ally.posX+m_BlockSize*range-30 && this.posX > ally.posX + 30 && this.Rival == null){ //add ally to enemys encounter if ally is in range
                    this.Rival = ally;
                }
            }
        }
        if(this.Rival == null) {
            this.posX -= this.speed;
        }
        this.nextFrame();
    }
}
