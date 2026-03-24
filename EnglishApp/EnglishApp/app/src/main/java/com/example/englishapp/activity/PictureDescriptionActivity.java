package com.example.englishapp.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.DatabaseHelper;
import com.example.englishapp.ListeningQuestion;
import com.example.englishapp.R;
import com.example.englishapp.TestDataRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PictureDescriptionActivity extends AppCompatActivity {
    private ImageView imgPicture;
    private ImageView btnBack;
    private TextView txtQuestion;
    private LinearLayout optionsContainer;
    private ProgressBar progressBar;
    private TextView txtProgress;
    private LinearLayout resultLayout;
    private TextView txtResult;
    private TextView txtEnglishText;
    private TextView txtVietnameseTranslation;
    private Button btnReplayAudio;
    private Button btnNext;

    private List<ListeningQuestion> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private MediaPlayer mediaPlayer;
    private boolean isAnswered = false;
    private int testId = 1;
    private boolean resultSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_picture_description);

        // Get test ID from intent, default to 1
        if (getIntent() != null && getIntent().hasExtra("picture_test_id")) {
            testId = getIntent().getIntExtra("picture_test_id", 1);
        }

        applyWindowInsets();
        initializeViews();
        loadQuestions();
        displayQuestion();
    }

    private void initializeViews() {
        imgPicture = findViewById(R.id.imgPicture);
        btnBack = findViewById(R.id.btnBack);
        txtQuestion = findViewById(R.id.txtQuestion);
        optionsContainer = findViewById(R.id.optionsContainer);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        resultLayout = findViewById(R.id.resultLayout);
        txtResult = findViewById(R.id.txtResult);
        txtEnglishText = findViewById(R.id.txtEnglishText);
        txtVietnameseTranslation = findViewById(R.id.txtVietnameseTranslation);
        btnReplayAudio = findViewById(R.id.btnReplayAudio);
        btnNext = findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v -> finish());
        btnReplayAudio.setOnClickListener(v -> playAudio());
        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestions() {
        List<ListeningQuestion> allQuestions;

        // Load questions based on test ID
        switch (testId) {
            case 2:
                allQuestions = TestDataRepository.buildPictureDescriptionQuestionsTest2();
                break;
            case 3:
                allQuestions = TestDataRepository.buildPictureDescriptionQuestionsTest3();
                break;
            default:
                allQuestions = TestDataRepository.buildPictureDescriptionQuestions();
                break;
        }

        // Không shuffle - giữ thứ tự từ Test 1, 2, 3 tương ứng
        questions = new ArrayList<>(allQuestions.subList(0, Math.min(6, allQuestions.size())));
        progressBar.setMax(questions.size());
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showResults();
            return;
        }

        ListeningQuestion question = questions.get(currentQuestionIndex);
        isAnswered = false;

        // Update progress
        txtProgress.setText((currentQuestionIndex + 1) + "/" + questions.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        // Display image
        imgPicture.setImageResource(question.getImageResourceId());

        // Display question
        txtQuestion.setText(question.getQuestionText());

        // Clear previous options
        optionsContainer.removeAllViews();

        // Display options
        for (int i = 0; i < question.getOptions().size(); i++) {
            View optionView = createOptionView(i, question.getCorrectOptionIndex());
            optionsContainer.addView(optionView);
        }

        // Hide result layout
        resultLayout.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnReplayAudio.setEnabled(false);

        // Auto play audio
        playAudio();
    }

    private View createOptionView(int optionIndex, int correctIndex) {
        Button button = new Button(this);
        char letter = (char) ('A' + optionIndex);
        button.setText(String.valueOf(letter));
        button.setTextSize(18);
        button.setPadding(16, 16, 16, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 12);
        button.setLayoutParams(params);

        if (!isAnswered) {
            button.setBackgroundResource(R.drawable.bg_option_button);
            button.setTextColor(getResources().getColor(R.color.text_dark));
        }

        button.setOnClickListener(v -> {
            if (!isAnswered) {
                isAnswered = true;
                selectAnswer(optionIndex, correctIndex);
            }
        });

        return button;
    }

    private void selectAnswer(int selectedIndex, int correctIndex) {
        boolean isCorrect = selectedIndex == correctIndex;

        if (isCorrect) {
            correctAnswers++;
            txtResult.setText("✓ Chính xác!");
            txtResult.setTextColor(getResources().getColor(R.color.success_green));
        } else {
            txtResult.setText("✗ Không chính xác!");
            txtResult.setTextColor(getResources().getColor(R.color.error_red));
        }

        ListeningQuestion currentQuestion = questions.get(currentQuestionIndex);
        txtEnglishText.setText(currentQuestion.getExplanation());
        txtVietnameseTranslation.setText(buildVietnameseExplanation(currentQuestion.getOptions()));

        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            Button btn = (Button) optionsContainer.getChildAt(i);
            if (i == correctIndex) {
                btn.setBackgroundResource(R.drawable.bg_option_correct);
                btn.setTextColor(getResources().getColor(android.R.color.white));
            } else if (i == selectedIndex && !isCorrect) {
                btn.setBackgroundResource(R.drawable.bg_option_wrong);
                btn.setTextColor(getResources().getColor(android.R.color.white));
            }
            btn.setEnabled(false);
        }

        resultLayout.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnReplayAudio.setEnabled(true);
    }

    private String buildVietnameseExplanation(List<String> options) {
        String[] labels = new String[]{"(A)", "(B)", "(C)", "(D)"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < options.size() && i < labels.length; i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(labels[i]).append(" ").append(options.get(i));
        }
        return builder.toString();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void playAudio() {
        if (currentQuestionIndex >= questions.size()) {
            return;
        }

        ListeningQuestion question = questions.get(currentQuestionIndex);
        int audioResId = question.getAudioResourceId();

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, audioResId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void showResults() {
        int total = questions.size();
        int percentage = total == 0 ? 0 : (correctAnswers * 100) / total;

        saveResultToDatabase(correctAnswers, total);

        Intent intent = new Intent(this, PictureDescriptionResultActivity.class);
        intent.putExtra("score", correctAnswers);
        intent.putExtra("total", total);
        intent.putExtra("percentage", percentage);
        startActivity(intent);
        finish();
    }

    private void saveResultToDatabase(int score, int totalQuestions) {
        if (resultSaved) {
            return;
        }

        String title = "Nghe tranh mo ta - De " + testId;
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long rowId = dbHelper.insertTestResult(2000 + testId, title, score, totalQuestions, currentDate);
        if (rowId > 0) {
            resultSaved = true;
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
