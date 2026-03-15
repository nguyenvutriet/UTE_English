package com.example.englishapp.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.HomeActivity;
import com.example.englishapp.R;

public class StartGameActivity extends AppCompatActivity {

    EditText edtName;
    Button btnContinue;
    ImageView btnBack3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_start);

        edtName = findViewById(R.id.edtName);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack3 = findViewById(R.id.btnBack3);

        if (btnBack3 != null) {
            btnBack3.setOnClickListener(v -> navigateToHome());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        btnContinue.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();

            // bat buoc nhap ten
            if(name.isEmpty()){

                edtName.setError("Please enter your name");
                edtName.requestFocus();

                Toast.makeText(
                        StartGameActivity.this,
                        "You must enter your name!",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // chuyen man hinh
            Intent intent = new Intent(StartGameActivity.this, DifficultyActivity.class);
            intent.putExtra("player", name);
            startActivity(intent);

        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(StartGameActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}