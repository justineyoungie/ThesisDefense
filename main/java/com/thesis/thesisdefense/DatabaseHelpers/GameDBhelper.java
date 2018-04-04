package com.thesis.thesisdefense.DatabaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by drjeoffreycruzada on 30/03/2018.
 */

public class GameDBhelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ThesisDefense.db";
    public static final String TABLE_NAME = "game";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "levelsCleared";
    public static final String COL_3 = "playerName";
    public static final String COL_4 = "Points";
    public GameDBhelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE "+TABLE_NAME+" ("+COL_1+" INTEGER,"+COL_2+" INTEGER, "+COL_3+" TEXT,"+COL_4+" INTEGER);"; //queries in SQl not mySQL
        db.execSQL(sqlCommand);
        initializeDB(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlCommand = "DROP TABLE IF EXISTS "+TABLE_NAME; //queries in SQl not mySQL
        db.execSQL(sqlCommand);
        onCreate(db);
    }


    public void initializeDB(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(COL_1, 1);
        cv.put(COL_2, 0);
        cv.put(COL_3, "");
        cv.put(COL_4, 0);
        db.insert(TABLE_NAME,null,cv);
    }

    public void updateLevel(int level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, level);
        db.update(TABLE_NAME,cv,"ID = ?",new String[] {"1"});
        //String sqlcommand = "UPDATE TABLE "+TABLE_NAME+" SET "+COL_2+" = "+level+" WHERE ID = 1; ";
        //db.execSQL(sqlcommand);
    }
    public void updateName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_3, name);
        db.update(TABLE_NAME,cv,"ID = ?",new String[] {"1"});
        //String sqlcommand = "UPDATE TABLE "+TABLE_NAME+" SET "+COL_3+" = '"+name+"' WHERE ID = 1; ";
        //db.execSQL(sqlcommand);
    }
    public void updatePoints(int points){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_4, points);
        db.update(TABLE_NAME,cv,"ID = ?",new String[] {"1"});
        // String sqlcommand = "UPDATE TABLE "+TABLE_NAME+" SET "+COL_4+" = "+points+" WHERE ID = 1; ";
        //db.execSQL(sqlcommand);
    }



    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlcommand = "SELECT * FROM "+TABLE_NAME+" WHERE ID = 1";
        Cursor res = db.rawQuery(sqlcommand, null);

        return res;
    }
}
