package com.thesis.thesisdefense.Misc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.ArrayList;

/**
 * Created by drjeoffreycruzada on 01/04/2018.
 */

public class Dialog {
    ArrayList<String> texts;
    Canvas canvas;
    private int canvasWidth;
    private int canvasHeight;
    private float scale;
    public Dialog(int canvasWidth, int canvasHeight, float scale){
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        this.scale = scale;
    }

    //Draws dialog on Canvas
    public void drawDialog(Canvas canvas, ArrayList<String> texts, Paint paint, int textsize, Bitmap bitmapnarrator){
        this.canvas = canvas;
        this.texts = texts;

        int frameHeight = (int)(canvasHeight-(0.25*canvasHeight));
        int frameinverse = (int)(0.25*canvasHeight);
        int narratorwidth = (int)(0.275*canvasWidth);
        int narratorstart = (int) (0.015*canvasWidth);
        //paint.setColor(Color.BLUE);
        //paint.setStyle(Paint.Style.FILL);

        Rect src = new Rect(0,0,bitmapnarrator.getWidth(),bitmapnarrator.getHeight());
        Rect dst = new Rect(narratorstart,frameinverse,narratorwidth,canvas.getHeight());

        //canvas.drawRect(narratorstart,frameinverse,narratorwidth,canvas.getHeight(),paint);

        canvas.drawBitmap(bitmapnarrator, src, dst, paint);
        paint.reset();


        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(97);
        canvas.drawRect(0,frameHeight,canvas.getWidth(),canvas.getHeight(),paint);

        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setTextSize(textsize);
        for(int i = 0; i < texts.size(); i++){
            canvas.drawText(texts.get(i),100,frameHeight+textsize+textsize*i+5*i,paint);

        }

        paint.reset();
    }

}
