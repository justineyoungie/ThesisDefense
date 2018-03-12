package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

/**
 * Created by justine on 3/10/18.
 */

public class Panelist extends Enemy{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Panelist(int posX, int poxY, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 5, 1, 1000, indexY, image, scale, 6, 6, 5);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.getImageHeight();

    }

    @Override
    public void updateEnemy(Ally[][] allyMap, int m_BlockSize){

        //checks every ally in the Lane
        for(int i = 0; i < allyMap[this.LaneY].length; i++){
            Ally ally = allyMap[this.LaneY][i];
            if(ally != null){
                if(this.posX <= ally.posX+m_BlockSize-30 && this.posX > ally.posX + 30){
                    this.inCombat = true;
                }
            }
        }
        if(!this.inCombat) {
            this.posX -= this.speed;
        }
        this.nextFrame();
    }
}
