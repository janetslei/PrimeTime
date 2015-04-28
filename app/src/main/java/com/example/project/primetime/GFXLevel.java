package com.example.project.primetime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
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

    int score;

    int remainingTime;

    boolean outOfTime;

    CountDownTimer myTimer;

    Rect primeButton;

    float minHeight, maxHeight, minWidth, maxWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ourSurfaceView = new LevelSurface(this);
        ourSurfaceView.setOnTouchListener(this);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        x = 0;
        y = 0;
        rect1 = new Rect();
        primeButton = new Rect();
        squareSize = screenWidth/8;
        selectedX = -1;
        selectedY = -1;
        tapped = false;
        score = 0;
        outOfTime = false;

        myTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                remainingTime = (int)millisUntilFinished / 1000;
                if(remainingTime == 1) outOfTime = true;
            }

            public void onFinish() {
            }
        };

        myTimer.start();

        minHeight = squareSize * 3;
        maxHeight = minHeight + (8 * squareSize);
        minWidth = squareSize;
        maxWidth = minWidth + (6 * squareSize);

        generateSquares();

        //test = BitmapFactory.decodeResource(getResources(), R.drawable.square);
        //selected = BitmapFactory.decodeResource(getResources(), R.drawable.selected);
        setContentView(ourSurfaceView);
    }

    void generateSquares()
    {
        Random r = new Random(System.currentTimeMillis());

        numOfSquares = r.nextInt(17) + 1;

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
    }

    void youLose()
    {
        Intent intent = new Intent("com.example.project.primetime.LOSE");
        intent.putExtra("score", score);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
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

                int squaresInHeight = 0;
                int expectedNumberOfSquares = -1;
                int currentY;
                int topSquare = -1;
                int rectWidth = -1;

                boolean winner = true;

                for(int i = 0; i < 6; i++) {
                    squaresInHeight = 0;
                    currentY = -1;
                    for (int j = 0; j < 8; j++) {
                        if (grid[i][j].containsSquare) {
                            if(topSquare == -1) topSquare = j;
                            if(currentY == -1) currentY = j;
                            else if((currentY + 1) != j) {
                                winner = false;
                            }
                            else currentY = j;

                            if(j < topSquare) winner = false;
                            if(expectedNumberOfSquares != -1 && j >= topSquare + expectedNumberOfSquares) winner = false;
                            squaresInHeight += 1;
                        }
                    }
                    if(expectedNumberOfSquares == -1){
                        if(squaresInHeight != 0) {
                            expectedNumberOfSquares = squaresInHeight;
                            if(expectedNumberOfSquares > 1) {
                                if(numOfSquares % expectedNumberOfSquares == 0) {
                                    rectWidth = numOfSquares/expectedNumberOfSquares;
                                    if(rectWidth == 1) {
                                        winner = false;
                                    }
                                }
                                else {
                                    winner = false;
                                }
                            }
                            else
                            {
                                winner = false;
                            }
                        }
                    }
                    else if(expectedNumberOfSquares != squaresInHeight && rectWidth > 1) {
                        winner = false;
                    }
                    else
                    {
                        rectWidth -= 1;
                        if(rectWidth == 1) break;
                    }
                }

                for(int i = 0; i < 6; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (grid[i][j].containsSquare) {
                            if (!grid[i][j].selected)
                                canvas.drawRect(grid[i][j].location, grid[i][j].color);
                            else {
                                Paint selectedColor = new Paint();
                                selectedColor.setColor(Color.BLACK);
                                selectedColor.setStyle(Paint.Style.FILL);
                                canvas.drawRect(grid[i][j].location, selectedColor);
                            }
                        }
                    }
                }

                if(winner) {
                    generateSquares();
                    score += remainingTime;
                    myTimer.start();
                }

                if(outOfTime) {
                    youLose();
                }

                if(y > (canvas.getHeight() - 200) && tapped)
                {
                    tapped = false;
                    boolean isPrime = true;
                    for(int i = 2; i < (numOfSquares/2) + 1; i++)
                    {
                        if(numOfSquares % i == 0) isPrime = false;
                    }
                    if(isPrime) {
                        generateSquares();
                        score += remainingTime;
                        myTimer.start();
                    }
                    else
                    {
                        youLose();
                    }
                }
                else if(x >= minWidth && x <= maxWidth && y >= minHeight && y <= maxHeight && tapped)
                {
                    tapped = false;
                    int xPos = (int)(x - minWidth) / (int)squareSize;
                    int yPos = (int)(y - minHeight) / (int)squareSize;

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
                primeButton.set(0, (canvas.getHeight() - 180), canvas.getWidth(), canvas.getHeight());
                Paint primeColor = new Paint();
                primeColor.setColor(Color.CYAN);
                primeColor.setStyle(Paint.Style.FILL);
                Paint textColor = new Paint();
                textColor.setColor(Color.BLACK);
                textColor.setStyle(Paint.Style.FILL);
                canvas.drawRect(primeButton, primeColor);
                Paint text = new Paint();
                text.setColor(Color.WHITE);
                text.setTextSize(100);
                String primeText = "Is Prime!";
                RectF bounds = new RectF(primeButton);
                bounds.right = text.measureText(primeText, 0, primeText.length());
                bounds.bottom = text.descent() - text.ascent();
                bounds.left += (primeButton.width() - bounds.right) / 2.0f;
                bounds.top += (primeButton.height() - bounds.bottom) /2.0f;
                canvas.drawText(primeText, bounds.left, bounds.top - text.ascent(), text);

                //Score Text
                Rect backTangle = new Rect();
                backTangle.set(canvas.getWidth()/2 + 20, 0, canvas.getWidth(), 100);
                Paint scoreText = new Paint();
                String scoreString = String.valueOf(score);
                scoreText.setTextSize(70);
                scoreText.setColor(Color.BLACK);
                bounds.right = scoreText.measureText(scoreString, 0, scoreString.length());
                bounds.bottom = scoreText.descent() - scoreText.ascent();
                bounds.left = (backTangle.width() - bounds.right) + canvas.getWidth()/2;
                bounds.top = (backTangle.height() - bounds.bottom) /2.0f;
                canvas.drawText(scoreString, bounds.left, bounds.top - scoreText.ascent(), scoreText);

                //Timer Text
                backTangle.set(0, 0, canvas.getWidth(), 100);
                Paint timerText = new Paint();
                String timerString = String.valueOf(remainingTime);
                timerText.setTextSize(70);
                timerText.setColor(Color.BLACK);
                bounds = new RectF(backTangle);
                bounds.right = timerText.measureText(timerString, 0, timerString.length());
                bounds.bottom = timerText.descent() - timerText.ascent();
                bounds.left += (backTangle.width() - bounds.right) / 2.0f;
                bounds.top += (backTangle.height() - bounds.bottom) /2.0f;
                canvas.drawText(timerString, bounds.left, bounds.top - timerText.ascent(), timerText);

                //Number of Squares Text
                backTangle.set(canvas.getWidth()/2 + 20, 0, canvas.getWidth(), 100);
                Paint numberText = new Paint();
                String numberString = String.valueOf(numOfSquares);
                numberText.setTextSize(70);
                numberText.setColor(Color.BLACK);
                bounds.right = numberText.measureText(numberString, 0, numberString.length());
                bounds.bottom = numberText.descent() - numberText.ascent();
                bounds.left = 20.0f;
                bounds.top = (backTangle.height() - bounds.bottom) /2.0f;
                canvas.drawText(numberString, bounds.left, bounds.top - numberText.ascent(), numberText);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
