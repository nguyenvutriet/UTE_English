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

            String email = ((android.widget.EditText)findViewById(R.id.etEmail))
                    .getText().toString().trim();

            String password = ((android.widget.EditText)findViewById(R.id.etPassword))
                    .getText().toString().trim();

            android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

            // admin
            if (email.equals("admin") && password.equals("123")) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                return;
            }

            String json = prefs.getString("users", "[]");

            try {
                org.json.JSONArray array = new org.json.JSONArray(json);

                for (int i = 0; i < array.length(); i++) {

                    org.json.JSONObject user = array.getJSONObject(i);

                    if (user.getString("email").equals(email)
                            && user.getString("password").equals(password)) {

                        prefs.edit().putBoolean("isLogin", true).apply();

                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                        return;
                    }
                }

                android.widget.Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", android.widget.Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}