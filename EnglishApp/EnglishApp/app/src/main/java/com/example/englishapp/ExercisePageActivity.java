package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExercisePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.exercise_page);

        applyWindowInsets();
        setupBackButton();
        setupCardClickListeners();
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupCardClickListeners() {
        CardView cardExercise1 = findViewById(R.id.cardExercise1);
        CardView cardExercise2 = findViewById(R.id.cardExercise2);
        CardView cardExercise3 = findViewById(R.id.cardExercise3);

        if (cardExercise1 != null) {
            cardExercise1.setOnClickListener(v -> {
                Intent intent = new Intent(ExercisePageActivity.this, PickTestActivity.class);
                startActivity(intent);
            });
        }
        
        if (cardExercise2 != null) {
            cardExercise2.setOnClickListener(v -> {
                Intent intent = new Intent(ExercisePageActivity.this, TestHistoryActivity.class);
                startActivity(intent);
            });
        }

        if (cardExercise3 != null) {
            cardExercise3.setOnClickListener(v -> {
                Intent intent = new Intent(ExercisePageActivity.this, ScoreStatsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
