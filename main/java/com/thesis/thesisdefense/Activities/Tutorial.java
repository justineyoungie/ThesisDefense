package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thesis.thesisdefense.Misc.Dialog;
import com.thesis.thesisdefense.Models.*;
import com.thesis.thesisdefense.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by justine on 3/5/18.
 */

public class Tutorial extends SurfaceView implements Runnable {

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

    // What is the screen resolution
    public static int m_ScreenWidth;
    private int m_ScreenHeight;

    // Control pausing between updates
    private long m_NextFrameTime;
    // Update the game 10 times per second
    private long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_IN_A_SECOND = 1000;
    // We will draw the frame much more often

    //pause of attack animation for wizard in milliseconds
    //to be set in updateGame()

    // The current m_Score
    private int m_Score;



    /*// The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;*/

    private Bitmap bitmapWizard;
    private Bitmap bitmapBackground;
    private Bitmap bitmapWizardIcon;
    private Bitmap bitmapWarrior;
    private Bitmap bitmapWarriorIcon;
    private Bitmap bitmapThesis;
    private Bitmap bitmapCastle;
    private Bitmap bitmapEnemy;
    private Bitmap bitmapMageProjectile;

    /*
        For drawing allies to the map
     */
    private boolean isSelecting;
    private String selectedAlly = "";

    // private Ally selectedAlly;
    private Point cursorLocation = new Point();

    private Point[][] map;

    /*
        Not yet implemented but needed
     */
    private Ally[][] allyMap;


    float scale = getResources().getDisplayMetrics().density;


    // The size in pixels of one tile
    public static int m_BlockSize;
    private int m_NumBlocksHigh; // determined dynamically

    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> killedEnemies;
    private MediaPlayer player;
    private int[] enemyCount;
    private ArrayList<Long>[] enemySpawnTime;
    private int timePassedPerWave = 0;
    private boolean winner = false;
    private boolean gameOver = false;
    private boolean winCon = false;

    public ArrayList<String> dialogtexts = new ArrayList<>();
    private Dialog dialog;

    private int waitTime = 0; //Delay variable, if 0 delay is over
    private int scene = 0; //What scene are we on?
    private int dialogTextSize = 50;


    //Variables for Tutorial//
        private boolean tutorialPass1;
        private boolean canSwitchScene = true;
        private  boolean tutorialPass2;
    /////
    public Tutorial(Context context, Point size) {
        super(context);
        m_context = context;

        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / 10;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / 6;

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();


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
        // initializing resources

        bitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);

