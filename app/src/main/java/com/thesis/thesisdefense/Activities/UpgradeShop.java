package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.thesis.thesisdefense.DatabaseHelpers.GameDBhelper;
import com.thesis.thesisdefense.Models.Warrior;
import com.thesis.thesisdefense.Models.Wizard;
import com.thesis.thesisdefense.Models.Archer;
import com.thesis.thesisdefense.Models.Spearman;
import com.thesis.thesisdefense.R;

public class UpgradeShop extends AppCompatActivity{

    public  Warrior war;
    public Wizard wiz;
    public Archer arc;
    public Spearman spe;
    public GameDBhelper dBhelper;
    public int diamonds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        this.setupUI();

        Cursor cursor = dBhelper.getAllData();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                diamonds = cursor.getInt(3);
            }
        }
    }

    private void setupUI() {

        Button warlvl1Btn  = this.findViewById(R.id.btn_warlvl1);
        warlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(2);
            }
        });

        TextView coins = this.findViewById(R.id.coins_amount);
        coins.setText(diamonds);

        Button warlvl2Btn  = this.findViewById(R.id.btn_warlvl2);
        warlvl2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(4);
            }
        });

        Button warlvl3Btn  = this.findViewById(R.id.btn_warlvl3);
        warlvl3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                war.setDamage(6);
            }
        });

        Button wizlvl1Btn  = this.findViewById(R.id.btn_wizlvl1);
        wizlvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(2);
            }
        });

        Button wizlvl2Btn  = this.findViewById(R.id.btn_wizlvl2);
        wizlvl2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(4);
            }
        });

        Button wizlvl3Btn  = this.findViewById(R.id.btn_wizlvl3);
        wizlvl3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wiz.setDamage(6);
            }
        });

        Button arclvl1Btn  = this.findViewById(R.id.btn_arclvl1);
        arclvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(2);
            }
        });

        Button arclvl2Btn  = this.findViewById(R.id.btn_arclvl2);
        arclvl2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(4);
            }
        });

        Button arclvl3Btn  = this.findViewById(R.id.btn_arclvl3);
        arclvl3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arc.setDamage(6);
            }
        });

        Button spelvl1Btn  = this.findViewById(R.id.btn_spelvl1);
        spelvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(2);
            }
        });


        Button spelvl2Btn  = this.findViewById(R.id.btn_spelvl2);
        spelvl2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(4);
            }
        });

        Button spelvl3Btn  = this.findViewById(R.id.btn_spelvl3);
        spelvl3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spe.setDamage(6);
            }
        });



    }

    public void increaseDiamonds(int diamonds)
    {
        this.diamonds+=diamonds;
    }
}