package com.example.englishapp.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;

public class DifficultyActivity extends AppCompatActivity {

    Button btnEasy, btnMedium, btnHard, btnLeaderboard;

    String player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_difficulty);

        player = getIntent().getStringExtra("player");

        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnHard = findViewById(R.id.btnHard);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        // animation trôi nút
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        btnEasy.startAnimation(slide);
        btnMedium.startAnimation(slide);
        btnHard.startAnimation(slide);

        // animation bấm nút
        Animation press = AnimationUtils.loadAnimation(this, R.anim.btn_press);

        btnEasy.setOnClickListener(v -> {

            v.startAnimation(press);

            new Handler().postDelayed(() -> startGame("easy"),150);

        });

        btnMedium.setOnClickListener(v -> {

            v.startAnimation(press);

            new Handler().postDelayed(() -> startGame("medium"),150);

        });

        btnHard.setOnClickListener(v -> {

            v.startAnimation(press);

            new Handler().postDelayed(() -> startGame("hard"),150);

        });

        // leaderboard
        btnLeaderboard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(this, LeaderboardActivity.class);

            startActivity(intent);

        });
    }

    void startGame(String level){

        Intent intent = new Intent(this, QuizActivity.class);

        intent.putExtra("player", player);
        intent.putExtra("level", level);

        startActivity(intent);
    }
}