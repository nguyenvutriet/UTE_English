package com.example.englishapp.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.example.englishapp.game.data.FakeWordData;
import com.example.englishapp.game.model.Word;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    TextView txtWord;
    TextView txtScore;

    ProgressBar progressBar;

    Button btnA, btnB, btnC, btnD;

    ArrayList<Word> list;

    int index = 0;
    int score = 0;

    CountDownTimer timer;

    long timeLeft = 10000;

    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_quiz);

        txtWord = findViewById(R.id.txtWord);
        txtScore = findViewById(R.id.txtScore);

        progressBar = findViewById(R.id.progressTime);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);

        list = FakeWordData.getEasyWords();

        playerName = getIntent().getStringExtra("player");

        loadQuestion();

        btnA.setOnClickListener(v -> checkAnswer(btnA.getText().toString()));
        btnB.setOnClickListener(v -> checkAnswer(btnB.getText().toString()));
        btnC.setOnClickListener(v -> checkAnswer(btnC.getText().toString()));
        btnD.setOnClickListener(v -> checkAnswer(btnD.getText().toString()));
    }

    void loadQuestion() {

        if (index >= list.size()) {

            saveScore(playerName, score);

            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("player", playerName);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();

            return;
        }

        Word w = list.get(index);

        txtWord.setText(w.getEnglish());

        btnA.setText(w.getA());
        btnB.setText(w.getB());
        btnC.setText(w.getC());
        btnD.setText(w.getD());

        resetButtons();

        startTimer();
    }

    void startTimer() {

        progressBar.setProgress(100);

        timer = new CountDownTimer(10000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft = millisUntilFinished;

                int progress = (int) (millisUntilFinished / 100);
                progressBar.setProgress(progress);

                // ⏳ đổi màu khi còn 3s
                if (millisUntilFinished < 3000) {

                    progressBar.setProgressDrawable(
                            getDrawable(R.drawable.timer_red));

                } else {

                    progressBar.setProgressDrawable(
                            getDrawable(R.drawable.timer_progress));
                }
            }

            @Override
            public void onFinish() {

                index++;
                loadQuestion();

            }
        };

        timer.start();
    }

    void checkAnswer(String answer) {

        Word w = list.get(index);

        if (timer != null) {
            timer.cancel();
        }

        if (answer.equals(w.getCorrect())) {

            int bonus = (int) (timeLeft / 100);
            score += 10 + bonus;

            highlightCorrect(w.getCorrect());

            Animation anim =
                    AnimationUtils.loadAnimation(this, R.anim.correct_anim);

            btnA.startAnimation(anim);
            btnB.startAnimation(anim);
            btnC.startAnimation(anim);
            btnD.startAnimation(anim);

        } else {

            highlightWrong(answer, w.getCorrect());

            Animation anim =
                    AnimationUtils.loadAnimation(this, R.anim.wrong_anim);

            btnA.startAnimation(anim);
            btnB.startAnimation(anim);
            btnC.startAnimation(anim);
            btnD.startAnimation(anim);
        }

        txtScore.setText("Score: " + score);

        new Handler().postDelayed(() -> {

            index++;
            loadQuestion();

        }, 1000);
    }

    void highlightCorrect(String correct) {

        if (btnA.getText().toString().equals(correct))
            btnA.setBackgroundResource(R.drawable.answer_correct);

        if (btnB.getText().toString().equals(correct))
            btnB.setBackgroundResource(R.drawable.answer_correct);

        if (btnC.getText().toString().equals(correct))
            btnC.setBackgroundResource(R.drawable.answer_correct);

        if (btnD.getText().toString().equals(correct))
            btnD.setBackgroundResource(R.drawable.answer_correct);
    }

    void highlightWrong(String answer, String correct) {

        if (btnA.getText().toString().equals(answer))
            btnA.setBackgroundResource(R.drawable.answer_wrong);

        if (btnB.getText().toString().equals(answer))
            btnB.setBackgroundResource(R.drawable.answer_wrong);

        if (btnC.getText().toString().equals(answer))
            btnC.setBackgroundResource(R.drawable.answer_wrong);

        if (btnD.getText().toString().equals(answer))
            btnD.setBackgroundResource(R.drawable.answer_wrong);

        highlightCorrect(correct);
    }

    void resetButtons() {

        btnA.setBackgroundResource(R.drawable.answer_normal);
        btnB.setBackgroundResource(R.drawable.answer_normal);
        btnC.setBackgroundResource(R.drawable.answer_normal);
        btnD.setBackgroundResource(R.drawable.answer_normal);
    }

    void saveScore(String name, int score) {

        SharedPreferences prefs =
                getSharedPreferences("leaderboard", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(name, score);

        editor.apply();
    }
}