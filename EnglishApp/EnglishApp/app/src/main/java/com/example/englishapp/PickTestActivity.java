package com.example.englishapp;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PickTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pick_test);

        applyWindowInsets();
        setupBackButton();
        setupTestCards();
    }

    private void setupTestCards() {
        androidx.cardview.widget.CardView cardTest1 = findViewById(R.id.cardTest1);
        androidx.cardview.widget.CardView cardTest2 = findViewById(R.id.cardTest2);
        androidx.cardview.widget.CardView cardTest3 = findViewById(R.id.cardTest3);

        if (cardTest1 != null) {
            cardTest1.setOnClickListener(v -> startTest(1));
        }
        if (cardTest2 != null) {
            cardTest2.setOnClickListener(v -> startTest(2));
        }
        if (cardTest3 != null) {
            cardTest3.setOnClickListener(v -> startTest(3));
        }
    }

    private void startTest(int testId) {
        android.content.Intent intent = new android.content.Intent(PickTestActivity.this, DoingTestActivity.class);
        intent.putExtra("TEST_ID", testId);
        startActivity(intent);
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
