package com.example.englishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.R;

public class PictureDescriptionTestSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_picture_description_test_select);

        applyWindowInsets();
        setupActions();
    }

    private void setupActions() {
        ImageView btnBack = findViewById(R.id.btnBack);
        LinearLayout test1Layout = findViewById(R.id.test1Layout);
        LinearLayout test2Layout = findViewById(R.id.test2Layout);
        LinearLayout test3Layout = findViewById(R.id.test3Layout);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (test1Layout != null) {
            test1Layout.setOnClickListener(v -> {
                Intent intent = new Intent(this, PictureDescriptionActivity.class);
                intent.putExtra("picture_test_id", 1);
                startActivity(intent);
            });
        }

        if (test2Layout != null) {
            test2Layout.setOnClickListener(v -> {
                Intent intent = new Intent(this, PictureDescriptionActivity.class);
                intent.putExtra("picture_test_id", 2);
                startActivity(intent);
            });
        }

        if (test3Layout != null) {
            test3Layout.setOnClickListener(v -> {
                Intent intent = new Intent(this, PictureDescriptionActivity.class);
                intent.putExtra("picture_test_id", 3);
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

