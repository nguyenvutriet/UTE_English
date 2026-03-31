package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        applyWindowInsets();
        setupActions();
    }

    private void applyWindowInsets() {
        android.view.View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupActions() {
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            EditText etEmail = findViewById(R.id.etEmail);
            String rawInput = etEmail.getText().toString().trim();
            String username = extractUsername(rawInput);

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập username hoặc email", Toast.LENGTH_SHORT).show();
                return;
            }

            Class<?> destination = username.equals("admin")
                    ? AdminDashboardActivity.class
                    : HomeActivity.class;

            startActivity(new Intent(MainActivity.this, destination));
            finish();
        });

        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private String extractUsername(String input) {
        if (input == null) {
            return "";
        }
        String normalized = input.trim().toLowerCase(Locale.ROOT);
        int atIndex = normalized.indexOf('@');
        return atIndex > 0 ? normalized.substring(0, atIndex) : normalized;
    }
}