package com.example.englishapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    ListView listTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        listTodo = findViewById(R.id.listTodo);

        ArrayList<String> tasks =
                getIntent().getStringArrayListExtra("TASKS");

        if(tasks == null){
            tasks = new ArrayList<>();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasks
        );

        listTodo.setAdapter(adapter);
    }
}