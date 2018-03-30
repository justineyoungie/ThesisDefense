package com.thesis.thesisdefense.DatabaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by drjeoffreycruzada on 30/03/2018.
 */

public class GameDBhelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ThesisDefense.db";
    public static final String TABLE_NAME = "game";
    public static final String COL_1 = "levelsCleared";
    public GameDBhelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE "+TABLE_NAME+" ("+COL_1+" INTEGER);"; //queries in SQl not mySQL
        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlCommand = "DROP TABLE IF EXISTS "+TABLE_NAME; //queries in SQl not mySQL
        db.execSQL(sqlCommand);
        onCreate(db);
    }
}
