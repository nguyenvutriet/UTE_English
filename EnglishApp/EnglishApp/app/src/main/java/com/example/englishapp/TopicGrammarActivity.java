package com.example.englishapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TopicGrammarActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TopicAdapterGrammar adapter;
    ArrayList<TopicGrammar> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_grammar);

        recyclerView = findViewById(R.id.recyclerTopic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = TopicGrammarData.getTopics();

        adapter = new TopicAdapterGrammar(this, list);

        recyclerView.setAdapter(adapter);
    }
}