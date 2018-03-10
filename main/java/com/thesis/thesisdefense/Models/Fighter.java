package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */

import android.graphics.Bitmap;

/**
 * Anything that can shoot projectiles or inflict damage
 */

public abstract class Fighter extends Drawable {
    protected int maxHealth;
    protected int currentHealth;
    protected int damage; // damage that fighter can deal
    protected boolean isDead = false;

    protected int incrementX;

    protected int currentFrame = 0;

    public Fighter(int posX, int poxY, int maxHealth, int damage, Bitmap image, float scale) {
        super(posX, poxY, image, scale);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damage = damage;
        this.incrementX = image.getWidth() / 4;
        this.currentFrame = incrementX;
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
        currentFrame += incrementX;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getIncrementX() {
        return incrementX;
    }

}
