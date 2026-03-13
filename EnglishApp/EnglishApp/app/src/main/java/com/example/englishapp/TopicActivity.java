package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {

    private TopicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        RecyclerView recycler = findViewById(R.id.topicRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TopicAdapter(new ArrayList<>(), topic -> {
            Intent intent = new Intent(TopicActivity.this, TopicDetailActivity.class);
            intent.putExtra(TopicDetailActivity.EXTRA_TOPIC_ID, topic.id);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTopics();
    }

    private void refreshTopics() {
        List<Topic> topics = TopicRepository.getTopics();
        for (Topic topic : topics) {
            List<TopicWord> words = TopicRepository.getWordsForTopic(topic.id);
            topic.total = words.size();
            topic.learned = TopicProgressStore.getLearnedCount(this, topic.id, words);
        }
        adapter.updateTopics(topics);
    }
}