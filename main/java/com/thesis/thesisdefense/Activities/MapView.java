package com.thesis.thesisdefense.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thesis.thesisdefense.DatabaseHelpers.GameDBhelper;
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

    private int currentWave = 0;
    private int maxWave;

    /*// The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;*/

    private Bitmap bitmapWizard;
    private Bitmap bitmapBackground;
    private Bitmap bitmapWizardIcon;
    private Bitmap bitmapWarrior;
    private Bitmap bitmapWarriorIcon;
    private Bitmap bitmapArcher;
    private Bitmap bitmapArcherIcon;
    private Bitmap bitmapSpearman;
    private Bitmap bitmapSpearmanIcon;
    private Bitmap bitmapThesis;
    private Bitmap bitmapCastle;
    private Bitmap bitmapEnemy;
    private Bitmap bitmapEnemyMage;
    private Bitmap bitmapEnemyProjectile;
    private Bitmap bitmapMageProjectile;
    private Bitmap bitmapArcherProjectile;
    private Bitmap bitmapSpells;

    private Bitmap bitmapFire;
    private Bitmap bitmapIce;
    private Bitmap bitmapThunder;

    private Bitmap bitmapFireAsset;
    private Bitmap bitmapIceAsset;
    private Bitmap bitmapThunderAsset;

    private Bitmap bitmapCoin;

    /*
        For drawing allies to the map
     */
    private boolean isSelecting;
    private String selectedAlly = "";

    // private Ally selectedAlly;
    private float startX;
    private float startY;
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
    private MediaPlayer player;
    private int[] enemyCount;
    private ArrayList<Integer>[] enemySpawnTime;
    private int timePassedPerWave = 0;
    private boolean winner = false;
    private boolean gameOver = false;
    private boolean winCon = false;

    private int spellY = -10;

    private ArrayList<int[]> killedEnemies;

    private String spellActivated = "";
    private ArrayList<Spell> spellsActive;

    private String difficulty;

    private boolean fastforward = false;
    private GameDBhelper dBhelper;
    private String name;

    public MapView(Context context, Point size,String difficulty, GameDBhelper dbhelper) {
        super(context);
        m_context = context;

        this.difficulty = difficulty;
        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / 10;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / 6;

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();


        bitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);

        // wizard
        bitmapWizard = decodeSampleBitmapFromResource(this.getResources(), R.drawable.mage, 250, 250);
        bitmapWizardIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.mage_icon);

        // warrior
        bitmapWarrior = decodeSampleBitmapFromResource(this.getResources(), R.drawable.knight, 250, 250);
        bitmapWarriorIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.knight_icon);

        // archer
        bitmapArcher = decodeSampleBitmapFromResource(this.getResources(), R.drawable.archer, 250, 250);
        bitmapArcherIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.archer_icon);

        // spearman
        bitmapSpearman = decodeSampleBitmapFromResource(this.getResources(), R.drawable.spearman, 250, 250);
        bitmapSpearmanIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.spear_icon);

        // enemy warrior
        bitmapEnemy = decodeSampleBitmapFromResource(this.getResources(), R.drawable.enemy_knight, 250, 250);
        // enemy mage
        bitmapEnemyMage = decodeSampleBitmapFromResource(this.getResources(), R.drawable.enemy_mage, 250, 250);

        bitmapCastle = decodeSampleBitmapFromResource(this.getResources(), R.drawable.castle, 150, 150);
        bitmapThesis = BitmapFactory.decodeResource(this.getResources(), R.drawable.thesis);

        bitmapMageProjectile = decodeSampleBitmapFromResource(this.getResources(), R.drawable.mage_projectile, 150, 150);
        bitmapArcherProjectile = decodeSampleBitmapFromResource(this.getResources(), R.drawable.archer_projectile, 150, 150);
        bitmapEnemyProjectile = decodeSampleBitmapFromResource(this.getResources(), R.drawable.enemy_projectile, 150, 150);
        bitmapSpells = decodeSampleBitmapFromResource(this.getResources(), R.drawable.spells, 100, 100);

        bitmapFire = BitmapFactory.decodeResource(this.getResources(), R.drawable.spell_fire);
        bitmapIce = BitmapFactory.decodeResource(this.getResources(), R.drawable.spell_ice);
        bitmapThunder = BitmapFactory.decodeResource(this.getResources(), R.drawable.spell_thunder);

        this.dBhelper = dbhelper;
        bitmapFireAsset = BitmapFactory.decodeResource(this.getResources(), R.drawable.fire);
        bitmapIceAsset = BitmapFactory.decodeResource(this.getResources(), R.drawable.snowstorm);
        bitmapThunderAsset = BitmapFactory.decodeResource(this.getResources(), R.drawable.thunder);

        bitmapCoin = decodeSampleBitmapFromResource(this.getResources(), R.drawable.coin, 100, 100);


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
            try {
                if(!fastforward) {
                    Thread.sleep(100);
                    timePassedPerWave += 100;
                }
                else{
                    Thread.sleep(30);
                    timePassedPerWave += 30;
                }
                //if(checkForUpdate()) {
                updateGame();
                if(m_Playing)
                    drawGame();
                //}
            }
            catch (Exception e){

            }
        }
    }

    public void pause() {
        m_Playing = false;
        try {
            m_Thread.join();
        } catch (InterruptedException e) {
            // Error
        } catch (NullPointerException ex){
            // Error
        }

        player.pause();
        if(!winner)
            drawGame();
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();
        player.start();

    }

    public void startGame() {
        // ini
        // reset everything
        currentWave = 0;
        allyMap = new Ally[5][8];
        //initializing resources

        map = new Point[5][8];

        enemies = new ArrayList<>();
        timePassedPerWave = 0;
        gameOver = false;
        winner = false;
        killedEnemies = new ArrayList<>();
        spellsActive = new ArrayList<>();

        // Load the points of each tile
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[y].length; x++){
                map[y][x] = new Point(m_BlockSize * (x + 2),m_NumBlocksHigh * (y + 1));
            }
        }

        // temporary instantiation
        new Wizard(0, 0, 0, 0, bitmapWizard, bitmapMageProjectile, scale,m_BlockSize,m_ScreenWidth);
        new Warrior(0, 0, 0, 0, bitmapWarrior, scale);
        new Archer(0, 0, 0, 0, bitmapArcher, bitmapMageProjectile, scale,m_BlockSize,m_ScreenWidth);
        new Spearman(0, 0, 0, 0, bitmapSpearman, scale);
