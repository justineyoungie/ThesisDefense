package com.thesis.thesisdefense.Models;

import android.graphics.Point;

/**
 * Created by justine on 4/3/18.
 */

public class Thunder extends Spell {
    public Thunder() {
        super(5, "", 10);
        areaOfEffect.add(new Point(-7, 0));
        areaOfEffect.add(new Point(-6, 0));
        areaOfEffect.add(new Point(-5, 0));
        areaOfEffect.add(new Point(-4, 0));
        areaOfEffect.add(new Point(-3, 0));
        areaOfEffect.add(new Point(-2, 0));
        areaOfEffect.add(new Point(-1, 0));
        areaOfEffect.add(new Point(0, 0));
        areaOfEffect.add(new Point(1, 0));
        areaOfEffect.add(new Point(2, 0));
        areaOfEffect.add(new Point(3, 0));
        areaOfEffect.add(new Point(4, 0));
        areaOfEffect.add(new Point(5, 0));
        areaOfEffect.add(new Point(6, 0));
        areaOfEffect.add(new Point(7, 0));
    }
}
