package com.example.englishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.DoingTestActivity;
import com.example.englishapp.PickTestActivity;
import com.example.englishapp.R;
import com.example.englishapp.TestDataRepository;

public class ReadingPracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading_practice);

        applyWindowInsets();
        setupHeaderActions();
    }

    private void setupHeaderActions() {
        ImageView btnBack = findViewById(R.id.btnBack);
        LinearLayout itemReadPassage = findViewById(R.id.itemReadPassage);
        LinearLayout itemFillBlank = findViewById(R.id.itemFillBlank);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (itemReadPassage != null) {
            itemReadPassage.setOnClickListener(v -> {
                Intent intent = new Intent(ReadingPracticeActivity.this, PickTestActivity.class);
                startActivity(intent);
            });
        }

        if (itemFillBlank != null) {
            itemFillBlank.setOnClickListener(v -> {
                int generatedTestId = TestDataRepository.createRandomFillBlankTest();
                Intent intent = new Intent(ReadingPracticeActivity.this, DoingTestActivity.class);
                intent.putExtra("TEST_ID", generatedTestId);
                intent.putExtra("IS_FILL_BLANK_MODE", true);
                startActivity(intent);
            });
        }

        LinearLayout itemSentenceArrange = findViewById(R.id.itemSentenceArrange);
        if (itemSentenceArrange != null) {
            itemSentenceArrange.setOnClickListener(v -> {
                int generatedTestId = TestDataRepository.createRandomSentenceArrangeTest();
                Intent intent = new Intent(ReadingPracticeActivity.this, SentenceArrangeActivity.class);
                intent.putExtra("TEST_ID", generatedTestId);
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
