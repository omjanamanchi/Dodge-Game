package com.example.dodgegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameOver extends AppCompatActivity
{
    Button play_again_button;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent replay = new Intent(GameOver.this, MainActivity.class);

        play_again_button = findViewById(R.id.play_again_button);
        play_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(replay);
            }
        });


    }
}