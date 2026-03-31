package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        View rootView = findViewById(R.id.main);
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

            String username = ((android.widget.EditText)findViewById(R.id.etEmail))
                    .getText().toString().trim();

            String password = ((android.widget.EditText)findViewById(R.id.etPassword))
                    .getText().toString().trim();

            if (username.equals("admin") && password.equals("123")) {

                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                finish();

            } else if (username.equals("user") && password.equals("123")) {

                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();

            } else {

                android.widget.Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", android.widget.Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}