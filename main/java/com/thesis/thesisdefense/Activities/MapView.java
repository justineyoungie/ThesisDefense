package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thesis.thesisdefense.Models.*;
import com.thesis.thesisdefense.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by justine on 3/5/18.
 */

public class MapView extends SurfaceView implements Runnable {

    public static final String TAG = "Thesis Defense"; //yes

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
    private Bitmap bitmapWarrior;
    private Bitmap bitmapWarriorIcon;
    private Bitmap bitmapPanelist;
    private Bitmap bitmapThesis;
    private Bitmap bitmapCastle;
    private Bitmap bitmapVictory;

    /*
        For drawing allies to the map
     */
    private boolean isSelecting;
    private String selectedAlly = "";

    // private Ally selectedAlly;
    private Point cursorLocation = new Point();

    private Point[][] map;

    /*
        not yet implemented but needed
     */
    private Ally[][] allyMap;


    float scale = getResources().getDisplayMetrics().density;


    // The size in pixels of one tile
    private int m_BlockSize;
    private int m_NumBlocksHigh; // determined dynamically

    public ArrayList<Enemy> enemies;
    private MediaPlayer player;
    private int[] enemyCount;
    private ArrayList<Long>[] enemySpawnTime;
    private int timePassedPerWave = 0;
    private boolean winner = false;
    private boolean gameOver = false;
    private boolean winCon = false;


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

        bitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);

        // wizard
        bitmapWizard = decodeSampleBitmapFromResource(this.getResources(), R.drawable.wizard, 150, 150);
        bitmapWizardIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.wizard_icon);

        bitmapWarrior = decodeSampleBitmapFromResource(this.getResources(), R.drawable.warrior, 150, 150);
        bitmapWarriorIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.warrior_icon);

        bitmapPanelist = BitmapFactory.decodeResource(this.getResources(), R.drawable.panelist);

        bitmapVictory = BitmapFactory.decodeResource(this.getResources(), R.drawable.victory);

        map = new Point[5][8];

        enemies = new ArrayList<>();
        bitmapCastle = decodeSampleBitmapFromResource(this.getResources(), R.drawable.castle, 150, m_ScreenHeight / 2);
        bitmapThesis = BitmapFactory.decodeResource(this.getResources(), R.drawable.thesis);


        loadSound();
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
        drawGame();
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();

    }

    public void startGame() {

        // reset everything
        currentWave = 0;
        allyMap = new Ally[5][8];
        enemyCount = new int[maxWave];
        enemySpawnTime = new ArrayList[maxWave];
        timePassedPerWave = 0;
        gameOver = false;
        winner = false;

        // Load the points of each tile
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[y].length; x++){
                map[y][x] = new Point(m_BlockSize * (x + 2),m_NumBlocksHigh * (y + 1));
            }
        }

        // Reset the m_Score
        m_Score = 150;
        allyMap[4][3] = new Warrior(map[4][3].x, map[4][3].y,
                3, 4,
                bitmapWarrior, scale);

        allyMap[1][0] = new Wizard(map[1][0].x, map[1][0].y,
                0, 1,
                bitmapWizard, scale);

        // initialize the spawn times
        for(int i = 0; i < maxWave; i ++){
            enemySpawnTime[i] = new ArrayList<Long>();
        }

        // for now, manually add each time to the arraylists
        Random rand = new Random();
        enemyCount[0] = rand.nextInt(5) + 1;
        enemyCount[1] = rand.nextInt(5) + 1;
        enemyCount[2] = rand.nextInt(8) + 1;
        enemyCount[3] = rand.nextInt(10) + 1;
        enemyCount[4] = rand.nextInt(15) + 1;


        for(int i = 0; i < enemyCount.length; i++){
            for(int y = 0; y < enemyCount[i]; y++){
                if(y == 0 && i == 0){
                    enemySpawnTime[i].add((long) 10000);
                }
                else if (i == 0){
                    int time = rand.nextInt(100) + 1;
                    enemySpawnTime[i].add((long) (time * 100 + 10000));
                }
                else{
                    int time = rand.nextInt(100) + 1;
                    enemySpawnTime[i].add((long) (time * 100));
                }
            }
        }

        // Setup m_NextFrameTime so an update is triggered immediately
        m_NextFrameTime = System.currentTimeMillis();

    }

    public void loadSound() {
        AssetFileDescriptor afd = null;
        try {
            afd = this.getContext().getAssets().openFd("battle_theme.mp3");
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, "Henlo");
                    mp.start();
                }
            });
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "Henloers");
                    mp.start();
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(TAG, "wew");
                    return false;
                }
            });
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            Log.e(TAG, "Henlongers");
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "error");
            e.printStackTrace();
        }
       /*
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
        */
    }

    public void drawGame() {
        // Prepare to draw
        if (m_Holder.getSurface().isValid()) {
            m_Canvas = m_Holder.lockCanvas();

            // Clear the screen with my favorite color
            //m_Canvas.drawColor(Color.argb(255, 51, 181, 229));

            m_Paint.reset();
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
                    if((i + j) % 2== 0) m_Paint.setColor(Color.argb(150,70, 140, 46));
                    else                m_Paint.setColor(Color.argb(150, 76, 154, 75));
                    m_Canvas.drawRect(map[i][j].x, map[i][j].y,
                            map[i][j].x + m_BlockSize, map[i][j].y + m_NumBlocksHigh, m_Paint);
                }
            }

            m_Paint.setColor(Color.rgb(0,0,0));


            Rect src;
            Rect dst;

            //draw the thesis papers

            src = new Rect(0, 0, bitmapThesis.getWidth(), bitmapThesis.getHeight());
            for(int i = 0; i < map.length; i++){
                dst = new Rect( map[i][0].x - m_BlockSize + m_BlockSize / 4, map[i][0].y + m_NumBlocksHigh / 4,
                                map[i][0].x - m_BlockSize + m_BlockSize / 4 * 3, map[i][0].y + m_NumBlocksHigh / 4 * 3);
                m_Canvas.drawBitmap(bitmapThesis, src, dst, m_Paint);
            }

            // draw castle on the side
            src = new Rect(0, 0, bitmapCastle.getWidth(), bitmapCastle.getHeight());
            dst = new Rect((int)(-120 * scale), (int)(-80 * scale), (int) (120 * scale), m_ScreenHeight);
            m_Canvas.drawBitmap(bitmapCastle, src, dst, m_Paint);

            //Draw the ally
            for(int y = 0; y < allyMap.length; y++){
                for(int x = 0; x < allyMap[y].length; x++){
                    if(allyMap[y][x] != null){
                        Ally ally = allyMap[y][x];
                        src = new Rect((ally.getCurrentFrame() - ally.getIncrementX()), 0, ally.getCurrentFrame(), ally.getImageHeight());
                        dst = new Rect( map[y][x].x,
                                        map[y][x].y,
                                        map[y][x].x + m_BlockSize + 60,
                                        map[y][x].y + m_NumBlocksHigh);
                        m_Canvas.drawBitmap(ally.getImage(), src, dst, m_Paint);
                    }
                }
            }


            //Draw enemies
            for(int y = 0; y < enemies.size(); y++){
                Enemy enemy = enemies.get(y);
                src = new Rect((enemy.getCurrentFrame() - enemy.getIncrementX()), 0, enemy.getCurrentFrame(), enemy.getImageHeight());
                dst = new Rect(enemy.getPosX(), enemy.getPosY(),
                               enemy.getPosX()+m_BlockSize+60,
                               enemy.getPosY()+m_NumBlocksHigh);
                m_Canvas.drawBitmap(enemy.getImage(), src, dst, m_Paint);
            }


            // Choose how big the score will be
            m_Paint.setTextSize(30);
            m_Paint.setColor(Color.rgb(255, 255, 255));
            m_Canvas.drawText("Coins: " + m_Score, (int) 500 * scale, (int)40 * scale, m_Paint);


            m_Canvas.drawText("Wave: " + (currentWave + 1) + "/" + maxWave, (int) 400 * scale, (int)40 * scale, m_Paint);

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
                            Math.round(110*scale),
                            Math.round(50*scale));

            if(isSelecting && selectedAlly.equals("Wizard") || m_Score < 100){
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                m_Canvas.drawBitmap(bitmapWizardIcon, src, dst, m_Paint);
            }
            else
                m_Canvas.drawBitmap(bitmapWizardIcon, src, dst, m_Paint);


            m_Paint.reset();

            // warrior icon for selection
            src = new Rect(0, 0, bitmapWarriorIcon.getWidth(), bitmapWarriorIcon.getHeight());
            dst = new Rect( Math.round(130*scale),
                            Math.round(10*scale),
                            Math.round(200*scale),
                            Math.round(50*scale));

            if(isSelecting && selectedAlly.equals("Warrior") || m_Score < 50){
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                m_Canvas.drawBitmap(bitmapWarriorIcon, src, dst, m_Paint);
            }
            else
                m_Canvas.drawBitmap(bitmapWarriorIcon, src, dst, m_Paint);


            m_Paint.reset();


            // if user is dragging an ally from the options to the map
            if(isSelecting){
                if(selectedAlly.equals("Wizard")) {
                    src = new Rect(0, 0, Wizard.FRAME_WIDTH, Wizard.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 60) / 2, // to center the image on the cursor
                                    cursorLocation.y - m_NumBlocksHigh / 2,
                                    cursorLocation.x + (m_BlockSize + 60) / 2,
                                    cursorLocation.y + m_NumBlocksHigh / 2);
                }
                else if(selectedAlly.equals("Warrior")){
                    src = new Rect(0, 0, Warrior.FRAME_WIDTH, Warrior.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 60) / 2, // to center the image on the cursor
                                    cursorLocation.y - m_NumBlocksHigh / 2,
                                    cursorLocation.x + (m_BlockSize + 60) / 2,
                                    cursorLocation.y + m_NumBlocksHigh / 2);
                }


                //check if cursor is within map
                for(int y = 0; y < map.length; y++){
                    for(int x = 0; x < map[y].length; x++){
                        // if within map, snap transparent to block
                        if( cursorLocation.x >= map[y][x].x &&
                            cursorLocation.x <= map[y][x].x + m_BlockSize &&
                            cursorLocation.y >= map[y][x].y &&
                            cursorLocation.y <= map[y][x].y + m_NumBlocksHigh &&
                            allyMap[y][x] == null){ // if within a block inside map and has no one occupying it
                            dst = new Rect( map[y][x].x, // to center the image on the cursor
                                            map[y][x].y,
                                            map[y][x].x + m_BlockSize + 60,
                                            map[y][x].y + m_NumBlocksHigh);
                        }
                    }
                }

                m_Paint.setAlpha(75);
                if(selectedAlly.equals("Wizard"))
                    m_Canvas.drawBitmap(bitmapWizard, src, dst, m_Paint);
                else if(selectedAlly.equals("Warrior"))
                    m_Canvas.drawBitmap(bitmapWarrior, src, dst, m_Paint);

            }


            if(winner){
                m_Paint.setARGB(60, 0,0,0);
                m_Canvas.drawRect(0, 0, m_ScreenWidth, m_ScreenHeight, m_Paint);

                m_Paint.setColor(Color.WHITE);
                m_Paint.setTextAlign(Paint.Align.CENTER);
                m_Paint.setTextSize(32 * scale);
                m_Canvas.drawText("YOU DEFENDED THESIS!", m_ScreenWidth / 2, m_ScreenHeight / 2 - 50, m_Paint);

                m_Paint.setTextSize(20 * scale);
                m_Canvas.drawText("(Click anywhere to restart level)", m_ScreenWidth / 2, m_ScreenHeight / 2, m_Paint);
            }
            else if(gameOver){
                m_Paint.setARGB(60, 0,0,0);
                m_Canvas.drawRect(0, 0, m_ScreenWidth, m_ScreenHeight, m_Paint);

                m_Paint.setColor(Color.WHITE);
                m_Paint.setTextAlign(Paint.Align.CENTER);
                m_Paint.setTextSize(32 * scale);
                m_Canvas.drawText("FAILED TO DEFEND YOUR THESIS!", m_ScreenWidth / 2, m_ScreenHeight / 2 - 50, m_Paint);

                m_Paint.setTextSize(20 * scale);
                m_Canvas.drawText("(Click anywhere to restart level)", m_ScreenWidth / 2, m_ScreenHeight / 2, m_Paint);
            }
            // if paused, draw the pause screen
            else if(!m_Playing) {
                m_Paint.setARGB(60, 0,0,0);
                m_Canvas.drawRect(0, 0, m_ScreenWidth, m_ScreenHeight, m_Paint);

                m_Paint.setColor(Color.WHITE);
                m_Paint.setTextAlign(Paint.Align.CENTER);
                m_Paint.setTextSize(32 * scale);
                m_Canvas.drawText("PAUSED", m_ScreenWidth / 2, m_ScreenHeight / 2 - 50, m_Paint);


                m_Paint.setTextSize(20 * scale);
                m_Canvas.drawText("(Click anywhere to continue)", m_ScreenWidth / 2, m_ScreenHeight / 2, m_Paint);
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

            timePassedPerWave += MILLIS_IN_A_SECOND / FPS;
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
                    if(ally.getCurrentHealth() <= 0){
                        allyMap[y][x] = null;
                    }
                    else {
                        m_Score += ally.updateAlly(enemies, m_BlockSize);
                    }
                }
            }
        }

        for(int y = 0; y < enemies.size(); y++){
            Enemy enemy = enemies.get(y);
            if(enemy.isDead()){
                enemies.remove(enemy);
            }
            else {
                enemy.updateEnemy(allyMap, m_BlockSize);
            }
        }


        for(int j = 0; j < enemySpawnTime[currentWave].size(); j++){
            long spawn = enemySpawnTime[currentWave].get(j);
            if(spawn <= timePassedPerWave){
                Random rand = new Random();
                this.summonEnemy(rand.nextInt(5));
                enemySpawnTime[currentWave].remove((int) j);
                if(currentWave == enemySpawnTime.length - 1) {
                    winCon = true;
                }
            }
        }

        for(int i = 0; i < enemies.size(); i ++){
            Enemy enemy = enemies.get(i);
            if(enemy.getPosX() <= map[enemy.getLane()][0].x - m_BlockSize + m_BlockSize / 2){
                gameOver = true;
                m_Playing = false;
                drawGame();
                break;
            }
        }
        if(enemySpawnTime[currentWave].size() == 0 && enemies.size() == 0 && currentWave < enemySpawnTime.length - 1) {
            currentWave++;
            timePassedPerWave = 0;
        }

        else if(currentWave == maxWave - 1 && enemies.size() == 0 && winCon){
            Log.e(TAG, "WINNER!");
            winner = true;
            //m_Playing = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(winner){
                    Intent intent = new Intent(this.getContext(), Level.class);
                    this.getContext().startActivity(intent);
                }
                else if(gameOver){
                    Intent intent = new Intent(this.getContext(), Level.class);
                    this.getContext().startActivity(intent);
                }
                else if(!m_Playing){
                    m_Playing = true;
                    resume();
                }
                else if(motionEvent.getX() >= Math.round(40 * scale) &&
                        motionEvent.getX() <= Math.round(120 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 100){ // ummm if you touch the wizard icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Wizard";
                }
                else if(motionEvent.getX() >= Math.round(130 * scale) &&
                        motionEvent.getX() <= Math.round(200 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 50){ // pressed the warrior icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Warrior";
                }

                else if(motionEvent.getX() >= Math.round(580 * scale) &&
                        motionEvent.getX() <= Math.round(610 * scale) &&
                        motionEvent.getY() >= Math.round(20 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale)){
                    pause();
                    drawGame();
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
                            motionEvent.getY() <= map[y][x].y + m_NumBlocksHigh && // if user released inside map
                            isSelecting && allyMap[y][x] == null){ // and no one's occupying the spot
                            // insert whoever's selected
                            if(selectedAlly.equals("Wizard")) {
                                allyMap[y][x] = new Wizard(map[y][x].x, map[y][x].y, x, y, bitmapWizard, scale);
                                m_Score -= 100;
                            }
                            if(selectedAlly.equals("Warrior")) {
                                allyMap[y][x] = new Warrior(map[y][x].x, map[y][x].y, x, y, bitmapWarrior, scale);
                                m_Score -= 50;
                            }
                        }
                    }
                }
                isSelecting = false;
                break;
        }
        return true;
    }

    // needed for big images to avoid OutOfMemoryException
    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight){

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void summonEnemy(int lane){ //Base index 0, until 4??
        Enemy panel = new Panelist(map[lane][7].x+m_BlockSize*2, map[lane][7].y,lane,bitmapWarrior,scale);
        enemies.add(panel);
    }
}
