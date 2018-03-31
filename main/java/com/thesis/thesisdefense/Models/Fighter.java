package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.thesis.thesisdefense.Activities.MapView;

import static com.thesis.thesisdefense.Activities.MapView.TAG;

/**
 * Anything that can shoot projectiles or inflict damage
 */

public abstract class Fighter extends Drawable {
    protected long FPS = 10;

    protected int maxHealth;
    protected int currentHealth;
    protected int damage; // damage that fighter can deal
    protected boolean isDead = false;

    protected int incrementX; //number of pixels in src image for next frame
    protected int incrementY; // number of pixels in src image for action toggle
    protected int idleFrameEnd; // pixel showing end of idle frames
    protected int attackFrameEnd; // pixel showing end of attack frames
    protected boolean attackBackwards = false; // idle animations are done in loops of forward then backward; a checker for the animation

    protected Point currentFrame;

    protected long attackPause;
    protected long pauseCountdown;
    protected boolean isAttacking = false;
    protected boolean readyToAttack = true;
    protected double range;
    protected int damageFrame = 0;

    protected boolean kill = false; //Ready to damage the foe
    public Fighter(int posX, int poxY, int maxHealth, int damage, int incrementX, int incrementY,
                   Bitmap image, float scale, int idleFrames, int attackFrames, long attackPause, double range) {
        super(posX, poxY, image, scale);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damage = damage;
        this.incrementX = incrementX;
        this.incrementY = incrementY;
        this.currentFrame = new Point(incrementX, incrementY);

        this.idleFrameEnd = image.getWidth() - (8 - idleFrames) * incrementX;
        this.attackFrameEnd = image.getWidth() - (8 - attackFrames) * incrementX;

        this.attackPause = attackPause;
        this.pauseCountdown = attackPause;
        this.range = range;
    }

    /**
     * Subtracts damage to current health of fighter
     *
     * @param damageReceived
     * @return true if damage is more than current health and false otherwise
     */

    public boolean calculateDamage(int damageReceived) {
        currentHealth -= damageReceived;
        if (currentHealth <= 0) {
            isDead = true;
        }
        damageFrame = 2;
        return isDead;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isBeingDamaged(){ return damageFrame > 0; }

    public boolean isDead() {
        return isDead;
    }

    public void nextFrame() {

        // 2 statements for idle animation

        // if not attacking and not yet reached end of idle frames and is not in attacking frames
        if (!isAttacking && currentFrame.x < idleFrameEnd && currentFrame.y <= incrementY){
            currentFrame.x += incrementX;
        }
        // if not attacking and exceeded idle frames OR if not attacking and is in attacking frames
        else if(!isAttacking && currentFrame.x >= idleFrameEnd && !attackBackwards || !isAttacking && currentFrame.y > incrementY && !attackBackwards){
            setToStartingFrame(); // reset to first idle frame
        }


        // statements for attack animation


        // if attacking and current frame is an idle frame
        else if(isAttacking && currentFrame.x < idleFrameEnd && currentFrame.y <= incrementY){
            setToStartingFrame(); // set to starting frame to reset animation
        }

        // if attacking and current frame not yet reached end of image (end of attacking frames)
        else if(isAttacking && currentFrame.x < attackFrameEnd && currentFrame.y > incrementY && !attackBackwards) {
            currentFrame.x += incrementX; // next frame
        }

        // if attacking and current frame exceeds image width (more than end of attacking frames)
        else if(isAttacking && currentFrame.x >= attackFrameEnd && currentFrame.y > incrementY) {
            readyToAttack = false; // attack has finished and needs to pause
            kill = true;
            isAttacking = false; // is no longer attacking
            attackBackwards = true; // reset animation for idle animation
        }

        // if attack animation is finished backwards
        else if(attackBackwards && currentFrame.x <= incrementX){
            attackBackwards = false;
            setToStartingFrame();
        }

        // if attack animation is going backwards for aesthetic purposes lmao
        else if(attackBackwards){
            currentFrame.x -= incrementX;
        }

        pauseCountdown(); // if not attacking, timer for attack pause is triggered

    }

    public Point getCurrentFrame() {
        return currentFrame;
    }

    public int getIncrementX() {
        return incrementX;
    }

    public int getIncrementY() {
        return incrementY;
    }



    public long getAttackPause(){
        return attackPause;
    }

    public long getPauseCountdown(){
        return pauseCountdown;
    }

    public void pauseCountdown(){
        if(isBeingDamaged())
            damageFrame --;

        if(!isAttacking) {
            if(!readyToAttack) {
                pauseCountdown -= 1000 / FPS; // 1000 here is number of millis in a second (im just ganna assume its per tick)
                if (pauseCountdown <= 0) {
                    pauseCountdown = attackPause;
                    readyToAttack = true;
                }
            }
        }
    }


    public boolean isAttacking(){
        return isAttacking;
    }

    public void toggleAttacking(){
        isAttacking = !isAttacking;
    }

    public void setToStartingFrame(){
        if(isAttacking)
            currentFrame.y = incrementY * 2;

        else
            currentFrame.y = incrementY;
        currentFrame.x = incrementX;
    }

    /*
    public int takeDamage(int dmg){

        if(currentHealth -dmg <0){
            currentHealth = 0;
        }
        else{
            currentHealth -= dmg;
        }
        return currentHealth;
    }
    */
}