        // wizard
        bitmapWizard = decodeSampleBitmapFromResource(this.getResources(), R.drawable.mage, 250, 250);
        bitmapWizardIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.mage_icon);

        bitmapWarrior = decodeSampleBitmapFromResource(this.getResources(), R.drawable.knight, 250, 250);
        bitmapWarriorIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.knight_icon);

        bitmapEnemy = decodeSampleBitmapFromResource(this.getResources(), R.drawable.enemy_knight, 250, 250);

        map = new Point[5][8];

        enemies = new ArrayList<Enemy>();
        bitmapCastle = decodeSampleBitmapFromResource(this.getResources(), R.drawable.castle, 150, m_ScreenHeight / 2);
        bitmapThesis = BitmapFactory.decodeResource(this.getResources(), R.drawable.thesis);

        bitmapMageProjectile = BitmapFactory.decodeResource(this.getResources(), R.drawable.mage_projectile);


        // reset everything
        allyMap = new Ally[5][8];
        timePassedPerWave = 0;
        gameOver = false;
        winner = false;
        killedEnemies = new ArrayList<Enemy>();

        // Load the points of each tile
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[y].length; x++){
                map[y][x] = new Point(m_BlockSize * (x + 2),m_NumBlocksHigh * (y + 1));
            }
        }

        // temporary instantiation
        Wizard wizard = new Wizard(0, 0, 0, 0, bitmapWizard, bitmapMageProjectile, scale,m_BlockSize,m_ScreenWidth);
        wizard = null;
        Warrior warrior = new Warrior(0,0,0,0, bitmapWarrior, scale);
        warrior = null;
        // Reset the m_Score
        m_Score = 50;

        // initialize the spawn times


        /*

        allyMap[4][3] = new Warrior(map[4][3].x, map[4][3].y,
                3, 4,
                bitmapWarrior, scale);
        allyMap[3][3] = new Warrior(map[3][3].x, map[3][3].y,
                3, 3,
                bitmapWarrior, scale);
        allyMap[2][3] = new Warrior(map[2][3].x, map[2][3].y,
                3, 2,
                bitmapWarrior, scale);
        allyMap[1][3] = new Warrior(map[1][3].x, map[1][3].y,
                3, 1,
                bitmapWarrior, scale);
        allyMap[0][3] = new Warrior(map[0][3].x, map[0][3].y,
                3, 0,
                bitmapWarrior, scale);

        */
        // for now, manually add each time to the arraylists
        /*
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
        */
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


            m_Paint.reset();
            //Draw enemies
            for(int y = 0; y < enemies.size(); y++){
                Enemy enemy = enemies.get(y);
                if(enemy.isBeingDamaged()){
                    m_Paint.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00999999));
                }
                else
                    m_Paint.reset();
                src = new Rect((enemy.getCurrentFrame().x - enemy.getIncrementX()), enemy.getCurrentFrame().y - enemy.getIncrementY(), enemy.getCurrentFrame().x, enemy.getCurrentFrame().y);
                dst = new Rect(enemy.getPosX(), enemy.getPosY() - 50,
                        enemy.getPosX()+m_BlockSize + 80,
                        enemy.getPosY()+m_NumBlocksHigh);
                m_Canvas.drawBitmap(enemy.getImage(), src, dst, m_Paint);
            }

            //Draw the ally
            for(int y = 0; y < allyMap.length; y++){
                for(int x = map[y].length - 1; x >= 0; x--){
                    if(allyMap[y][x] != null){
                        Ally ally = allyMap[y][x];
                        if(ally.isBeingDamaged()){
                            m_Paint.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00222222));
                        }
                        else
                            m_Paint.reset();
                        src = new Rect((ally.getCurrentFrame().x - ally.getIncrementX()), ally.getCurrentFrame().y - ally.getIncrementY(), ally.getCurrentFrame().x, ally.getCurrentFrame().y);
                        dst = new Rect( map[y][x].x,
                                map[y][x].y - 50,
                                map[y][x].x + m_BlockSize + 90,
                                map[y][x].y + m_NumBlocksHigh);
                        m_Canvas.drawBitmap(ally.getImage(), src, dst, m_Paint);

                        if(ally instanceof Wizard){
                            ArrayList<Projectile> projectiles = ((Wizard) ally).getProjectiles();
                            for(int i = 0; i < projectiles.size(); i++){
                                Projectile proj = projectiles.get(i);
                                src = new Rect(0, 0, bitmapMageProjectile.getWidth(), bitmapMageProjectile.getHeight());
                                dst = new Rect(proj.getPosX(), proj.getPosY(), (int)(proj.getPosX() + 50 * scale), (int)(proj.getPosY() + 50 * scale));
                                m_Canvas.drawBitmap(bitmapMageProjectile, src, dst, null);
                            }
                        }
                    }
                }
            }

            // Draw score gotten from dead enemies if applicable
            for(int x = 0; x < killedEnemies.size(); x++){
                Enemy killedEnemy = killedEnemies.get(x);
                m_Paint.setTextSize(24);
                m_Paint.setColor(Color.rgb(255, 255, 255));
                m_Canvas.drawText("+" + killedEnemy.getScore(), killedEnemy.getPosX() + (m_BlockSize + 40) / 2, killedEnemy.getPosY() - 30, m_Paint);
            }

            m_Paint.reset();

            // Choose how big the score will be
            m_Paint.setTextSize(30);
            m_Paint.setColor(Color.rgb(255, 255, 255));
            m_Canvas.drawText("Coins: " + m_Score, (int) 460 * scale, (int)40 * scale, m_Paint);


            m_Canvas.drawText("Tutorial: " + (scene) + "/5 ", (int) 380 * scale, (int)40 * scale, m_Paint);

            m_Paint.reset();
            //pause button
            Bitmap pause = BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_media_pause);
            src = new Rect(0, 0, pause.getWidth(), pause.getHeight());
            dst = new Rect( Math.round(580 * scale),
                    Math.round(20 * scale),
                    Math.round(610 * scale),
                    Math.round(50 * scale));
            m_Canvas.drawBitmap(pause, src, dst, m_Paint);

            //fast forward button
            Bitmap ff = BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_media_ff);
            src = new Rect(0, 0, pause.getWidth(), pause.getHeight());
            dst = new Rect( Math.round(540 * scale),
                    Math.round(20 * scale),
                    Math.round(570 * scale),
                    Math.round(50 * scale));
            if(FPS == 30)
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
            m_Canvas.drawBitmap(ff, src, dst, m_Paint);

            m_Paint.reset();
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

            if(isSelecting && selectedAlly.equals("Warrior") || m_Score < 50 || scene == 5 || scene == 4){
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
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 90) / 2, // to center the image on the cursor
                            cursorLocation.y - (m_NumBlocksHigh) / 2 - 25,
                            cursorLocation.x + (m_BlockSize + 90) / 2,
                            cursorLocation.y + (m_NumBlocksHigh) / 2 + 25);
                }
                else if(selectedAlly.equals("Warrior")){
                    src = new Rect(0, 0, Warrior.FRAME_WIDTH, Warrior.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 90) / 2, // to center the image on the cursor
                            cursorLocation.y - m_NumBlocksHigh / 2 - 25,
                            cursorLocation.x + (m_BlockSize + 90) / 2,
                            cursorLocation.y + m_NumBlocksHigh / 2 + 25);
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
                                    map[y][x].y - 50,
                                    map[y][x].x + m_BlockSize + 90,
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

            if(dialogtexts != null) {
                dialog = new Dialog(m_Canvas.getWidth(), m_Canvas.getHeight(), scale);
                dialog.drawDialog(m_Canvas, dialogtexts, m_Paint, dialogTextSize);
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

        // update each ally; next frame, attack or remove when dead
        for(int y = 0; y < allyMap.length; y++){
            for(int x = 0; x < allyMap[y].length; x++){
                if(allyMap[y][x] != null){
                    Ally ally = allyMap[y][x];
                    if(ally.getCurrentHealth() <= 0){
                        allyMap[y][x] = null;
                    }
                    else {
                        // update ally is immediate damage
                        m_Score += ally.updateAlly(enemies, m_BlockSize);

                        // check contact is for projectiles with damage
                        if(ally instanceof Wizard){
                            for(int i = 0; i < ((Wizard) ally).getProjectiles().size(); i++){
                                Projectile proj = ((Wizard) ally).getProjectiles().get(i);
                                if(proj.hasEncountered()){
                                    ((Wizard) ally).removeProjectile(proj);
                                }
                                else
                                    m_Score += proj.checkContact(enemies);
                            }
                        }

                    }
                }
            }
        }

        // update each enemy; next frame, attack or remove when dead
        for(int y = 0; y < enemies.size(); y++){
            Enemy enemy = enemies.get(y);
            if(enemy.isDead()){
                //killedEnemies.add(enemy);
                enemies.remove(enemy);

            }
            else {
                enemy.updateEnemy(allyMap);
            }
        }

/*
        // updates killed enemies; removes those who have passed the scoreDisplayLength
        for(int x = 0; x < killedEnemies.size(); x ++){
            Enemy enemy = killedEnemies.get(x);
            if(enemy.checkDisplayScore()){ // if exceeding the scoreDisplayLength
                killedEnemies.remove(enemy); // remove enemy from killedEnemies
            }
        }
*/

        // spawns enemies; winCon is set when each wave is done
        //this.summonEnemy(rand.nextInt(5));


        for(int i = 0; i < enemies.size(); i ++){
            Enemy enemy = enemies.get(i);
            if(enemy.getPosX() <= map[enemy.getLane()][0].x - m_BlockSize + m_BlockSize / 2){
                gameOver = true;
                m_Playing = false;
                drawGame();
                break;
            }
        }

        if(waitTime > 0) {
            waitTime-=1;
        }
        else{
            if(canSwitchScene) {
                setScene();

            }
            if(waitTime <= 0) {
                updateScene();
            }

        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(winner){
                    this.destroy();
                    startGame();
                }
                else if(gameOver){
                    this.destroy();
                    startGame();
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
                        m_Score >= 50 &&
                        (scene != 5 && scene != 4)){ // pressed the warrior icon
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

                else if(motionEvent.getX() >= Math.round(540 * scale) &&
                        motionEvent.getX() <= Math.round(570 * scale) &&
                        motionEvent.getY() >= Math.round(20 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale)){
                    if(FPS == 30)
                        FPS = 10;
                    else
                        FPS = 30;
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
                                allyMap[y][x] = new Wizard(map[y][x].x, map[y][x].y, x, y, bitmapWizard, bitmapMageProjectile, scale,m_BlockSize,m_ScreenWidth);
                                m_Score -= 100;
                            }
                            else if(selectedAlly.equals("Warrior")) {
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

    public void prepareWave(int density){

    }

    public void summonEnemy(int lane){ //Base index 0, until 4??
        Enemy panel = new Panelist(map[lane][7].x+m_BlockSize*2, map[lane][7].y,lane,bitmapEnemy,scale,m_BlockSize);
        enemies.add(panel);
    }


    public void destroy(){
        bitmapWizard.recycle();
        bitmapBackground.recycle();
        bitmapWizardIcon.recycle();
        bitmapWarrior.recycle();
        bitmapWarriorIcon.recycle();
        bitmapThesis.recycle();
        bitmapCastle.recycle();
        bitmapEnemy.recycle();
        bitmapWizard = null;
        bitmapBackground = null;
        bitmapWizardIcon = null;
        bitmapWarrior = null;
        bitmapWarriorIcon = null;
        bitmapThesis = null;
        bitmapCastle = null;
        bitmapEnemy = null;
    }
    public void setVariables(){
        dialogtexts = null;
        waitTime = 0;
        scene = 0;
        dialogTextSize = 50;
        canSwitchScene = true;
        tutorialPass1 = false;
        tutorialPass2 = false;
    }
    public void setScene(){
        canSwitchScene = false;
        scene++;
        if(scene == 1){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("Hello there! I see you are here to defend your thesis");
            dialogtexts.add("First off to defend your thesis, You have to form a good team");
            dialogtexts.add("Drag an drop units from the upper left screen to the field to prepare to defend your thesis");
            dialogTextSize = 30;

        }
        else if(scene == 2){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("Here comes the panelists!");
            waitTime = 20;
        }
        else if(scene == 3){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("One of your team is about to face the panel");
            dialogtexts.add("Get ready!");
            for(int i = 0 ; i < allyMap.length; i++){
                for(int j = 0; j < allyMap[i].length; j++){
                    if(allyMap[i][j] != null){
                        summonEnemy(i);
                    }
                }
            }
            waitTime = 20;
        }
        else if(scene == 4){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("Congratulations!");
            dialogtexts.add("Your team has defended your thesis from a panel!");
            m_Score = 100;
            waitTime = 20;
        }
        else if(scene == 5){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("Lets have more groupmates to help");
            dialogtexts.add("with our documentation");
        }
        else if(scene == 6){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("More panelists are comming");
            waitTime = 30;
        }
        else if(scene == 7){
            dialogtexts = new ArrayList<>();
            dialogtexts.add("One of your team is about to face the panel");
            dialogtexts.add("Get ready!");
            for(int i = 0 ; i < allyMap.length; i++){
                for(int j = 0; j < allyMap[i].length; j++){
                    if(allyMap[i][j] != null){
                        summonEnemy(i);
                    }
                }
            }
            waitTime = 40;
        }
    }
    public void updateScene(){
        if(scene == 1){
            if(!tutorialPass1) {
                for (int i = 0; i < allyMap.length; i++) {
                    for (int j = 0; j < allyMap[i].length; j++) {
                        if (allyMap[i][j] != null) {
                            tutorialPass1 = true;
                        }
                    }
                }

                if (tutorialPass1) {
                    dialogtexts = null;
                    waitTime = 30;

                }
            }
            else{
                canSwitchScene = true;
            }
        }
        else if(scene == 2){
                dialogtexts = null;
                canSwitchScene = true;
        }
        else if(scene == 3){
            dialogtexts = null;

            if(enemies.size() == 0){
               canSwitchScene = true;
            }
        }
        else if(scene == 4){
            dialogtexts = null;
            canSwitchScene = true;
        }
        else if(scene == 5){
            if(!tutorialPass2) {
                int count = 0;
                for (int i = 0; i < allyMap.length; i++) {
                    for (int j = 0; j < allyMap[i].length; j++) {
                        if (allyMap[i][j] != null) {
                            count++;
                            if(count == 2)
                                tutorialPass2 = true;
                        }
                    }
                }

                if (tutorialPass2) {
                    dialogtexts = null;
                    waitTime = 30;

                }
            }
            else{
                canSwitchScene = true;
            }
        }
        else if(scene == 6){
            dialogtexts = null;
            canSwitchScene = true;
        }
        else if(scene == 7){
            dialogtexts = null;

            if(enemies.size() == 0){
                canSwitchScene = true;
            }
        }

    }
}
