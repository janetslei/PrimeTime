package com.example.project.primetime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import java.util.Random;
/**
 * Created by amin on 4/19/2015.
 */
public class Game extends Activity implements View.OnTouchListener {

    Button oButton;
    Level ourView;

    String levels[] = { "MainActivity", "Instructions", "game"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourView = new Level(this);
        setContentView(ourView);
        //Random rand = new Random(System.currentTimeMillis());
        //int option = rand.nextInt(2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}