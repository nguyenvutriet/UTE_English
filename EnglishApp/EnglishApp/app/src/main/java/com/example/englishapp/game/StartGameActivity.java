package com.example.englishapp.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;

public class StartGameActivity extends AppCompatActivity {

    EditText edtName;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_start);

        edtName = findViewById(R.id.edtName);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();

            // bắt buộc nhập tên
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

            // chuyển màn hình
            Intent intent = new Intent(StartGameActivity.this, DifficultyActivity.class);
            intent.putExtra("player", name);
            startActivity(intent);

        });
    }
}