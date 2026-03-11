package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    TextView txtQuestion;
    RadioButton radioA, radioB, radioC;
    Button btnNext;

    ArrayList<Question> list;
    int index = 0;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtQuestion = findViewById(R.id.txtQuestion);
        radioA = findViewById(R.id.radioA);
        radioB = findViewById(R.id.radioB);
        radioC = findViewById(R.id.radioC);
        btnNext = findViewById(R.id.btnNext);

        list = QuizData.getQuestions();

        loadQuestion();

        btnNext.setOnClickListener(v -> checkAnswer());
    }

    void loadQuestion(){

        Question q = list.get(index);

        txtQuestion.setText(q.getQuestion());
        radioA.setText(q.getOptionA());
        radioB.setText(q.getOptionB());
        radioC.setText(q.getOptionC());
        // reset lựa chọn
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
    }

    void checkAnswer(){

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        RadioButton selected = findViewById(radioGroup.getCheckedRadioButtonId());

        if(selected != null){

            String answer = selected.getText().toString();

            // lưu đáp án người dùng
            list.get(index).setUserAnswer(answer);

            // kiểm tra đúng sai
            if(answer.equals(list.get(index).getCorrect())){
                score++;
            }

            index++;

            if(index < list.size()){
                loadQuestion();
            }else{

                // mở màn hình kết quả
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("score",score);

                QuizData.resultList = list;

                startActivity(intent);
                finish();
            }
        }
    }
}
