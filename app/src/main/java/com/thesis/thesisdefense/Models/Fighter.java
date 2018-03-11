package com.thesis.thesisdefense.Models;

/**
 * Created by justine on 3/5/18.
 */

import android.graphics.drawable.BitmapDrawable;

/**
 * Anything that can shoot projectiles or inflict damage
 */

public abstract class Fighter extends Drawable {
    protected int maxHealth;
    protected int currentHealth;
    protected int damage; // damage that fighter can deal
    protected boolean isDead = false;

    public Fighter(int posX, int poxY, int sizeX, int sizeY, int maxHealth, int damage, BitmapDrawable image) {
        super(posX, poxY, sizeX, sizeY, image);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damage = damage;
    }

    /**
     * Subtracts damage to current health of fighter
     * @param damageReceived
     * @return true if damage is more than current health and false otherwise
     */

    public boolean calculateDamage(int damageReceived){
        currentHealth -= damageReceived;
        if(currentHealth <= 0){
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

    public boolean isDead(){
        return isDead;
    }
}
