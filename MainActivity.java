package com.example.dodgegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView foodFlight;
    ImageButton hawk;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodFlight = findViewById(R.id.title);
        hawk = findViewById(R.id.hawk);

        Intent intent = new Intent(MainActivity.this, DodgeGame.class);


        hawk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });



    }
}