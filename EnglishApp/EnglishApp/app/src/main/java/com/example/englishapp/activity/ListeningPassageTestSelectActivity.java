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

import com.example.englishapp.R;

public class ListeningPassageTestSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_passage_test_select);

        applyWindowInsets();
        setupActions();
    }

    private void setupActions() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        bindTestClick(R.id.test1Layout, 1);
        bindTestClick(R.id.test2Layout, 2);
        bindTestClick(R.id.test3Layout, 3);
        bindTestClick(R.id.test4Layout, 4);
        bindTestClick(R.id.test5Layout, 5);
    }

    private void bindTestClick(int viewId, int testId) {
        LinearLayout item = findViewById(viewId);
        if (item != null) {
            item.setOnClickListener(v -> startSelectedTest(testId));
        }
    }

    private void startSelectedTest(int testId) {
        Intent intent = new Intent(this, ListeningPassageActivity.class);
        intent.putExtra(ListeningPassageActivity.EXTRA_LISTENING_TEST_ID, testId);
        startActivity(intent);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

