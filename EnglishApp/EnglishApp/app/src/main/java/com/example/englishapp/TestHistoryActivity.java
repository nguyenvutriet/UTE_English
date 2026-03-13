package com.example.englishapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class TestHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_history);

        applyWindowInsets();
        setupBackButton();
        loadHistory();
    }

    private void loadHistory() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<DatabaseHelper.TestHistoryRecord> records = db.getAllHistory();

        LinearLayout historyContainer = findViewById(R.id.historyContainer);
        TextView txtEmpty = findViewById(R.id.txtEmpty);

        if (records.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            historyContainer.setVisibility(View.GONE);
            return;
        }

        txtEmpty.setVisibility(View.GONE);
        historyContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (DatabaseHelper.TestHistoryRecord record : records) {
            View historyItem = inflater.inflate(R.layout.item_test_history, historyContainer, false);

            TextView txtTestTitle = historyItem.findViewById(R.id.txtTestTitle);
            TextView txtDateTaken = historyItem.findViewById(R.id.txtDateTaken);
            TextView txtScore = historyItem.findViewById(R.id.txtScore);

            txtTestTitle.setText(record.getTestTitle());
            txtDateTaken.setText(record.getDateTaken());
            txtScore.setText(record.getScore() + "/" + record.getTotalQuestions());

            historyContainer.addView(historyItem);
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
