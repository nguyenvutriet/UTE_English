package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

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
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegister).setOnClickListener(v -> {

            String name = ((android.widget.EditText)findViewById(R.id.etFullName))
                    .getText().toString().trim();

            String email = ((android.widget.EditText)findViewById(R.id.etEmail))
                    .getText().toString().trim();

            String password = ((android.widget.EditText)findViewById(R.id.etPassword))
                    .getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Nhập đầy đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

            String json = prefs.getString("users", "[]");

            try {
                org.json.JSONArray array = new org.json.JSONArray(json);

                // ❌ check trùng email
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject obj = array.getJSONObject(i);

                    if (obj.getString("email").equals(email)) {
                        android.widget.Toast.makeText(this, "Email đã tồn tại", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // ✅ thêm user mới
                org.json.JSONObject newUser = new org.json.JSONObject();
                newUser.put("name", name);
                newUser.put("email", email);
                newUser.put("password", password);

                array.put(newUser);

                prefs.edit().putString("users", array.toString()).apply();

                android.widget.Toast.makeText(this, "Đăng ký thành công", android.widget.Toast.LENGTH_SHORT).show();

                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}

