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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.thesisdefense.DatabaseHelpers.GameDBhelper;
import com.thesis.thesisdefense.R;

public class Nickname extends AppCompatActivity{
private GameDBhelper gdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        this.setupUI();
    }

    private void setupUI() {
        final EditText editNickname = this.findViewById(R.id.edittext_nickname);
        Button enterBtn = this.findViewById(R.id.enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editNickname.getText().toString();
                gdb.updateName(nickname);
                Intent i = new Intent(Nickname.this, Menu.class);
                startActivity(i);

            }
        });

    }
}