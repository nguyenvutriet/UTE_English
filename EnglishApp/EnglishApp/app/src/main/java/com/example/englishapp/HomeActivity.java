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

import com.example.englishapp.activity.MainVideoActivity;
import com.example.englishapp.game.StartGameActivity;

public class HomeActivity extends AppCompatActivity {

    LinearLayout btnProfile, btnGrammar, cardGame, btnVideo;
    LinearLayout cardPodcast, cardReader;
    TextView btnLearningMethod;
    CardView cardVocabulary, cardExerciseHome, cardDictionaryHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_home);

        // Ánh xạ các view
        btnProfile = findViewById(R.id.btnProfile);
        btnGrammar = findViewById(R.id.btnGrammar);
        btnLearningMethod = findViewById(R.id.btnLearningMethod);
        cardVocabulary = findViewById(R.id.cardVocabulary);
        cardExerciseHome = findViewById(R.id.cardExerciseHome);
        cardDictionaryHome = findViewById(R.id.cardDictionaryHome);
        cardGame = findViewById(R.id.cardGame);
        cardPodcast = findViewById(R.id.cardPodcast);
        cardReader = findViewById(R.id.cardReader);
        btnVideo = findViewById(R.id.btnVideo); // Fix: Thêm dòng này

        applyWindowInsets();

        // Thiết lập sự kiện click
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            });
        }

        if (btnGrammar != null) {
            btnGrammar.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, TopicGrammarActivity.class));
            });
        }

        if (btnLearningMethod != null) {
            btnLearningMethod.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, LearningMethodActivity.class));
            });
        }

        if (btnVideo != null) {
            btnVideo.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, MainVideoActivity.class));
            });
        }

        if (cardPodcast != null) {
            cardPodcast.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, PodcastActivity.class));
            });
        }

        if (cardReader != null) {
            cardReader.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, BookActivity.class));
            });
        }

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
