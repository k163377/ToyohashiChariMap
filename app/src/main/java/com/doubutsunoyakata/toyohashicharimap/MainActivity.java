package com.doubutsunoyakata.toyohashicharimap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.doubutsunoyakata.toyohashicharimap.input.MapsActivity;

public class MainActivity extends AppCompatActivity {
    Button input;
    Button serch;
    Button history;
    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (Button) findViewById(R.id.Input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent[] = new Intent[1];
                intent[0] = new Intent(MainActivity.this, MapsActivity.class);
                startActivities(intent);
            }
        });

        serch = (Button) findViewById(R.id.Serch);

        upload = (Button) findViewById(R.id.Upload);

        history = (Button) findViewById(R.id.History);
    }
}
