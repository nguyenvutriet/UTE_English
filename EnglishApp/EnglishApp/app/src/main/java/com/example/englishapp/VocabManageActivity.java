package com.example.englishapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VocabManageActivity extends AppCompatActivity {

    private final ArrayList<String> vocabItems = new ArrayList<>();
    private ArrayAdapter<String> vocabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_manage);

        EditText etWord = findViewById(R.id.etWord);
        EditText etMeaning = findViewById(R.id.etMeaning);
        EditText etExample = findViewById(R.id.etExample);
        Spinner spTopic = findViewById(R.id.spTopic);
        ListView lvVocab = findViewById(R.id.lvVocab);
        MaterialButton btnSaveWord = findViewById(R.id.btnSaveWord);
        MaterialButton btnBack = findViewById(R.id.btnBackVocabDashboard);

        setupTopicSpinner(spTopic);
        setupFakeList(lvVocab);

        btnSaveWord.setOnClickListener(v -> {
            String word = etWord.getText().toString().trim();
            String meaning = etMeaning.getText().toString().trim();
            String example = etExample.getText().toString().trim();
            String topic = String.valueOf(spTopic.getSelectedItem());

            if (word.isEmpty() || meaning.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ và nghĩa", Toast.LENGTH_SHORT).show();
                return;
            }

            if (example.isEmpty()) {
                example = "No example";
            }

            String display = word + " - " + meaning + " | " + topic + "\n" + "VD: " + example;
            vocabItems.add(0, display);
            vocabAdapter.notifyDataSetChanged();

            etWord.setText("");
            etMeaning.setText("");
            etExample.setText("");
            Toast.makeText(this, "Đã lưu từ vựng (fake)", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupTopicSpinner(Spinner spinner) {
        List<String> topics = Arrays.asList("Daily Life", "Business", "Travel", "Education");
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                topics
        );
        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(topicAdapter);
    }

    private void setupFakeList(ListView listView) {
        vocabItems.add("assignment - bài tập | Education\nVD: I have to finish my assignment tonight.");
        vocabItems.add("lecture - bài giảng | Education\nVD: The lecture was very interesting.");
        vocabItems.add("scholarship - học bổng | Education\nVD: She received a scholarship to study abroad.");
        vocabItems.add("curriculum - chương trình học | Education\nVD: The curriculum includes many practical subjects.");

        vocabItems.add("salary - lương | Business\nVD: His salary increases every year.");
        vocabItems.add("interview - phỏng vấn | Business\nVD: I have a job interview tomorrow.");
        vocabItems.add("promotion - thăng chức | Business\nVD: She got a promotion last month.");
        vocabItems.add("colleague - đồng nghiệp | Business\nVD: My colleagues are very friendly.");

        vocabItems.add("destination - điểm đến | Travel\nVD: Paris is a popular tourist destination.");
        vocabItems.add("luggage - hành lý | Travel\nVD: Don't forget your luggage.");
        vocabItems.add("reservation - đặt chỗ | Travel\nVD: I made a hotel reservation online.");
        vocabItems.add("passport - hộ chiếu | Travel\nVD: You need a passport to travel abroad.");

        vocabAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                vocabItems
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                if (textView != null) {
                    textView.setTextColor(Color.parseColor("#111827"));
                }
                return view;
            }
        };
        listView.setAdapter(vocabAdapter);
    }
}
