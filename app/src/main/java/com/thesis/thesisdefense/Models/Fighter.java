package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */

import android.graphics.Bitmap;
import android.util.Log;

import com.thesis.thesisdefense.Activities.MapView;

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
    protected int idleFrame; //number of pixels that specify until what frame is the idle animation
    protected boolean idleBackwards = false; // idle animations are done in loops of forward then backward; a checker for the animation

    protected int currentFrame;

    protected long attackPause;
    protected long pauseCountdown;
    protected boolean isAttacking = false;
    protected boolean readyToAttack = true;
    protected double range;
    protected boolean isWizard =false;

    protected boolean kill = false; //Ready to damage the foe
    public Fighter(int posX, int poxY, int maxHealth, int damage, Bitmap image, float scale, int idleFrame, int numberOfFrames, long attackPause, double range, boolean isWizard) {
        super(posX, poxY, image, scale);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damage = damage;
        this.incrementX = image.getWidth() / numberOfFrames;
        this.currentFrame = incrementX;
        this.idleFrame = idleFrame * incrementX;
        this.attackPause = attackPause;
        this.pauseCountdown = attackPause;
        this.range = range;
        this.isWizard=isWizard;
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

    public boolean isDead() {
        return isDead;
    }

    public void nextFrame() {

        // 4 statements for idle animation

        // if idle and not yet reached end of idle frames and forward animation
        if(!isAttacking && currentFrame < idleFrame && !idleBackwards)
            currentFrame += incrementX; // next frame
        // if idle and backward animation and not yet reached first idle frame
        else if(!isAttacking && idleBackwards && currentFrame > incrementX){
            currentFrame -= incrementX; // previous frame
        }
        // if idle and backward animation and reached the first frame of idle animation
        else if(!isAttacking && idleBackwards && currentFrame <= incrementX) {
            idleBackwards = false; // go forward animation
        }

        else if(!isAttacking && currentFrame >= idleFrame){
            idleBackwards = true; // go backward animation
        }

        // statements for attack animation

        // if attacking and current frame is starting frame (animation is reset)
        else if(isAttacking && currentFrame == incrementX){
            currentFrame = idleFrame + incrementX; // set to the starting frame of the attacking frames
        }

        // if attacking and current frame is an idle frame
        else if(isAttacking && currentFrame <= idleFrame){
            setToStartingFrame(); // set to starting frame to reset animation
        }

        // if attacking and current frame not yet reached end of image (end of attacking frames)
        else if(isAttacking && currentFrame < getImageWidth())
            currentFrame += incrementX; // next frame

        // if attacking and current frame exceeds image width (more than end of attacking frames)
        else if(isAttacking && currentFrame >= getImageWidth()) {
            readyToAttack = false; // attack has finished and needs to pause
            kill = true;
            //toggleAttacking(); // is no longer attacking
            setToStartingFrame(); // reset animation for idle animation
        }

        pauseCountdown(); // if not attacking, timer for attack pause is triggered

    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getIncrementX() {
        return incrementX;
    }



    public long getAttackPause(){
        return attackPause;
    }

    public long getPauseCountdown(){
        return pauseCountdown;
    }

    public void pauseCountdown(){
        if(!isAttacking) {
            if(!readyToAttack) {
                pauseCountdown --; // 1000 here is number of millis in a second (im just ganna assume its per tick)
                if (pauseCountdown <= 0) {
                    pauseCountdown = attackPause;
                    readyToAttack = true;
                }
            }
            if(false) //if enemy is within range
                isAttacking = true;
        }
    }


    public boolean isAttacking(){
        return isAttacking;
    }

    public boolean isWizard(){
        return isWizard;
    }

    public void toggleAttacking(){
        isAttacking = !isAttacking;
    }

    public void setToStartingFrame(){
        currentFrame = incrementX;
    }

    public int takeDamage(int dmg){
        if(currentHealth -dmg <0){
            currentHealth = 0;
        }
        else{
            currentHealth -= dmg;
        }
        return currentHealth;
    }

}
