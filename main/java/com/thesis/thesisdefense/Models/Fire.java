package com.thesis.thesisdefense.Models;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by justine on 4/1/18.
 */

public class Fire extends Spell{

    public Fire() {
        super(10, "Burn", 10);
        areaOfEffect.add(new Point(-1, -1));
        areaOfEffect.add(new Point(-1, 0));
        areaOfEffect.add(new Point(-1, 1));
        areaOfEffect.add(new Point(0, -1));
        areaOfEffect.add(new Point(0, 0));
        areaOfEffect.add(new Point(0, 1));
        areaOfEffect.add(new Point(1, -1));
        areaOfEffect.add(new Point(1, 0));
        areaOfEffect.add(new Point(1, 1));

    }
}
