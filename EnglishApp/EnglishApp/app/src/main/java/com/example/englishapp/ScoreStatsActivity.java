package com.example.englishapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ScoreStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score_stats);

        applyWindowInsets();
        setupBackButton();
        calculateAndDisplayStats();
    }

    private void calculateAndDisplayStats() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<DatabaseHelper.TestHistoryRecord> records = db.getAllHistory();

        int totalTests = records.size();
        int totalCorrect = 0;
        int totalQuestions = 0;

        for (DatabaseHelper.TestHistoryRecord record : records) {
            totalCorrect += record.getScore();
            totalQuestions += record.getTotalQuestions();
        }

        TextView txtTotalTests = findViewById(R.id.txtTotalTests);
        TextView txtAverageAccuracy = findViewById(R.id.txtAverageAccuracy);
        TextView txtTotalCorrect = findViewById(R.id.txtTotalCorrect);
        TextView txtTotalQuestions = findViewById(R.id.txtTotalQuestions);

        txtTotalTests.setText(String.valueOf(totalTests));
        txtTotalCorrect.setText(String.valueOf(totalCorrect));
        txtTotalQuestions.setText(String.valueOf(totalQuestions));

        if (totalQuestions > 0) {
            int averageAccuracy = (int) (((double) totalCorrect / totalQuestions) * 100);
            txtAverageAccuracy.setText(averageAccuracy + "%");
        } else {
            txtAverageAccuracy.setText("0%");
        }
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
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
