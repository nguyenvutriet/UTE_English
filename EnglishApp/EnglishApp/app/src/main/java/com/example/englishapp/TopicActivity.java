package com.example.englishapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        RecyclerView recycler=findViewById(R.id.topicRecycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<Topic> list=new ArrayList<>();

        list.add(new Topic("Động vật",R.drawable.animal,10,0));
        list.add(new Topic("Cây cối",R.drawable.tree,10,0));
        list.add(new Topic("Quần áo",R.drawable.clothes,10,0));
        list.add(new Topic("Vật dụng bếp",R.drawable.kitchen,10,0));
        list.add(new Topic("Đồ ăn",R.drawable.food,10,0));
        list.add(new Topic("Lễ hội",R.drawable.festival,10,0));
        list.add(new Topic("Trái cây",R.drawable.fruit,10,0));
        list.add(new Topic("Phương tiện",R.drawable.vehicle,10,0));
        list.add(new Topic("Công nghệ",R.drawable.tech,10,0));

        TopicAdapter adapter=new TopicAdapter(list);

        recycler.setAdapter(adapter);

    }
}