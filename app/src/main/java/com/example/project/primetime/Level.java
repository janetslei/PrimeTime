package com.example.project.primetime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by amin on 4/19/2015.
 */
public class Level extends View {

    Bitmap wSquare;
    float changingY;

    public Level(Context context) {
        super(context);
        wSquare = BitmapFactory.decodeResource(getResources(), R.drawable.square);
        changingY = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(wSquare, (canvas.getWidth()/2), changingY, null);
    }
}
