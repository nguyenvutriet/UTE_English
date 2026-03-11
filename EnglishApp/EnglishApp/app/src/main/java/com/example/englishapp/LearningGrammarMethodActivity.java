package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LearningGrammarMethodActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GrammarAdapter adapter;
    ArrayList<Grammar> list;
    TextView txtTopicTitle;
    Button btnQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_grammar_method);

        recyclerView = findViewById(R.id.recyclerGrammar);
        txtTopicTitle = findViewById(R.id.txtTopicTitle);
        btnQuiz = findViewById(R.id.btnQuiz);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String topic = getIntent().getStringExtra("topic");
        txtTopicTitle.setText(topic);

        list = GrammarData.getGrammarList();

        adapter = new GrammarAdapter(this, list);

        recyclerView.setAdapter(adapter);

        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(LearningGrammarMethodActivity.this, QuizActivity.class);
            startActivity(intent);
        });
    }
}