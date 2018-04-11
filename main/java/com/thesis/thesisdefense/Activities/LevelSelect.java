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

       this.setupUI();
    }

  private void setupUI() {
        Button easyBtn = (Button) this.findViewById(R.id.btn_easy);
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LevelSelect.this, Level.class);
                i.putExtra("TAG" , "Easy");
                startActivity(i);
            }
        });

        Button mediumBtn = (Button) this.findViewById(R.id.btn_medium);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LevelSelect.this, Level.class);
                i.putExtra("TAG","Moderate");
                startActivity(i);
            }
        });

        Button hardBtn = (Button) this.findViewById(R.id.btn_hard);
        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LevelSelect.this, Level.class);
                i.putExtra("TAG","Difficult");
                startActivity(i);
            }
        });

        Button tutorialBtn = (Button) this.findViewById(R.id.btn_tutorial);
        tutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LevelSelect.this, TutorialActivity.class);
                startActivity(i);
            }
        });

    }

}