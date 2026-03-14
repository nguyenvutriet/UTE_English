package com.example.englishapp.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.englishapp.R;

public class ResultActivity extends AppCompatActivity {

    TextView txtResult;
    Button btnLeaderboard;

    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_result);

        txtResult = findViewById(R.id.txtResult);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        int score = getIntent().getIntExtra("score",0);

        playerName = getIntent().getStringExtra("player");

        txtResult.setText("Score: " + score);

        btnLeaderboard.setOnClickListener(v -> {

            Intent intent = new Intent(ResultActivity.this, LeaderboardActivity.class);
            intent.putExtra("player", playerName);

            startActivity(intent);

        });
    }
}