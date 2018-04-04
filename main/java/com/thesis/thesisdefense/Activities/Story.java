package com.thesis.thesisdefense.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.thesisdefense.R;

public class Story extends AppCompatActivity {

    private ImageView characterActive;
    private TypeWriter dialog;
    private TextView characterName;
    private View layout;
    private String[][] levels = {{  "User: Soldier! What's happening?",
                                    "Lorio: Sir! Infantry from the Panelist Kingdom are staging an attack towards Thesis.",
                                    "User: Where are all the Thesis defenders?",
                                    "Lorio: Most of the soldiers, including our commander-in-chief, were ambushed by the enemy forces. Only a few of us remain now.",
                                    "User: What's your name, young soldier?",
                                    "Lorio: Lorio, sir.",
                                    "User: Alright, Lorio. Gather all men who are able to fight and let me lead the defense."}};

    private int currentConversation = 0;
    private int currentLevel = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        layout = this.findViewById(R.id.layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        if(dialog.oneClick()){
                            currentConversation++;
                            drawConversation();
                        }
                        break;
                }
                return true;
            }
        });
        characterActive = this.findViewById(R.id.image_char);
        dialog = (TypeWriter) this.findViewById(R.id.text_dialog);
        characterName = this.findViewById(R.id.text_name);

    }

    private void setCurrentLevel(int currentLevel){
        this.currentLevel = currentLevel;
    }

    private void drawConversation(){
        if(currentConversation < levels[currentLevel - 1].length) {
            String message = levels[currentLevel - 1][currentConversation];
            String character = message.substring(0, message.indexOf(":"));
            String msg = message.substring(message.indexOf(":") + 2);

            switch (character) {
                case "User":
                    characterActive.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Lorio":
                    characterActive.setImageResource(R.drawable.knight_portrait);
            }
            characterName.setText(character);

            dialog.setText("");
            dialog.setCharacterDelay(150);
            dialog.animateText(msg);
            currentConversation++;
        }
        else{
            startActivity(new Intent(this, Level.class));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        this.drawConversation();
    }
}
