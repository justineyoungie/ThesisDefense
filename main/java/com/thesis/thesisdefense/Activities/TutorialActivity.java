package com.thesis.thesisdefense.Activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;


public class TutorialActivity extends AppCompatActivity {

    public Tutorial tutorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_level);

        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new View based on the SnakeView class
        tutorial = new Tutorial(this, size);

        // Make snakeView the default view of the Activity
        setContentView(tutorial);
    }

    // Start the thread in snakeView when this Activity
    // is shown to the player
    @Override
    protected void onResume() {
        super.onResume();
        tutorial.resume();
    }

    // Make sure the thread in snakeView is stopped
    // If this Activity is about to be closed
    @Override
    protected void onPause() {
        super.onPause();
        tutorial.pause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        tutorial.destroy();
    }

}
