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

public class Menu extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.setupUI();
    }

    private void setupUI() {
        Button storyBtn = this.findViewById(R.id.button_newstory);
        storyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, LevelSelect.class);
                startActivity(i);
            }
        });

        Button challengeBtn = this.findViewById(R.id.button_challenge);
        challengeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Level.class);
                startActivity(i);
            }
        });

        Button creditsBtn = this.findViewById(R.id.button_credits);
        creditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Credits.class);
                startActivity(i);
            }
        });

    }
}