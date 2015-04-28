package com.example.project.primetime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Lose extends Activity {

    Button sButton;
    TextView playerScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent = getIntent();
        setContentView(R.layout.lose);
        int score = getIntent.getIntExtra("score", 0);

        playerScore = (TextView) findViewById(R.id.score);
        String scoreString = String.valueOf(score);
        playerScore.setText(scoreString);

        sButton = (Button) findViewById(R.id.startButton);
        sButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.project.primetime.GFXLEVEL");
                startActivity(intent);
            }
        });
    }
}
