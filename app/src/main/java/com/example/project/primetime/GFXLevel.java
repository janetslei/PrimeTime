package com.example.project.primetime;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

/**
 * Created by austi_000 on 4/26/2015.
 */
public class GFXLevel extends Activity implements View.OnTouchListener {

    public class square {
        boolean selected; //the selected square
        Rect location; //Rectangle Location
        boolean containsSquare; //if false no square
        Paint color; //color of square
    }

    square[][] grid = new square[6][8]; //grid of squares

    LevelSurface ourSurfaceView;
    float x, y;
    float squareSize;

    int selectedX, selectedY;
    Bitmap test, selected;
    Rect rect1;
    boolean tapped;

    int numOfSquares;

    float minHeight, maxHeight, minWidth, maxWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        ourSurfaceView = new LevelSurface(this);
        ourSurfaceView.setOnTouchListener(this);
        x = 0;
        y = 0;
        rect1 = new Rect();
        squareSize = 90;
        selectedX = -1;
        selectedY = -1;
        tapped = false;

        minHeight = squareSize * 3;
        maxHeight = minHeight + (8 * squareSize);
        minWidth = squareSize;
        maxWidth = minWidth + (6 * squareSize);

        Random r = new Random(System.currentTimeMillis());

        numOfSquares = r.nextInt(11) + 1;

        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                grid[i][j] = new square();
                grid[i][j].selected = false;
                grid[i][j].location = new Rect();
                grid[i][j].containsSquare = false;
                grid[i][j].color = new Paint();
            }
        }

        for(int i = 0; i < numOfSquares; i++)
        {
            int xPos = r.nextInt(6);
            int yPos = r.nextInt(7);
            while(grid[xPos][yPos].containsSquare)
            {
                xPos = r.nextInt(6);
                yPos = r.nextInt(7);
            }
            Rect temp = new Rect();
            int left = (xPos * (int)squareSize) + (int)minWidth;
            int right = (xPos * (int)squareSize) + (int)minWidth + (int)squareSize;
            int top = (yPos * (int)squareSize) + (int)minHeight;
            int bottom = (yPos * (int)squareSize) + (int)minHeight + (int)squareSize;
            temp.set(left, top, right, bottom);
            grid[xPos][yPos].location = temp;
            grid[xPos][yPos].containsSquare = true;
            int colorNum = r.nextInt(4);
            if(colorNum == 0) grid[xPos][yPos].color.setColor(Color.BLUE);
            else if(colorNum == 1) grid[xPos][yPos].color.setColor(Color.GREEN);
            else if(colorNum == 2) grid[xPos][yPos].color.setColor(Color.YELLOW);
            else if(colorNum == 3) grid[xPos][yPos].color.setColor(Color.RED);
            grid[xPos][yPos].color.setStyle(Paint.Style.FILL);
        }
        //test = BitmapFactory.decodeResource(getResources(), R.drawable.square);
        //selected = BitmapFactory.decodeResource(getResources(), R.drawable.selected);
        setContentView(ourSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ourSurfaceView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ourSurfaceView.resume();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x = motionEvent.getX();
                y = motionEvent.getY();
                tapped = true;
                break;
            case MotionEvent.ACTION_UP:
                x = 0;
                y = 0;
                tapped = false;
                break;
        }
        return false;
    }
    
    public class LevelSurface extends SurfaceView implements Runnable{

        SurfaceHolder ourHolder;
        Thread ourThread = null;
        boolean isRunning = true;

        public LevelSurface(Context context) {
            super(context);
            ourHolder = getHolder();
        }

        public void pause()
        {
            isRunning = false;
            while(true)
            {
                try {
                    ourThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            ourThread = null;
        }

        public void resume()
        {
            isRunning = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public void run() {
            while(isRunning)
            {
                if(!ourHolder.getSurface().isValid()) {
                    continue;
                }

                Canvas canvas;
                canvas = ourHolder.lockCanvas();

                canvas.drawRGB(255, 255, 255);

                if(x >= minWidth && x <= maxWidth && y >= minHeight && y <= maxHeight && tapped)
                {
                    tapped = false;
                    int xPos = (int)(x - minWidth) / 90;
                    int yPos = (int)(y - minHeight) / 90;

                    if(grid[xPos][yPos].containsSquare)
                    {
                        grid[xPos][yPos].selected = true;
                        if(selectedX != -1 && selectedY != -1)
                        {
                            grid[selectedX][selectedY].selected = false;
                        }
                        selectedX = xPos;
                        selectedY = yPos;
                    }
                    else
                    {
                        if(selectedX != -1 && selectedY != -1)
                        {
                            Rect temp = new Rect();
                            int left = (xPos * (int)squareSize) + (int)minWidth;
                            int right = (xPos * (int)squareSize) + (int)minWidth + (int)squareSize;
                            int top = (yPos * (int)squareSize) + (int)minHeight;
                            int bottom = (yPos * (int)squareSize) + (int)minHeight + (int)squareSize;
                            temp.set(left, top, right, bottom);
                            grid[selectedX][selectedY].location = temp;
                            square tempSquare = grid[xPos][yPos];
                            grid[xPos][yPos] = grid[selectedX][selectedY];
                            grid[selectedX][selectedY] = tempSquare;
                            selectedX = -1;
                            selectedY = -1;
                            grid[xPos][yPos].selected = false;
                        }
                    }
                }

                for(int i = 0; i < 6; i++)
                {
                    for(int j = 0; j < 8; j++)
                    {
                        if(grid[i][j].containsSquare)
                        {
                            if(!grid[i][j].selected) canvas.drawRect(grid[i][j].location, grid[i][j].color);
                            else {
                                Paint selectedColor = new Paint();
                                selectedColor.setColor(Color.BLACK);
                                selectedColor.setStyle(Paint.Style.FILL);
                                canvas.drawRect(grid[i][j].location, selectedColor);
                            }
                        }
                    }
                }

                /*if(x <= maxWidth && x >= minWidth && y >= minHeight && y <= maxHeight)
                {
                    rect1.set((int)x - (canvas.getWidth()/16), (int)y - (canvas.getWidth()/16), (canvas.getWidth()/8) - (canvas.getWidth()/16) + (int)x, (canvas.getWidth()/8) - (canvas.getWidth()/16) + (int)y);
                    Paint blue = new Paint();
                    blue.setColor(Color.BLUE);
                    blue.setStyle(Paint.Style.FILL);
                    //canvas.drawBitmap(test , x-(test.getWidth()/2), y-(test.getHeight()/2), null);
                    canvas.drawRect(rect1, blue);
                }
                if(startX != 0 && startY != 0)
                {
                    //canvas.drawBitmap(selected , startX-(selected.getWidth()/2), startY-(selected.getHeight()/2), null);
                }
                if(finishX != 0 && finishY != 0)
                {
                   // canvas.drawBitmap(selected , finishX-(selected.getWidth()/2), finishY-(selected.getHeight()/2), null);
                }*/
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
