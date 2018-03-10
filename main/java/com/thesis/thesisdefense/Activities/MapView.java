package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import com.thesis.thesisdefense.Models.Wizard;
import com.thesis.thesisdefense.R;

import java.io.IOException;

/**
 * Created by justine on 3/5/18.
 */

public class MapView extends SurfaceView implements Runnable {

    static final String TAG = "Thesis Defense";

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

    //pause of attack animation for wizard in milliseconds
    //to be set in updateGame()

    // The current m_Score
    private int m_Score;

    private int currentWave = 0;
    private int maxWave = 5;

    /*// The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;*/

    private Bitmap bitmapWizard;
    private Bitmap bitmapBackground;
    private Bitmap bitmapWizardIcon;
    private Bitmap bitmapWizardIconDisabled;
    private Bitmap bitmapWizardTransparent;

    /*
        For drawing allies to the map
     */
    private boolean isSelecting;
    // private Ally selectedAlly;
    private Point cursorLocation = new Point();

    private Point[][] map;

    /*
        not yet implemented but needed
     */
    private Ally[][] allyMap = new Ally[5][8];


    float scale = getResources().getDisplayMetrics().density;


    private int x = Math.round(74 * scale);
    private int y = Math.round(44 * scale);

    final int incrementX = Math.round(x);


    // The size in pixels of one tile
    private int m_BlockSize;
    private int m_NumBlocksHigh; // determined dynamically



