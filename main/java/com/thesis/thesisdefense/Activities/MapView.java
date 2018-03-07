package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thesis.thesisdefense.Models.Ally;
import com.thesis.thesisdefense.R;

import java.io.IOException;

/**
 * Created by justine on 3/5/18.
 */

public class MapView extends SurfaceView implements Runnable {

    // All the code will run separately to the UI
    private Thread m_Thread = null;
    // This variable determines when the game is playing
    // It is declared as volatile because
    // it can be accessed from inside and outside the thread
    private volatile boolean m_Playing;

    // This is what we draw on
    private Canvas m_Canvas;
    // This is required by the Canvas class to do the drawing
    private SurfaceHolder m_Holder;
    // This lets us control colors etc
    private Paint m_Paint;

    // This will be a reference to the Activity
    private Context m_context;

    // Sound
    private SoundPool m_SoundPool;
    private int m_get_mouse_sound = -1;
    private int m_dead_sound = -1;

    // For tracking movement m_Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Direction m_Direction = Direction.RIGHT;

    // What is the screen resolution
    private int m_ScreenWidth;
    private int m_ScreenHeight;

    // Control pausing between updates
    private long m_NextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_IN_A_SECOND = 1000;
    // We will draw the frame much more often

    // The current m_Score
    private int m_Score;

    /*// The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;*/

    private Bitmap bitmapWizard;

    private Point[][] map;

    /*
        not yet implemented but needed
     */
    private Ally[][] allyMap;


    float scale = getResources().getDisplayMetrics().density;

    private int x = Math.round(74 * scale);
    private int y = Math.round(44 * scale);

    final int incrementX = Math.round(x);


    // The size in pixels of one tile
    private int m_BlockSize;
    private int m_NumBlocksHigh; // determined dynamically

    private boolean forwardAnimation = true;



    public MapView(Context context, Point size) {
        super(context);
        m_context = context;

        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / 9;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / 6;

        // Set the sound up
        //loadSound();

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();

        bitmapWizard = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard);
        map = new Point[5][8];

        // Start the game
        startGame();

    }


    @Override
    public void run() {
        // The check for m_Playing prevents a crash at the start
        // You could also extend the code to provide a pause feature
        while (m_Playing) {

            // Update 10 times a second
            if(checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }

    public void pause() {
        m_Playing = false;
        try {
            m_Thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();
    }

    public void startGame() {
        // Load the points of each tile
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[y].length; x++){
                map[y][x] = new Point(m_BlockSize * (x + 1),m_NumBlocksHigh * (y + 1));
            }
        }

        // Reset the m_Score
        m_Score = 0;

        // Setup m_NextFrameTime so an update is triggered immediately
        m_NextFrameTime = System.currentTimeMillis();
    }

    public void loadSound() {
        m_SoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = m_context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            m_get_mouse_sound = m_SoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            m_dead_sound = m_SoundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }

    public void drawGame() {
        // Prepare to draw
        if (m_Holder.getSurface().isValid()) {
            m_Canvas = m_Holder.lockCanvas();

            // Clear the screen with my favorite color
            m_Canvas.drawColor(Color.argb(255, 51, 181, 229));




        /*
            // Choose how big the score will be
            m_Paint.setTextSize(30);
            m_Canvas.drawText("Score:" + m_Score, 10, 30, m_Paint);
        */

            //Draw the map
            for (int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++){
                    if((i + j) % 2== 0) m_Paint.setColor(Color.rgb(168, 75, 18));
                    else    m_Paint.setColor(Color.rgb(242, 152, 96));
                    m_Canvas.drawRect(map[i][j].x, map[i][j].y,
                            map[i][j].x + m_BlockSize, map[i][j].y + m_NumBlocksHigh, m_Paint);
                }
            }

            // to use density pixels instead of actual pixels of image

            //Draw the ally
            Rect src = new Rect(x - incrementX, 0, x, y);
            Rect dst = new Rect(map[0][0].x, map[0][0].y, map[0][0].x + m_BlockSize, map[0][0].y + m_NumBlocksHigh);
            m_Canvas.drawBitmap(bitmapWizard, src, dst, m_Paint);

            // Draw the whole frame
            m_Holder.unlockCanvasAndPost(m_Canvas);
        }
    }

    public boolean checkForUpdate() {

        // Are we due to update the frame
        if(m_NextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            m_NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    public void updateGame() {


        Log.e("X Coord", x + "");
        if(x < 296 * scale && forwardAnimation) {
            x += incrementX;
        }
        else if (x >= 296 * scale && forwardAnimation){
            forwardAnimation = false;
        }
        else if (!forwardAnimation && x > incrementX)
            x -= incrementX;
        else{
            forwardAnimation = true;
            m_NextFrameTime = System.currentTimeMillis() + MILLIS_IN_A_SECOND;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= m_ScreenWidth / 2) {
                    switch(m_Direction){
                        case UP:
                            m_Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            m_Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            m_Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            m_Direction = Direction.UP;
                            break;
                    }
                } else {
                    switch(m_Direction){
                        case UP:
                            m_Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            m_Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            m_Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            m_Direction = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }
}
