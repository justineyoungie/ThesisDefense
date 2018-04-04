package com.thesis.thesisdefense.Models;

import android.graphics.Bitmap;

import com.thesis.thesisdefense.Activities.MapView;

import java.util.ArrayList;

import static com.thesis.thesisdefense.Activities.MapView.m_ScreenWidth;

/**
 * Created by justine on 4/4/18.
 */

public class EnemyMage extends Enemy {
    public static int FRAME_WIDTH;
    public static int FRAME_HEIGHT;

    private ArrayList<Projectile> projectiles;
    private Bitmap projImage;

    private boolean isVisible = false;

    private int m_blocksize;
    public EnemyMage(int posX, int poxY, int indexY, Bitmap image, Bitmap projImage, float scale, int m_blocksize) {
        super(posX, poxY, 7, 1, 1000, indexY, 447, 494, image, scale, 7, 7, 5, 7, 100);
        FRAME_WIDTH = this.incrementX;
        FRAME_HEIGHT = this.incrementY;
        this.m_blocksize = m_blocksize;
        this.projImage = projImage;

    }

    @Override
    public void updateEnemy(Ally[][] allyMap){

        this.nextFrame();
        //checks every ally in the Lane

        if(this.Rival == null) {
            for(int i = 0; i < allyMap[this.LaneY].length; i++){
                Ally ally = allyMap[this.LaneY][i];
                if(ally != null){
                    if( this.posX <= ally.posX+m_blocksize*range-30 && this.posX + (m_blocksize + 90) / 2 > ally.posX + 30 &&
                            this.Rival == null){ //add ally to enemys encounter if ally is in range

                        encounterAlly(ally);
                    }
                }
            }
            this.posX -= this.speed;
            if(this.posX <= m_ScreenWidth - 20 * scale) isVisible = true;
        }
        else{
            if(Rival.isDead()){
                Rival = null;
            }
            else {
                if (!this.isAttacking && this.readyToAttack && isVisible) {
                    this.isAttacking = true;
                }
                if (this.kill) {
                    kill = false;
                    projectiles.add(new Projectile(posX + MapView.m_BlockSize, posY, projImage,
                            this.scale, this, (int)(50 * scale), 30,m_blocksize,m_ScreenWidth));
                }
            }
        }
    }


    public ArrayList<Projectile> getProjectiles(){ return projectiles; }

    public void removeProjectile(Projectile projectile){ projectiles.remove(projectile); }
}
