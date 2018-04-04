package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

import static com.thesis.thesisdefense.Activities.MapView.m_BlockSize;
import static com.thesis.thesisdefense.Activities.MapView.m_ScreenWidth;

/**
 * Created by justine on 4/4/18.
 */

public class Archer extends Ally {

    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    private ArrayList<Projectile> projectiles;
    private Bitmap projImage;

    private int m_blocksize;
    private int m_ScreenWidth;

    public Archer(int posX, int poxY, int indexX, int indexY, Bitmap image, Bitmap projImage, float scale, int m_blocksize, int m_ScreenWidth) {
        super(posX, poxY, 5, 1, 500, indexX, indexY,
                485, 493, image, scale,
                8, 7, 7);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;
        this.projImage = projImage;
        projectiles = new ArrayList<>();
        this.m_blocksize = m_blocksize;
        this.m_ScreenWidth = m_ScreenWidth;

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
            projectiles.add(new Projectile(posX + m_BlockSize, posY, projImage,
                    this.scale, this, (int)(50 * scale), 30, this.m_blocksize, this.m_ScreenWidth));
        }
        return 0;
    }


    public ArrayList<Projectile> getProjectiles(){ return projectiles; }

    public void removeProjectile(Projectile projectile){ projectiles.remove(projectile); }
}
