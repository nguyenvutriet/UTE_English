package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    TextView txtScore;
    RecyclerView recyclerView;
    MaterialButton btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtScore = findViewById(R.id.txtScore);
        recyclerView = findViewById(R.id.recyclerResult);
        btnRetry = findViewById(R.id.btnRetry);

        int score = getIntent().getIntExtra("score",0);

        txtScore.setText("Score: " + score + "/" + QuizData.resultList.size());

        ResultAdapter adapter = new ResultAdapter(this, QuizData.resultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Nút Retry Quiz
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
                startActivity(intent);

                finish(); // đóng màn result
            }
        });

    }
}
