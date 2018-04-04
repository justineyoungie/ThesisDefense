package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.thesisdefense.Models.Warrior;
import com.thesis.thesisdefense.Models.Wizard;
import com.thesis.thesisdefense.Models.Archer;
import com.thesis.thesisdefense.Models.Spearman;
import com.thesis.thesisdefense.R;

public class UpgradeShop extends AppCompatActivity{

    private Warrior war;
    private Wizard wiz;
    private Archer arc;
    private Spearman spe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        this.setupUI();
    }

    private void setupUI() {
        Button warlvl1Btn  = this.findViewById(R.id.btn_warlvl1);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(2);
            }
        });

        Button warlvl2Btn  = this.findViewById(R.id.btn_warlvl2);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(4);
            }
        });

        Button warlvl3Btn  = this.findViewById(R.id.btn_warlvl3);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(6);
            }
        });

        Button wizlvl1Btn  = this.findViewById(R.id.btn_wizlvl1);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(2);
            }
        });

        Button wizlvl2Btn  = this.findViewById(R.id.btn_wizlvl2);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(4);
            }
        });

        Button wizlvl3Btn  = this.findViewById(R.id.btn_wizlvl3);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(6);
            }
        });

        Button arclvl1Btn  = this.findViewById(R.id.btn_arclvl1);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(2);
            }
        });

        Button arclvl2Btn  = this.findViewById(R.id.btn_arclvl2);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(4);
            }
        });

        Button arclvl3Btn  = this.findViewById(R.id.btn_arclvl3);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(6);
            }
        });

        Button spelvl1Btn  = this.findViewById(R.id.btn_spelvl1);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(2);
            }
        });


        Button spelvl2Btn  = this.findViewById(R.id.btn_spelvl2);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(4);
            }
        });

        Button spelvl3Btn  = this.findViewById(R.id.btn_spelvl3);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(6);
            }
        });



    }
}