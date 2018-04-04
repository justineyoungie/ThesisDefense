package com.thesis.thesisdefense.Activities;

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

import com.thesis.thesisdefense.R;

public class LevelSelect extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levelselect);

       
    }

 /*   private void setupUI() {
        Button level1btn = this.findViewById(R.id.button_lvl1);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level2btn = this.findViewById(R.id.button_lvl2);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level3btn = this.findViewById(R.id.button_lvl3);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level4btn = this.findViewById(R.id.button_lvl4);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level5btn = this.findViewById(R.id.button_lvl5);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level6btn = this.findViewById(R.id.button_lvl6);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level7btn = this.findViewById(R.id.button_lvl7);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level8btn = this.findViewById(R.id.button_lvl8);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button level9btn = this.findViewById(R.id.button_lvl9);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });


        Button level10btn = this.findViewById(R.id.button_lvl10);
        level1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });


    }
    */
}