package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import com.thesis.thesisdefense.Activities.MapView;

import java.util.ArrayList;

/**
 * Created by justine on 3/10/18.
 */

public class Wizard extends Ally{

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    private ArrayList<Projectile> projectiles;
    private Bitmap projImage;

    public Wizard(int posX, int poxY, int indexX, int indexY, Bitmap image, Bitmap projImage, float scale) {
        super(posX, poxY, 5, 1, 1000, indexX, indexY,
                478, 484, image, scale,
                7, 8, 7);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;
        this.projImage = projImage;
        projectiles = new ArrayList<>();

    }

    public int attackEnemy(){
        if(!this.isAttacking && this.readyToAttack){
            this.isAttacking = true;
        }
        // kill is basically the variable to trigger the damage
        // for wizards, instead of damaging immediately when attack animation finishes
        // a projectile will be launched
        if(this.kill){
            kill = false;
            projectiles.add(new Projectile(posX + MapView.m_BlockSize, posY, projImage,
                    this.scale, this, (int)(50 * scale), 30));
        }
        return 0;
    }


    public ArrayList<Projectile> getProjectiles(){ return projectiles; }

    public void removeProjectile(Projectile projectile){ projectiles.remove(projectile); }

}
