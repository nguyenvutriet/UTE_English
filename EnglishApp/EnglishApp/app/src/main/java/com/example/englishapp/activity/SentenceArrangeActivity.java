package com.example.englishapp.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.DatabaseHelper;
import com.example.englishapp.R;
import com.example.englishapp.TestDataRepository;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SentenceArrangeActivity extends AppCompatActivity {

    private FlexboxLayout droppedWordsContainer;
    private FlexboxLayout sourceWordsContainer;
    private Button btnSubmit;
    private TextView txtScore;
    private ProgressBar progressQuestionBar;
    private TextView txtQuestionProgress;
    private TextView txtResultStatus;
    private TextView txtExplanation;
    private TextView txtPlaceholder;
    private TextView txtVietnameseMeaning;

    private final List<TestDataRepository.SentenceArrangeQuestion> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean currentQuestionAnswered = false;
    private boolean resultSaved = false;
    private final List<String> currentDraggedWords = new ArrayList<>();
    private final List<Button> wordButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sentence_arrange);

        applyWindowInsets();
        setupBackButton();

        int testId = getIntent().getIntExtra("TEST_ID", -1);
        if (testId == -1) {
            Toast.makeText(this, "Không tìm thấy bài kiểm tra", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTestData();
        setupUI();
        if (!questions.isEmpty()) {
            renderCurrentQuestion();
        }
    }

    private void loadTestData() {
        questions.addAll(TestDataRepository.buildSentenceArrangeQuestionBank());
        Collections.shuffle(questions);

        int count = Math.min(10, questions.size());
        while (questions.size() > count) {
            questions.remove(questions.size() - 1);
        }
    }

    private void setupUI() {
        TextView txtTestTitle = findViewById(R.id.txtTestTitle);
        droppedWordsContainer = findViewById(R.id.droppedWordsContainer);
        sourceWordsContainer = findViewById(R.id.sourceWordsContainer);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtScore = findViewById(R.id.txtScore);
        progressQuestionBar = findViewById(R.id.progressQuestionBar);
        txtQuestionProgress = findViewById(R.id.txtQuestionProgress);
        LinearLayout resultLayout = findViewById(R.id.resultLayout);
        txtResultStatus = findViewById(R.id.txtResultStatus);
        txtExplanation = findViewById(R.id.txtExplanation);
        txtPlaceholder = findViewById(R.id.txtPlaceholder);
        txtVietnameseMeaning = findViewById(R.id.txtVietnameseMeaning);

        txtTestTitle.setText("Sắp xếp câu");

        if (progressQuestionBar != null) {
            progressQuestionBar.setMax(questions.size());
            progressQuestionBar.setProgress(0);
        }
        if (txtQuestionProgress != null) {
            txtQuestionProgress.setText("1/" + questions.size());
        }

        btnSubmit.setText("Kiểm tra");
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private void renderCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showFinalScore();
            return;
        }

        TestDataRepository.SentenceArrangeQuestion currentQuestion = questions.get(currentQuestionIndex);
        int totalQuestions = questions.size();

        TextView txtQuestionNumber = findViewById(R.id.txtQuestionNumber);
        txtQuestionNumber.setText("Câu " + (currentQuestionIndex + 1) + "/" + totalQuestions);

        if (progressQuestionBar != null) {
            progressQuestionBar.setProgress(currentQuestionIndex);
        }
        if (txtQuestionProgress != null) {
            txtQuestionProgress.setText((currentQuestionIndex + 1) + "/" + totalQuestions);
        }

        droppedWordsContainer.removeAllViews();
        sourceWordsContainer.removeAllViews();
        currentDraggedWords.clear();
        wordButtons.clear();
        currentQuestionAnswered = false;

        LinearLayout resultLayout = findViewById(R.id.resultLayout);
        if (resultLayout != null) {
            resultLayout.setVisibility(View.GONE);
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Kiểm tra");

        if (txtPlaceholder != null) {
            txtPlaceholder.setVisibility(View.VISIBLE);
        }

        renderSourceWords(currentQuestion);

        if (txtVietnameseMeaning != null) {
            txtVietnameseMeaning.setText("Nghĩa: " + TestDataRepository.getVietnameseMeaning(currentQuestion.getCorrectAnswer()));
        }
    }

    private void renderSourceWords(TestDataRepository.SentenceArrangeQuestion currentQuestion) {
        wordButtons.clear();
        sourceWordsContainer.removeAllViews();

        // Get all words from the correct answer
        String[] words = currentQuestion.getWords();
        List<String> shuffledWords = new ArrayList<>(Arrays.asList(words));
        Collections.shuffle(shuffledWords);

        for (String word : shuffledWords) {
            Button wordBtn = new Button(this);
            wordBtn.setText(word);
            wordBtn.setTextColor(Color.WHITE);
            wordBtn.setTextSize(14);
            wordBtn.setTypeface(null, android.graphics.Typeface.BOLD);
            wordBtn.setBackground(getDrawable(R.drawable.bg_word_button));
            wordBtn.setPadding(12, 8, 12, 8);
            wordBtn.setTag("active");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(6, 6, 6, 6);
            wordBtn.setLayoutParams(params);

            wordBtn.setOnClickListener(v -> onWordClicked(wordBtn, word));
            sourceWordsContainer.addView(wordBtn);
            wordButtons.add(wordBtn);
        }
    }

    private void onWordClicked(Button btn, String word) {
        if ("active".equals(btn.getTag())) {
            currentDraggedWords.add(word);
            btn.setBackground(getDrawable(R.drawable.bg_word_button_used));
            btn.setTag("used");
            btn.setEnabled(false);

            TextView wordView = new TextView(this);
            wordView.setText(word);
            wordView.setTextColor(Color.WHITE);
            wordView.setTextSize(14);
            wordView.setTypeface(null, android.graphics.Typeface.BOLD);
            wordView.setBackground(getDrawable(R.drawable.bg_word_button));
            wordView.setPadding(12, 8, 12, 8);
            wordView.setTag(word);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(6, 6, 6, 6);
            wordView.setLayoutParams(params);

            wordView.setOnClickListener(v -> onDroppedWordClicked(wordView, btn, word));
            droppedWordsContainer.addView(wordView);

            if (txtPlaceholder != null) {
                txtPlaceholder.setVisibility(View.GONE);
            }

            btnSubmit.setEnabled(!currentDraggedWords.isEmpty());
        }
    }

    private void onDroppedWordClicked(TextView wordView, Button btn, String word) {
        currentDraggedWords.remove(word);
        droppedWordsContainer.removeView(wordView);
        btn.setBackground(getDrawable(R.drawable.bg_word_button));
        btn.setTag("active");
        btn.setEnabled(true);

        if (currentDraggedWords.isEmpty() && txtPlaceholder != null) {
            txtPlaceholder.setVisibility(View.VISIBLE);
        }

        btnSubmit.setEnabled(!currentDraggedWords.isEmpty());
    }

    private void checkAnswer() {
        if (currentQuestionAnswered) {
            goToNextQuestion();
            return;
        }

        currentQuestionAnswered = true;
        TestDataRepository.SentenceArrangeQuestion currentQuestion = questions.get(currentQuestionIndex);

        String userAnswer = String.join(" ", currentDraggedWords);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
        if (isCorrect) {
            score++;
        }

        LinearLayout resultLayout = findViewById(R.id.resultLayout);
        if (resultLayout != null) {
            resultLayout.setVisibility(View.VISIBLE);
        }

        if (isCorrect) {
            txtResultStatus.setText("✓ Chính xác!");
            txtResultStatus.setTextColor(Color.parseColor("#299D2D"));
        } else {
            txtResultStatus.setText("✗ Sai rồi. Đáp án đúng: " + correctAnswer);
            txtResultStatus.setTextColor(Color.parseColor("#D32F2F"));
        }
        txtExplanation.setText("Giải thích: " + currentQuestion.getCorrectAnswer());

        disableAllWords();

        if (currentQuestionIndex == questions.size() - 1) {
            btnSubmit.setText("Xem kết quả");
        } else {
            btnSubmit.setText("Câu tiếp theo");
        }
        btnSubmit.setEnabled(true);
    }

    private void disableAllWords() {
        for (Button btn : wordButtons) {
            btn.setEnabled(false);
        }
    }

    private void goToNextQuestion() {
        if (currentQuestionIndex == questions.size() - 1) {
            showFinalScore();
            return;
        }

        currentQuestionIndex++;
        renderCurrentQuestion();
    }

    private void showFinalScore() {
        int totalQuestions = questions.size();
        txtScore.setVisibility(View.VISIBLE);
        txtScore.setText("Điểm: " + score + "/" + totalQuestions);

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đã hoàn thành");
        btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));

        saveResultToDatabase(score, totalQuestions);
    }

    private void saveResultToDatabase(int scoreValue, int totalQuestions) {
        if (resultSaved) {
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());
        int currentTestId = getIntent().getIntExtra("TEST_ID", -1);
        long rowId = dbHelper.insertTestResult(currentTestId, "Sắp xếp câu", scoreValue, totalQuestions, currentDate);

        if (rowId > 0) {
            resultSaved = true;
            Toast.makeText(this, "Đã lưu kết quả bài làm!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể lưu kết quả bài làm.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
