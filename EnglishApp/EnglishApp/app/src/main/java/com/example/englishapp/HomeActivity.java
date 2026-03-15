package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.game.StartGameActivity;

public class HomeActivity extends AppCompatActivity {

    LinearLayout btnProfile, btnGrammar, cardGame;
    TextView btnLearningMethod;
    CardView cardVocabulary, cardExerciseHome, cardDictionaryHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_home);

        btnProfile = findViewById(R.id.btnProfile);
        btnGrammar = findViewById(R.id.btnGrammar);
        btnLearningMethod = findViewById(R.id.btnLearningMethod);
        cardVocabulary = findViewById(R.id.cardVocabulary);
        cardExerciseHome = findViewById(R.id.cardExerciseHome);
        cardDictionaryHome = findViewById(R.id.cardDictionaryHome);
        cardGame = findViewById(R.id.cardGame);

        applyWindowInsets();

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        btnGrammar.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, TopicGrammarActivity.class));
        });

        btnLearningMethod.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LearningMethodActivity.class));
        });

        if (cardVocabulary != null) {
            cardVocabulary.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, TopicActivity.class));
            });
        }

        if (cardExerciseHome != null) {
            cardExerciseHome.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, ExercisePageActivity.class));
            });
        }

        if (cardDictionaryHome != null) {
            cardDictionaryHome.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, DictionaryActivity.class));
            });
        }

        if (cardGame != null) {
            cardGame.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, StartGameActivity.class));
            });
        }
    }

    private void applyWindowInsets() {
        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}