package com.thesis.thesisdefense.Models;

import android.graphics.Point;

import java.util.ArrayList;

import static com.thesis.thesisdefense.Activities.MapView.m_BlockSize;

/**
 * Created by justine on 4/1/18.
 */

public abstract class Spell {
    protected ArrayList<Point> areaOfEffect;
    protected ArrayList<Point> actualBlocks;
    protected String status;
    protected int damage;
    protected int duration;

    public Spell(int damage, String status, int duration){
        this.damage = damage;
        this.status = status;
        this.duration = duration;
        areaOfEffect = new ArrayList<>();
        actualBlocks = new ArrayList<>();
    }

    // int representing total score of enemies defeated
    public int activateSpell(int x, int y, ArrayList<Enemy> enemies, Point[][] map){
        // loop through all blocks affected by the spell
        int total = 0;

        updateAreaOfEffect(x, y);
        for(int i = 0; i < actualBlocks.size(); i++){
            Point affectedBlock = actualBlocks.get(i);
            for(int j = 0; j < enemies.size(); j++){
                Enemy enemy = enemies.get(j);
                if( enemy.getLane() == affectedBlock.y && // if same lane
                    enemy.getPosX() <= map[affectedBlock.y][affectedBlock.x].x + m_BlockSize && // left edge of enemy less than right of block
                    enemy.getPosX() + (m_BlockSize + 80) / 2 >= map[affectedBlock.y][affectedBlock.x].x &&
                    !enemy.isDead()){
                    if(!enemy.calculateDamage(this.damage)){ // if not dead
                        enemy.inflictStatus(status);
                    }
                    else{
                        total += enemy.getScore();
                    }
                }
            }

        }
        return total;
    }

    public boolean countdownDuration(){
        duration --;
        return duration == 0;
    }

    protected void updateAreaOfEffect(int x, int y){
        for (int i = 0; i < areaOfEffect.size(); i++) {
            Point affectedBlock = areaOfEffect.get(i);
            if (affectedBlock.x + x >= 0 && affectedBlock.x + x <= 7 &&
                    affectedBlock.y + y >= 0 && affectedBlock.y + y <= 4) {
                affectedBlock.x += x; // area of effect is basically computed coordinates of the target of the spell
                affectedBlock.y += y; // based on given Point in map selected
                actualBlocks.add(affectedBlock);
            }
        }
    }


    public ArrayList<Point> getAreaOfEffect() { return actualBlocks; }
}
