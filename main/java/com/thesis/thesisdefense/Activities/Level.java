package com.thesis.thesisdefense.Activities;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;


public class Level extends AppCompatActivity {

    public MapView mapView;
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
        mapView = new MapView(this, size);

        // Make snakeView the default view of the Activity
        setContentView(mapView);
    }

    // Start the thread in snakeView when this Activity
    // is shown to the player
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Make sure the thread in snakeView is stopped
    // If this Activity is about to be closed
    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

}
