package com.thesis.thesisdefense.Models;

import android.graphics.Point;

import java.util.ArrayList;

import static com.thesis.thesisdefense.Activities.MapView.m_BlockSize;


/**
 * Created by justine on 4/3/18.
 */

public class Ice extends Spell {

    public Ice() {
        super(2, "Freeze", 10);
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 8; j++){
                areaOfEffect.add(new Point(j, i));
            }
        }
    }

    public int activateSpell(ArrayList<Enemy> enemies, Point[][] map){
        // loop through all blocks affected by the spell
        int total = 0;
        for(int i = 0; i < areaOfEffect.size(); i++){
            Point affectedBlock = areaOfEffect.get(i);
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

    @Override
    public ArrayList<Point> getAreaOfEffect() { return areaOfEffect; }
}