/*
        //read
        Cursor cursor = dBhelper.getAllData();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                m_Score = cursor.getInt(3);
            }
        }
*/

        // initialize the spawn times
        for(int i = 0; i < maxWave; i ++){
            enemySpawnTime[i] = new ArrayList<Integer>();
        }
        switch(difficulty){
            case "Easy":
                maxWave = 3;
                enemyCount = new int[maxWave];
                enemySpawnTime = new ArrayList[maxWave];

                // Reset the m_Score
                m_Score = 150;

                // initialize the spawn times
                for(int i = 0; i < maxWave; i ++){
                    enemySpawnTime[i] = new ArrayList<Integer>();
                }

                // for now, manually add each time to the arraylists
                enemyCount[0] = 3;
                enemyCount[1] = 5;
                enemyCount[2] = 8;

                enemySpawnTime[0].add(10000);
                enemySpawnTime[0].add(18000);
                enemySpawnTime[0].add(24000);

                enemySpawnTime[1].add(0);
                enemySpawnTime[1].add(2000);
                enemySpawnTime[1].add(7000);
                enemySpawnTime[1].add(12000);
                enemySpawnTime[1].add(14000);

                enemySpawnTime[2].add(2000);
                enemySpawnTime[2].add(6000);
                enemySpawnTime[2].add(10000);
                enemySpawnTime[2].add(11000);
                enemySpawnTime[2].add(16000);
                enemySpawnTime[2].add(21000);
                enemySpawnTime[2].add(23000);
                enemySpawnTime[2].add(25000);
                break;


            case "Moderate":
                maxWave = 5;
                enemyCount = new int[maxWave];
                enemySpawnTime = new ArrayList[maxWave];

                // Reset the m_Score
                m_Score = 250;

                // initialize the spawn times
                for(int i = 0; i < maxWave; i ++){
                    enemySpawnTime[i] = new ArrayList<Integer>();
                }

                // for now, manually add each time to the arraylists
                enemyCount[0] = 3;
                enemyCount[1] = 5;
                enemyCount[2] = 8;
                enemyCount[3] = 12;
                enemyCount[4] = 15;

                enemySpawnTime[0].add(10000);
                enemySpawnTime[0].add(18000);
                enemySpawnTime[0].add(24000);

                enemySpawnTime[1].add(0);
                enemySpawnTime[1].add(2000);
                enemySpawnTime[1].add(7000);
                enemySpawnTime[1].add(12000);
                enemySpawnTime[1].add(14000);

                enemySpawnTime[2].add(2000);
                enemySpawnTime[2].add(6000);
                enemySpawnTime[2].add(10000);
                enemySpawnTime[2].add(11000);
                enemySpawnTime[2].add(16000);
                enemySpawnTime[2].add(21000);
                enemySpawnTime[2].add(23000);
                enemySpawnTime[2].add(25000);

                enemySpawnTime[3].add(4000);
                enemySpawnTime[3].add(7000);
                enemySpawnTime[3].add(10000);
                enemySpawnTime[3].add(11000);
                enemySpawnTime[3].add(16000);
                enemySpawnTime[3].add(20000);
                enemySpawnTime[3].add(23000);
                enemySpawnTime[3].add(25000);
                enemySpawnTime[3].add(26000);
                enemySpawnTime[3].add(32000);
                enemySpawnTime[3].add(32500);
                enemySpawnTime[3].add(36000);

                enemySpawnTime[4].add(4000);
                enemySpawnTime[4].add(7000);
                enemySpawnTime[4].add(10000);
                enemySpawnTime[4].add(11000);
                enemySpawnTime[4].add(16000);
                enemySpawnTime[4].add(20000);
                enemySpawnTime[4].add(23000);
                enemySpawnTime[4].add(25000);
                enemySpawnTime[4].add(26000);
                enemySpawnTime[4].add(32000);
                enemySpawnTime[4].add(32500);
                enemySpawnTime[4].add(36000);
                break;
            case "Difficult":
                maxWave = 8;
                enemyCount = new int[maxWave];
                enemySpawnTime = new ArrayList[maxWave];

                // Reset the m_Score
                m_Score = 300;

                // initialize the spawn times
                for(int i = 0; i < maxWave; i ++){
                    enemySpawnTime[i] = new ArrayList<Integer>();
                }

                // for now, manually add each time to the arraylists
                enemyCount[0] = 3;
                enemyCount[1] = 5;
                enemyCount[2] = 8;
                enemyCount[3] = 12;
                enemyCount[4] = 15;
                enemyCount[5] = 15;
                enemyCount[6] = 20;
                enemyCount[7] = 30;

                enemySpawnTime[0].add(10000);
                enemySpawnTime[0].add(18000);
                enemySpawnTime[0].add(24000);

                enemySpawnTime[1].add(0);
                enemySpawnTime[1].add(2000);
                enemySpawnTime[1].add(7000);
                enemySpawnTime[1].add(12000);
                enemySpawnTime[1].add(14000);

                enemySpawnTime[2].add(2000);
                enemySpawnTime[2].add(6000);
                enemySpawnTime[2].add(10000);
                enemySpawnTime[2].add(11000);
                enemySpawnTime[2].add(16000);
                enemySpawnTime[2].add(21000);
                enemySpawnTime[2].add(23000);
                enemySpawnTime[2].add(25000);

                enemySpawnTime[3].add(4000);
                enemySpawnTime[3].add(7000);
                enemySpawnTime[3].add(10000);
                enemySpawnTime[3].add(11000);
                enemySpawnTime[3].add(16000);
                enemySpawnTime[3].add(20000);
                enemySpawnTime[3].add(23000);
                enemySpawnTime[3].add(25000);
                enemySpawnTime[3].add(26000);
                enemySpawnTime[3].add(32000);
                enemySpawnTime[3].add(32500);
                enemySpawnTime[3].add(36000);

                enemySpawnTime[4].add(4000);
                enemySpawnTime[4].add(7000);
                enemySpawnTime[4].add(10000);
                enemySpawnTime[4].add(11000);
                enemySpawnTime[4].add(16000);
                enemySpawnTime[4].add(20000);
                enemySpawnTime[4].add(23000);
                enemySpawnTime[4].add(25000);
                enemySpawnTime[4].add(26000);
                enemySpawnTime[4].add(32000);
                enemySpawnTime[4].add(32500);
                enemySpawnTime[4].add(36000);
                enemySpawnTime[4].add(40000);
                enemySpawnTime[4].add(41000);
                enemySpawnTime[4].add(41500);


                enemySpawnTime[5].add(0);
                enemySpawnTime[5].add(2000);
                enemySpawnTime[5].add(7000);
                enemySpawnTime[5].add(12000);
                enemySpawnTime[5].add(14000);
                enemySpawnTime[5].add(16000);
                enemySpawnTime[5].add(20000);
                enemySpawnTime[5].add(23000);
                enemySpawnTime[5].add(25000);
                enemySpawnTime[5].add(26000);
                enemySpawnTime[5].add(32000);
                enemySpawnTime[5].add(32500);
                enemySpawnTime[5].add(36000);
                enemySpawnTime[5].add(40000);
                enemySpawnTime[5].add(41000);

                enemySpawnTime[6].add(2000);
                enemySpawnTime[6].add(6000);
                enemySpawnTime[6].add(10000);
                enemySpawnTime[6].add(11000);
                enemySpawnTime[6].add(16000);
                enemySpawnTime[6].add(21000);
                enemySpawnTime[6].add(23000);
                enemySpawnTime[6].add(25000);
                enemySpawnTime[6].add(26000);
                enemySpawnTime[6].add(32000);
                enemySpawnTime[6].add(32500);
                enemySpawnTime[6].add(36000);
                enemySpawnTime[6].add(40000);
                enemySpawnTime[6].add(41000);
                enemySpawnTime[6].add(41500);
                enemySpawnTime[6].add(42000);
                enemySpawnTime[6].add(45000);
                enemySpawnTime[6].add(45500);
                enemySpawnTime[6].add(47000);
                enemySpawnTime[6].add(51000);

                enemySpawnTime[7].add(2000);
                enemySpawnTime[7].add(6000);
                enemySpawnTime[7].add(10000);
                enemySpawnTime[7].add(11000);
                enemySpawnTime[7].add(16000);
                enemySpawnTime[7].add(21000);
                enemySpawnTime[7].add(23000);
                enemySpawnTime[7].add(25000);
                enemySpawnTime[7].add(26000);
                enemySpawnTime[7].add(32000);
                enemySpawnTime[7].add(32500);
                enemySpawnTime[7].add(36000);
                enemySpawnTime[7].add(40000);
                enemySpawnTime[7].add(41000);
                enemySpawnTime[7].add(41500);
                enemySpawnTime[7].add(42000);
                enemySpawnTime[7].add(45000);
                enemySpawnTime[7].add(45500);
                enemySpawnTime[7].add(47000);
                enemySpawnTime[7].add(51000);
                enemySpawnTime[7].add(52000);
                enemySpawnTime[7].add(52600);
                enemySpawnTime[7].add(57000);
                enemySpawnTime[7].add(61000);
                enemySpawnTime[7].add(61500);
                enemySpawnTime[7].add(63000);
                enemySpawnTime[7].add(65000);
                enemySpawnTime[7].add(70000);
                enemySpawnTime[7].add(70100);
                enemySpawnTime[7].add(71000);


                break;
        }


        resume();
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
                    mp.start();
                }
            });
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
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
            // m_Canvas.drawColor(Color.argb(255, 51, 181, 229));

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
            dst = new Rect((int)(-120 * scale), (int)(-80 * scale), (int) (60 * scale), m_ScreenHeight);
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
                        dst = new Rect( ally.getPosX() - ally.getAllowanceX(),
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
                        else if(ally instanceof Archer){
                            ArrayList<Projectile> projectiles = ((Archer) ally).getProjectiles();
                            for(int i = 0; i < projectiles.size(); i++){
                                Projectile proj = projectiles.get(i);
                                src = new Rect(0, 0, bitmapArcherProjectile.getWidth(), bitmapArcherProjectile.getHeight());
                                dst = new Rect(proj.getPosX(), proj.getPosY(), (int)(proj.getPosX() + 100 * scale), (int)(proj.getPosY() + 40 * scale));
                                m_Canvas.drawBitmap(bitmapArcherProjectile, src, dst, null);
                            }
                        }
                    }
                }
            }

            // Draw score gotten from dead enemies if applicable
            for(int x = 0; x < killedEnemies.size(); x++){
                int[] killedEnemy = killedEnemies.get(x);
                m_Paint.setTextSize(24);
                m_Paint.setColor(Color.rgb(255, 255, 255));
                m_Canvas.drawText("+" + killedEnemy[3], killedEnemy[0] + (m_BlockSize + 40) / 2, killedEnemy[1] - 30, m_Paint);
                m_Canvas.drawBitmap(bitmapCoin,
                                    new Rect(   0, 0, bitmapCoin.getWidth(), bitmapCoin.getHeight()),
                                    new Rect(   killedEnemy[0] + (m_BlockSize) / 2,
                                                killedEnemy[1] - 50,
                                                killedEnemy[0] + (m_BlockSize + 20) / 2,
                                                killedEnemy[1] - 40),
                                null);
            }

            m_Paint.reset();

            // Choose how big the score will be
            m_Paint.setTextSize(30);
            m_Paint.setColor(Color.rgb(255, 255, 255));
            m_Canvas.drawText("Coins: " + m_Score, (int) 460 * scale, (int)40 * scale, m_Paint);


            m_Canvas.drawText("Wave: " + (currentWave + 1) + "/" + maxWave, (int) 380 * scale, (int)40 * scale, m_Paint);

            m_Paint.reset();
            //pause button
            Bitmap pause = BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_media_pause);
            src = new Rect(0, 0, pause.getWidth(), pause.getHeight());
            dst = new Rect( Math.round(m_ScreenWidth - 50 * scale),
                            Math.round(20 * scale),
                            Math.round(m_ScreenWidth - 20 * scale),
                            Math.round(50 * scale));
            m_Canvas.drawBitmap(pause, src, dst, m_Paint);

            //fast forward button
            Bitmap ff = BitmapFactory.decodeResource(this.getResources(), android.R.drawable.ic_media_ff);
            src = new Rect(0, 0, pause.getWidth(), pause.getHeight());
            dst = new Rect( Math.round(m_ScreenWidth - 100 * scale),
                    Math.round(20 * scale),
                    Math.round(m_ScreenWidth - 70 * scale),
                    Math.round(50 * scale));
            if(fastforward)
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
            m_Canvas.drawBitmap(ff, src, dst, m_Paint);

            m_Paint.reset();

            // wizard icon for selection
            src = new Rect(0, 0, bitmapWizardIcon.getWidth(), bitmapWizardIcon.getHeight());
            dst = new Rect( Math.round(30*scale),
                            Math.round(10*scale),
                            Math.round(100*scale),
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
            dst = new Rect( Math.round(120*scale),
                            Math.round(10*scale),
                            Math.round(190*scale),
                            Math.round(50*scale));

            if(isSelecting && selectedAlly.equals("Warrior") || m_Score < 50){
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                m_Canvas.drawBitmap(bitmapWarriorIcon, src, dst, m_Paint);
            }
            else
                m_Canvas.drawBitmap(bitmapWarriorIcon, src, dst, m_Paint);


            m_Paint.reset();

            // archer icon for selection
            src = new Rect(0, 0, bitmapArcherIcon.getWidth(), bitmapArcherIcon.getHeight());
            dst = new Rect( Math.round(210*scale),
                    Math.round(10*scale),
                    Math.round(280*scale),
                    Math.round(50*scale));

            if(isSelecting && selectedAlly.equals("Archer") || m_Score < 75){
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                m_Canvas.drawBitmap(bitmapArcherIcon, src, dst, m_Paint);
            }
            else
                m_Canvas.drawBitmap(bitmapArcherIcon, src, dst, m_Paint);


            m_Paint.reset();

            // spearman icon for selection
            src = new Rect(0, 0, bitmapSpearmanIcon.getWidth(), bitmapSpearmanIcon.getHeight());
            dst = new Rect( Math.round(300*scale),
                            Math.round(10*scale),
                            Math.round(370*scale),
                            Math.round(50*scale));

            if(isSelecting && selectedAlly.equals("Spearman") || m_Score < 125){
                m_Paint.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                m_Canvas.drawBitmap(bitmapSpearmanIcon, src, dst, m_Paint);
            }
            else
                m_Canvas.drawBitmap(bitmapSpearmanIcon, src, dst, m_Paint);


            m_Paint.reset();

            m_Paint.setColor(Color.rgb(0,0,0));
            m_Paint.setTextSize(30);
            m_Paint.setTextAlign(Paint.Align.CENTER);
            // spell container
            src = new Rect( 0, 0, bitmapSpells.getWidth(), bitmapSpells.getHeight());
            dst = new Rect( (int) (m_ScreenWidth - 200 * scale),    (int) (m_ScreenHeight + spellY * scale),
                            (int) (m_ScreenWidth - 10 * scale),     (int) (m_ScreenHeight + (spellY + 110) * scale));
            m_Canvas.drawBitmap(bitmapSpells, src, dst, null);

            // spell fire
            src = new Rect(0, 0, bitmapFire.getWidth(), bitmapFire.getHeight());
            dst = new Rect( (int) (m_ScreenWidth - 190 * scale),    (int) (m_ScreenHeight + (spellY + 30) * scale),
                            (int) (m_ScreenWidth - 140 * scale),     (int) (m_ScreenHeight + (spellY + 80) * scale));
            m_Canvas.drawBitmap(bitmapFire, src, dst, null);

            m_Canvas.drawText("300", dst.centerX(), dst.bottom + 20, m_Paint);
            //spell ice
            // src = new Rect(0,0, bitmapIce.getWidth(), bitmapIce.getHeight());
            dst = new Rect( (int) (m_ScreenWidth - 130 * scale),    (int) (m_ScreenHeight + (spellY + 30) * scale),
                            (int) (m_ScreenWidth - 80 * scale),    (int) (m_ScreenHeight + (spellY + 80) * scale));
            m_Canvas.drawBitmap(bitmapIce, src, dst, null);

            m_Canvas.drawText("500", dst.centerX(), dst.bottom + 20, m_Paint);

            //spell thunder
            // src = new Rect(0,0, bitmapThunder.getWidth(), bitmapThunder.getHeight());
            dst = new Rect( (int) (m_ScreenWidth - 70 * scale),    (int) (m_ScreenHeight + (spellY + 30) * scale),
                            (int) (m_ScreenWidth - 20 * scale),    (int) (m_ScreenHeight + (spellY + 80) * scale));
            m_Canvas.drawBitmap(bitmapThunder, src, dst, null);

            m_Canvas.drawText("200", dst.centerX(), dst.bottom + 20, m_Paint);

            // set spells ablaze
            for(int i = 0; i < spellsActive.size(); i++){
                Spell spell = spellsActive.get(i);
                if(spell instanceof Fire){
                    m_Paint.setARGB(60, 230,50,50);
                    ArrayList<Point> affected = spell.getAreaOfEffect();
                    for(int j = 0; j < affected.size(); j ++){
                        Point block = affected.get(j);
                        m_Canvas.drawRect(  map[block.y][block.x].x,
                                            map[block.y][block.x].y,
                                            map[block.y][block.x].x + m_BlockSize,
                                            map[block.y][block.x].y + m_NumBlocksHigh,
                                            m_Paint);
                        m_Canvas.drawBitmap(bitmapFireAsset,
                                new Rect(0, 0, bitmapFireAsset.getWidth(), bitmapFireAsset.getHeight()),
                                new Rect(   map[block.y][block.x].x,
                                            map[block.y][block.x].y,
                                            map[block.y][block.x].x + m_BlockSize,
                                            map[block.y][block.x].y + m_NumBlocksHigh),
                                null);
                    }

                }
                else if(spell instanceof Thunder){
                    m_Paint.setARGB(60, 153, 0, 255);
                    ArrayList<Point> affected = spell.getAreaOfEffect();
                    for(int j = 0; j < affected.size(); j ++){
                        Point block = affected.get(j);
                        m_Canvas.drawRect(  map[block.y][block.x].x,
                                map[block.y][block.x].y,
                                map[block.y][block.x].x + m_BlockSize,
                                map[block.y][block.x].y + m_NumBlocksHigh,
                                m_Paint);

                        m_Canvas.drawBitmap(bitmapThunderAsset,
                                new Rect(0, 0, bitmapThunderAsset.getWidth(), bitmapThunderAsset.getHeight()),
                                new Rect(   map[block.y][block.x].x,
                                        map[block.y][block.x].y -  (int)(20 * scale),
                                        map[block.y][block.x].x + m_BlockSize,
                                        map[block.y][block.x].y + m_NumBlocksHigh),
                                null);
                    }
                }

                else if(spell instanceof Ice){
                    m_Paint.setARGB(60, 40, 43, 237);
                    ArrayList<Point> affected = spell.getAreaOfEffect();
                    for(int j = 0; j < affected.size(); j ++){
                        Point block = affected.get(j);
                        m_Canvas.drawRect(  map[block.y][block.x].x,
                                            map[block.y][block.x].y,
                                            map[block.y][block.x].x + m_BlockSize,
                                            map[block.y][block.x].y + m_NumBlocksHigh,
                                            m_Paint);

                    }
                    m_Canvas.drawBitmap(bitmapIceAsset,
                            new Rect(0, 0, bitmapIceAsset.getWidth(), bitmapIceAsset.getHeight()),
                            new Rect(map[0][0].x,
                                    map[0][0].y,
                                    map[4][7].x + m_BlockSize,
                                    map[4][7].y + m_NumBlocksHigh),
                            null);
                }
            }

            m_Paint.reset();

            // if user is dragging an ally from the options to the map
            if(isSelecting){
                int imgsubtract = 0;
                if(selectedAlly.equals("Wizard")) {
                    src = new Rect(0, 0, Wizard.FRAME_WIDTH, Wizard.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 90) / 2, // to center the image on the cursor
                                    cursorLocation.y - (m_NumBlocksHigh) / 2 - 25,
                                    cursorLocation.x + (m_BlockSize + 90) / 2,
                                    cursorLocation.y + (m_NumBlocksHigh) / 2 + 25);
                }
                else if(selectedAlly.equals("Warrior")){
                    imgsubtract = 30;
                    src = new Rect(0, 0, Warrior.FRAME_WIDTH, Warrior.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 90) / 2 - imgsubtract, // to center the image on the cursor
                                    cursorLocation.y - m_NumBlocksHigh / 2 - 25,
                                    cursorLocation.x + (m_BlockSize + 90) / 2,
                                    cursorLocation.y + m_NumBlocksHigh / 2 + 25);
                }
                else if(selectedAlly.equals("Archer")){
                    src = new Rect(0, 0, Archer.FRAME_WIDTH, Archer.FRAME_HEIGHT);
                    dst = new Rect( cursorLocation.x - (m_BlockSize + 90) / 2, // to center the image on the cursor
                                    cursorLocation.y - m_NumBlocksHigh / 2 - 25,
                                    cursorLocation.x + (m_BlockSize + 90) / 2,
                                    cursorLocation.y + m_NumBlocksHigh / 2 + 25);
                }
                else if(selectedAlly.equals("Spearman")){
                    src = new Rect(0, 0, Spearman.FRAME_WIDTH, Spearman.FRAME_HEIGHT);
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
                            dst = new Rect( map[y][x].x - imgsubtract, // to center the image on the cursor
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
                if(selectedAlly.equals("Archer"))
                    m_Canvas.drawBitmap(bitmapArcher, src, dst, m_Paint);
                else if(selectedAlly.equals("Spearman"))
                    m_Canvas.drawBitmap(bitmapSpearman, src, dst, m_Paint);
            }

            if(winner){
                m_Paint.setARGB(60, 0,0,0);
                m_Canvas.drawRect(0, 0, m_ScreenWidth, m_ScreenHeight, m_Paint);

                Bitmap bitmapVictory = decodeSampleBitmapFromResource(this.getResources(), R.drawable.victory, 250, 100);
                src = new Rect(0, 0, bitmapVictory.getWidth(), bitmapVictory.getHeight());
                dst = new Rect( (int)((m_ScreenWidth - 200 * scale) / 2),
                                (int)((m_ScreenHeight - 50 * scale) / 2),
                                (int)((m_ScreenWidth + 200 * scale) / 2),
                                (int)((m_ScreenHeight + 50 * scale) / 2));
                m_Canvas.drawBitmap(bitmapVictory, src, dst, null);

            }
            else if(gameOver){
                m_Paint.setARGB(60, 0,0,0);
                m_Canvas.drawRect(0, 0, m_ScreenWidth, m_ScreenHeight, m_Paint);

                Bitmap bitmapDefeat = decodeSampleBitmapFromResource(this.getResources(), R.drawable.defeat, 250, 100);
                src = new Rect(0, 0, bitmapDefeat.getWidth(), bitmapDefeat.getHeight());
                dst = new Rect( (int)((m_ScreenWidth - 200 * scale) / 2),
                                (int)((m_ScreenHeight - 50 * scale) / 2),
                                (int)((m_ScreenWidth + 200 * scale) / 2),
                                (int)((m_ScreenHeight + 50 * scale) / 2));
                m_Canvas.drawBitmap(bitmapDefeat, src, dst, null);

                m_Paint.reset();

                m_Paint.setColor(Color.rgb( 45, 45, 205));
                m_Canvas.drawRect(  (float) (m_ScreenWidth / 4),
                                    (float) (m_ScreenHeight / 4 * 3),
                                    (float) (m_ScreenWidth / 4 + 130 * scale),
                                    (float) (m_ScreenHeight / 4 * 3 + 80 * scale),
                                    m_Paint);

                m_Canvas.drawRect(  (float) (m_ScreenWidth / 2 + 30 * scale),
                                    (float) (m_ScreenHeight / 4 * 3),
                                    (float) (m_ScreenWidth / 4 * 3 + 30 * scale),
                                    (float) (m_ScreenHeight / 4 * 3 + 80 * scale),
                                    m_Paint);

                m_Paint.setColor(Color.WHITE);
                m_Paint.setTextSize(36);

                m_Canvas.drawText("Retry", m_ScreenWidth / 4 + 40 * scale, m_ScreenHeight / 4 * 3 + 50 * scale, m_Paint);
                m_Canvas.drawText("Go to Main Menu", m_ScreenWidth / 2 + 50 * scale, m_ScreenHeight / 4 * 3 + 50 * scale, m_Paint);

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
                        updateScore(m_Score + ally.updateAlly(enemies, m_BlockSize));

                        // check contact is for projectiles with damage
                        if(ally instanceof Wizard){
                            for(int i = 0; i < ((Wizard) ally).getProjectiles().size(); i++){
                                Projectile proj = ((Wizard) ally).getProjectiles().get(i);
                                if(proj.hasEncountered()){
                                    ((Wizard) ally).removeProjectile(proj);
                                }
                                else
                                    updateScore(m_Score + proj.checkContact(enemies));
                            }
                        }
                        else if(ally instanceof Archer){
                            for(int i = 0; i < ((Archer) ally).getProjectiles().size(); i++){
                                Projectile proj = ((Archer) ally).getProjectiles().get(i);
                                if(proj.hasEncountered()){
                                    ((Archer) ally).removeProjectile(proj);
                                }
                                else
                                    m_Score += proj.checkContact(enemies);
                            }
                        }

                    }
                }
            }
        }

        ArrayList<Enemy> tempo = new ArrayList<>();
        // update each enemy; next frame, attack or remove when dead
        for(int y = 0; y < enemies.size(); y++){
            Enemy enemy = enemies.get(y);
            if(enemy.isDead()){
                int[] temp = new int[4];
                temp[0] = enemy.getPosX();
                temp[1] = enemy.getPosY();
                temp[2] = 10;
                temp[3] = enemy.getScore();
                killedEnemies.add(temp);
                tempo.add(enemy);
            }
            else {
                enemy.updateEnemy(allyMap);
                if(enemy instanceof EnemyMage){
                    for(int i = 0; i < ((EnemyMage) enemy).getProjectiles().size(); i++){
                        Projectile proj = ((EnemyMage) enemy).getProjectiles().get(i);
                        if(proj.hasEncountered()){
                            ((EnemyMage) enemy).removeProjectile(proj);
                        }
                        else
                            proj.checkContact(allyMap);
                    }
                }
            }
        }

        //removes enemies from enemySpawnTime[currentWave] to update the enemies being spawned
        for(int j = 0; j < tempo.size(); j++){
            enemies.remove(tempo.get(j));
        }

        // updates killed enemies; removes those who have passed the scoreDisplayLength
        for(int x = 0; x < killedEnemies.size(); x ++){
            int[] temp = killedEnemies.get(x);
            temp[2] --;
            if(temp[2] == 0){
                killedEnemies.remove(temp);
            }
        }

        ArrayList<Integer> temp = new ArrayList<>();

        // summon new enemies
        for(int j = 0; j < enemySpawnTime[currentWave].size(); j++){
            if(j < enemySpawnTime[currentWave].size()) {
                if(enemySpawnTime[currentWave] != null) {
                    int spawn = enemySpawnTime[currentWave].get(j);
                    if (spawn <= timePassedPerWave) {
                        Random rand = new Random();
                        this.summonEnemy(rand.nextInt(5));
                        temp.add(spawn);
                        if (currentWave == enemySpawnTime.length - 1) {
                            winCon = true;
                        }
                    }
                }
            }
        }

        //removes enemies from enemySpawnTime[currentWave] to update the enemies being spawned
        for(int j = 0; j < temp.size(); j++){
            enemySpawnTime[currentWave].remove(temp.get(j));
        }

        ArrayList<Spell> tempor = new ArrayList<>();
        // clear spells
        for(int i = 0; i < spellsActive.size(); i++){
            Spell spell = spellsActive.get(i);
            if(spell.countdownDuration()){
                tempor.add(spell);
            }
        }

        // actually remove the spells in active spells
        for(int i = 0; i < tempor.size(); i++){
            spellsActive.remove(tempor.get(i));
        }

        // game over
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

        else if(currentWave == maxWave - 1 && enemies.size() == 0 && winCon && enemySpawnTime[currentWave].size() == 0){
            winner = true;
            //m_Playing = false;
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                if(winner){
                    pause();
                    this.destroy();
                    this.getContext().startActivity(new Intent(this.getContext(), LevelSelect.class));
                }
                else if(gameOver){
                   if( motionEvent.getX() >= m_ScreenWidth / 4 &&
                       motionEvent.getY() >= m_ScreenHeight / 4 * 3 &&
                       motionEvent.getX() <= m_ScreenWidth / 4 + 130 * scale &&
                       motionEvent.getY() <= m_ScreenHeight / 4 * 3 + 80 * scale){ // retry
                       pause();
                       startGame();
                   }
                   else if( motionEvent.getX() >= (m_ScreenWidth / 2 + 30 * scale) &&
                            motionEvent.getY() >= m_ScreenHeight / 4 * 3 &&
                            motionEvent.getX() <= m_ScreenWidth / 4 * 3 + 30 * scale &&
                            motionEvent.getY() <= m_ScreenHeight / 4 * 3 + 80 * scale){
                       this.destroy();
                       this.getContext().startActivity(new Intent(this.getContext(), LevelSelect.class));
                   }

                }
                else if(!m_Playing){
                    m_Playing = true;
                    resume();
                }


                else if(motionEvent.getX() >= Math.round(30 * scale) &&
                        motionEvent.getX() <= Math.round(110 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 100){ // ummm if you touch the wizard icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Wizard";
                }

                else if(motionEvent.getX() >= Math.round(120 * scale) &&
                        motionEvent.getX() <= Math.round(190 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 50){ // pressed the warrior icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Warrior";
                }

                else if(motionEvent.getX() >= Math.round(210 * scale) &&
                        motionEvent.getX() <= Math.round(280 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 75){ // pressed the archer icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Archer";
                }

                else if(motionEvent.getX() >= Math.round(300 * scale) &&
                        motionEvent.getX() <= Math.round(370 * scale) &&
                        motionEvent.getY() >= Math.round(10 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale) &&
                        m_Score >= 125){ // pressed the spearman icon
                    isSelecting = true;
                    cursorLocation.x = (int) motionEvent.getX();
                    cursorLocation.y = (int) motionEvent.getY();
                    selectedAlly = "Spearman";
                }

                // pause
                else if(motionEvent.getX() >= Math.round(580 * scale) &&
                        motionEvent.getX() <= Math.round(610 * scale) &&
                        motionEvent.getY() >= Math.round(20 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale)){
                    pause();
                    drawGame();
                }

                // fast forward
                else if(motionEvent.getX() >= Math.round(540 * scale) &&
                        motionEvent.getX() <= Math.round(570 * scale) &&
                        motionEvent.getY() >= Math.round(20 * scale) &&
                        motionEvent.getY() <= Math.round(50 * scale)){
                    if(fastforward)
                        fastforward = false;
                    else
                        fastforward = true;
                    drawGame();
                }

                // fire spell selected from spell list
                else if(motionEvent.getX() >= m_ScreenWidth - 190 * scale &&
                        motionEvent.getX() <= m_ScreenWidth - 140 * scale &&
                        motionEvent.getY() >= m_ScreenHeight + (spellY + 30) * scale &&
                        motionEvent.getY() <= m_ScreenHeight + (spellY + 80) * scale &&
                        spellY == -110){ // spell list is active
                    spellActivated = "Fire";
                    spellY = 0; // hide spell list to make all blocks accessible
                }

                // thunder spell selected from spell list
                else if(motionEvent.getX() >= m_ScreenWidth - 70 * scale &&
                        motionEvent.getX() <= m_ScreenWidth - 20 * scale &&
                        motionEvent.getY() >= m_ScreenHeight + (spellY + 30) * scale &&
                        motionEvent.getY() <= m_ScreenHeight + (spellY + 80) * scale &&
                        spellY == -110){ // spell list is active
                    spellActivated = "Thunder";
                    spellY = 0; // hide spell list to make all blocks accessible
                }

                // ice spell selected from spell list
                else if(motionEvent.getX() >= m_ScreenWidth - 130 * scale &&
                        motionEvent.getX() <= m_ScreenWidth - 80 * scale &&
                        motionEvent.getY() >= m_ScreenHeight + (spellY + 30) * scale &&
                        motionEvent.getY() <= m_ScreenHeight + (spellY + 80) * scale &&
                        spellY == -110){ // spell list is active
                    spellActivated = "Ice";
                    spellY = 0; // hide spell list to make all blocks accessible
                }

                // spell casted
                else if(!spellActivated.equals("")){
                    for(int y = 0; y < map.length; y++){
                        for(int x = 0; x < map[y].length; x++){
                            // if within map, snap transparent to block
                            if( motionEvent.getX() >= map[y][x].x &&
                                motionEvent.getX() <= map[y][x].x + m_BlockSize &&
                                motionEvent.getY() >= map[y][x].y &&
                                motionEvent.getY() <= map[y][x].y + m_NumBlocksHigh){ // if within a block inside map
                                switch(spellActivated){
                                    case "Fire":
                                        if(m_Score >= 300) {
                                            m_Score -= 300;
                                            Fire fire = new Fire();
                                            updateScore(m_Score + fire.activateSpell(x, y, enemies, map));
                                            spellsActive.add(fire);
                                        }
                                        spellActivated = "";
                                        spellY = -10;
                                        break;
                                    case "Thunder":
                                        if(m_Score >= 200) {
                                            m_Score -= 200;
                                            Thunder thunder = new Thunder();
                                            updateScore(m_Score + thunder.activateSpell(x, y, enemies, map));
                                            spellsActive.add(thunder);
                                        }
                                        spellActivated = "";
                                        spellY = -10;
                                        break;
                                    case "Ice":
                                        if(m_Score >= 500) {
                                            m_Score -= 500;
                                            Ice ice = new Ice();
                                            updateScore(m_Score + ice.activateSpell(enemies, map));
                                            spellsActive.add(ice);
                                        }
                                        spellActivated = "";
                                        spellY = -10;
                                        break;
                                }
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                cursorLocation.x = (int) motionEvent.getX();
                cursorLocation.y = (int) motionEvent.getY();
                if( cursorLocation.y <= startY + 10 * scale && // nakapag-swipe up ka
                    startX >= m_ScreenWidth - 200 * scale && // within bounds of spell
                    startX <= m_ScreenWidth - 10 * scale && // within bounds of spell
                    startY >= m_ScreenHeight - 20 * scale && // kung saan ka nagsimulang mag-drag is within 20dp from the end of the screen
                    spellY == -10){ // position of spells is hidden

                    spellY = -110;
                }
                else if(cursorLocation.y >= startY - 10 * scale &&
                        startX >= m_ScreenWidth - 200 * scale &&
                        startX <= m_ScreenWidth - 10 * scale &&
                        startY >= m_ScreenHeight / 2 &&
                        spellY == -110){
                    spellY = -10;
                }
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
                            else if(selectedAlly.equals("Archer")) {
                                allyMap[y][x] = new Archer(map[y][x].x, map[y][x].y, x, y, bitmapArcher, bitmapArcherProjectile, scale, m_BlockSize, m_ScreenWidth);
                                m_Score -= 75;
                            }
                            else if(selectedAlly.equals("Spearman")) {
                                allyMap[y][x] = new Spearman(map[y][x].x, map[y][x].y, x, y, bitmapSpearman, scale);
                                m_Score -= 125;
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
        //Random rand = new Random();
        Enemy panel;
        //int result = rand.nextInt(2);

        //if(result == 1)
            panel = new Panelist(map[lane][7].x+m_BlockSize*2, map[lane][7].y,lane,bitmapEnemy,scale,m_BlockSize);
        //else
        //    panel = new EnemyMage(map[lane][7].x+m_BlockSize*2, map[lane][7].y,lane,bitmapEnemy, bitmapEnemyProjectile, scale,m_BlockSize);
        panel.setDifficulty(difficulty);
        enemies.add(panel);
    }


    public void destroy(){
        pause();
        bitmapWizard.recycle();
        bitmapBackground.recycle();
        bitmapWizardIcon.recycle();
        bitmapWarrior.recycle();
        bitmapWarriorIcon.recycle();
        bitmapThesis.recycle();
        bitmapCastle.recycle();
        bitmapEnemy.recycle();
        bitmapMageProjectile.recycle();
        bitmapArcher.recycle();
        bitmapArcherIcon.recycle();
        bitmapSpearman.recycle();
        bitmapSpearmanIcon.recycle();
        bitmapArcherProjectile.recycle();
        bitmapWizard = null;
        bitmapBackground = null;
        bitmapWizardIcon = null;
        bitmapWarrior = null;
        bitmapWarriorIcon = null;
        bitmapThesis = null;
        bitmapCastle = null;
        bitmapEnemy = null;
        bitmapMageProjectile = null;
        bitmapArcher = null;
        bitmapArcherIcon = null;
        bitmapSpearman = null;
        bitmapSpearmanIcon = null;
        bitmapArcherProjectile = null;
        m_Thread = null;
    }

    public void updateScore(int score){
        m_Score = score;
        dBhelper.updatePoints(m_Score);
    }
}
