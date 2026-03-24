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

public class ListeningPracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_practice);

        applyWindowInsets();
        setupHeaderActions();
        setupClickListeners();
    }

    private void setupHeaderActions() {
        ImageView btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupClickListeners() {
        // Picture Description - First option
        LinearLayout pictureDescriptionLayout = findViewById(R.id.pictureDescriptionLayout);
        if (pictureDescriptionLayout != null) {
            pictureDescriptionLayout.setOnClickListener(v -> {
                startActivity(new Intent(ListeningPracticeActivity.this, PictureDescriptionTestSelectActivity.class));
            });
        }

        // Điền từ còn thiếu - Fill in the blanks option
        LinearLayout fillInBlanksLayout = findViewById(R.id.fillInBlanksLayout);
        if (fillInBlanksLayout != null) {
            fillInBlanksLayout.setOnClickListener(v -> {
                startActivity(new Intent(this, FillInTheBlanksActivity.class));
            });
        }

        // Nghe đoạn văn - random 1/5 listening passage test
        LinearLayout listeningPassageLayout = findViewById(R.id.listeningPassageLayout);
        if (listeningPassageLayout != null) {
            listeningPassageLayout.setOnClickListener(v ->
                    startActivity(new Intent(this, ListeningPassageTestSelectActivity.class))
            );
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
