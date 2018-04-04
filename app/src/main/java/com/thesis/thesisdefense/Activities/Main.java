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
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.thesisdefense.R;

public class Main extends AppCompatActivity{

    private ImageView logo;
    private TextView touch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_nickname);
=======
        setContentView(R.layout.activity_main);
>>>>>>> 0ada1ed537ed0bdfc0f5a15b5bc4e3ecd5ef18d6

        logo = this.findViewById(R.id.image_logo);
        touch = this.findViewById(R.id.text_touch);

        setupAnimationLogo();
    }

    private void setupAnimationLogo() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        touch.setAnimation(animation);

    }

    @Override
<<<<<<< HEAD
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Intent intent = new Intent(this, Menu.class);
=======

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Intent intent = new Intent(this, Nickname.class);
>>>>>>> 0ada1ed537ed0bdfc0f5a15b5bc4e3ecd5ef18d6
                this.startActivity(intent);
                break;
        }
        return true;
    }
}