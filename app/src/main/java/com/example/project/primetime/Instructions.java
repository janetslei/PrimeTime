package com.example.project.primetime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by amin on 4/19/2015.
 */
public class Instructions extends Activity {

    Button oButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);

        oButton = (Button) findViewById(R.id.okayButton);
        oButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.project.primetime.GFXLEVEL");
                startActivity(intent);
            }
        });
    }

}
