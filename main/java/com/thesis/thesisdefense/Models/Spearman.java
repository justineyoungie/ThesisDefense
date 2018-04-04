package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import com.thesis.thesisdefense.Activities.MapView;

import java.util.ArrayList;

/**
 * Created by justine on 4/4/18.
 */

public class Spearman extends Ally{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    public Spearman(int posX, int poxY, int indexX, int indexY, Bitmap image, float scale) {
        super(posX, poxY, 10, 2, 500, indexX, indexY, 615, 475, image, scale, 6, 8, 2);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;
    }

    public int attackEnemy(){
        int total = 0;
        if(!this.isAttacking && this.readyToAttack){
            this.isAttacking = true;
        }
        if(this.kill){
            kill = false;
            for(int i = 0; i < enemies.size(); i++){
                if(enemies.get(i).calculateDamage(this.damage))
                    total += enemies.get(i).getScore();
            }

        }
        return total;
    }
}