    public MapView(Context context, Point size) {
        super(context);
        m_context = context;

        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / 10;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / 6;

        // Set the sound up
        //loadSound();

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();

        bitmapWizard = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard);
        bitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        bitmapWizardIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard_icon);
        bitmapWizardIconDisabled = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard_icon_disabled);
        bitmapWizardTransparent = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard_transparent);

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
                map[y][x] = new Point(m_BlockSize * (x + 2),m_NumBlocksHigh * (y + 1));
            }
        }

        // Reset the m_Score
        m_Score = 0;
        allyMap[4][3] = new Wizard(map[4][3].x, map[4][3].y,
                3, 4,
                bitmapWizard, scale);

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
            //m_Canvas.drawColor(Color.argb(255, 51, 181, 229));


            // draw background
            final int bgWidth = bitmapBackground.getWidth();
            final int bgHeight = bitmapBackground.getHeight();

            for(int y = 0; y < m_ScreenHeight; y+= bgHeight){
                for(int x = 0; x < m_ScreenWidth; x += bgWidth){
                    m_Canvas.drawBitmap(bitmapBackground, x, y, null);
                }
            }

            //Draw the map
            for (int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++){
                    if((i + j) % 2== 0) m_Paint.setColor(Color.argb(150,168, 75, 18));
                    else    m_Paint.setColor(Color.argb(150, 242, 152, 96));
                    m_Canvas.drawRect(map[i][j].x, map[i][j].y,
                            map[i][j].x + m_BlockSize, map[i][j].y + m_NumBlocksHigh, m_Paint);
                }
            }

            m_Paint.setColor(Color.rgb(0,0,0));
            // to use density pixels instead of actual pixels of image

            Rect src;
            Rect dst;
            //Draw the ally
            for(int y = 0; y < allyMap.length; y++){
                for(int x = 0; x < allyMap[y].length; x++){
                    if(allyMap[y][x] != null){
                        Ally ally = allyMap[y][x];

                        src = new Rect((ally.getCurrentFrame() - ally.getIncrementX()), 0, ally.getCurrentFrame(), ally.getImageHeight());
                        dst = new Rect( map[y][x].x,
                                        map[y][x].y,
                                        map[y][x].x + ally.getIncrementX() + 50,
                                        map[y][x].y + m_NumBlocksHigh);
                        m_Canvas.drawBitmap(bitmapWizard, src, dst, m_Paint);
                    }
                }
            }



            // Choose how big the score will be
            m_Paint.setTextSize(30);
            m_Paint.setColor(Color.rgb(255, 255, 255));
            m_Canvas.drawText("Coins: " + m_Score, (int) 500 * scale, (int)40 * scale, m_Paint);


            m_Canvas.drawText("Wave: " + currentWave + "/" + maxWave, (int) 400 * scale, (int)40 * scale, m_Paint);

            //pause button
            Bitmap pause = BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_media_pause);
            src = new Rect(0, 0, pause.getWidth(), pause.getHeight());
            dst = new Rect( Math.round(580 * scale),
                            Math.round(20 * scale),
                            Math.round(610 * scale),
                            Math.round(50 * scale));
            m_Canvas.drawBitmap(pause, src, dst, m_Paint);

            // wizard icon for selection
            src = new Rect(0, 0, bitmapWizardIcon.getWidth(), bitmapWizardIcon.getHeight());
            dst = new Rect( Math.round(40*scale),
                            Math.round(10*scale),
                            Math.round((bitmapWizardIcon.getWidth() - 30) * scale),
                            Math.round((bitmapWizardIcon.getHeight() - 30) * scale));

            if(isSelecting) m_Canvas.drawBitmap(bitmapWizardIconDisabled, src, dst, m_Paint);
            else            m_Canvas.drawBitmap(bitmapWizardIcon, src, dst, m_Paint);


            // if user is dragging an ally from the options to the map
            if(isSelecting){
                src = new Rect(0, 0, bitmapWizardTransparent.getWidth(), bitmapWizardTransparent.getHeight());
                dst = new Rect( cursorLocation.x - bitmapWizardTransparent.getWidth() / 2, // to center the image on the cursor
                                cursorLocation.y - bitmapWizardTransparent.getHeight() / 2,
                                cursorLocation.x + bitmapWizardTransparent.getWidth(),
                                cursorLocation.y + m_NumBlocksHigh - bitmapWizardTransparent.getHeight() / 2);
                for(int y = 0; y < map.length; y++){
                    for(int x = 0; x < map[y].length; x++){
                        if( cursorLocation.x >= map[y][x].x &&
                            cursorLocation.x <= map[y][x].x + m_BlockSize &&
                            cursorLocation.y >= map[y][x].y &&
                            cursorLocation.y <= map[y][x].y + m_NumBlocksHigh &&
                            allyMap[y][x] == null){ // if within a block inside map and has no one occupying it
                            dst = new Rect( map[y][x].x, // to center the image on the cursor
                                            map[y][x].y,
                                            map[y][x].x + bitmapWizardTransparent.getWidth() + 50,
                                            map[y][x].y + m_NumBlocksHigh);
                            Log.e(TAG, "Coord: (" + map[y][x].x + ", " + map[y][x].y + ");" +
                                    " Index: (" + y + ", " + x + ")");
                        }
                    }
                }


                m_Canvas.drawBitmap(bitmapWizardTransparent, src, dst, null);
            }

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

        for(int y = 0; y < allyMap.length; y++){
            for(int x = 0; x < allyMap[y].length; x++){
                if(allyMap[y][x] != null){
                    Ally ally = allyMap[y][x];
                    if(ally instanceof Wizard) {

                        if (ally.getCurrentFrame() < ally.getImageWidth() && //if not yet out of bounds in src image
                           ((Wizard) ally).isForward() && //forward animation for wizards
                           ally.getPauseCountdown() == 1000) { // not yet paused for
                            ally.nextFrame();
                        }

                        else if(ally.getCurrentFrame() >= ally.getImageWidth() && //if out of bounds
                                ((Wizard) ally).isForward()){ //and still going forward
                            ((Wizard) ally).toggleForwardAnimation(); //make animation go backwards
                        }

                        else if(!((Wizard) ally).isForward() && //if not going forward
                                ally.getCurrentFrame() > ally.getIncrementX()){ //and not yet out of bounds
                            ((Wizard) ally).previousFrame();
                        }

                        else{
                            if(ally.pauseCountdown(FPS))
                                ((Wizard) ally).toggleForwardAnimation();
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if( motionEvent.getX() >= Math.round(40 * scale) &&
                    motionEvent.getX() <= Math.round((bitmapWizardIcon.getWidth() - 30 ) * scale) &&
                    motionEvent.getY() >= Math.round(10 * scale) &&
                    motionEvent.getY() <= Math.round((bitmapWizardIcon.getHeight() - 30 )* scale)){
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                cursorLocation.x = (int) motionEvent.getX();
                cursorLocation.y = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                for(int y = 0; y < map.length; y++){
                    for(int x = 0; x < map[y].length; x++){
                        if( motionEvent.getX() >= map[y][x].x &&
                            motionEvent.getX() <= map[y][x].x + m_BlockSize &&
                            motionEvent.getY() >= map[y][x].y &&
                            motionEvent.getY() <= map[y][x].y + m_NumBlocksHigh &&
                            isSelecting && allyMap[y][x] == null){
                            allyMap[y][x] = new Wizard(map[y][x].x, map[y][x].y, x, y, bitmapWizard, scale);
                        }
                    }
                }
                isSelecting = false;
        }
        return true;
    }
}
