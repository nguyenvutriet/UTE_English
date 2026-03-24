package com.example.englishapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class DoingTestActivity extends AppCompatActivity {

    private ReadingTest currentTest;
    private LinearLayout questionsContainer;
    private final List<RadioGroup> questionRadioGroups = new ArrayList<>();

    private Button btnSubmit;
    private TextView txtScore;
    private ProgressBar progressQuestionBar;
    private TextView txtQuestionProgress;
    private LinearLayout questionProgressLayout;

    private boolean isFillBlankMode;
    private int currentQuestionIndex = 0;
    private int answeredCount = 0;
    private int score = 0;
    private boolean currentQuestionAnswered = false;
    private boolean resultSaved = false;
    private boolean isSentenceArrangeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doing_test);

        applyWindowInsets();
        setupBackButton();

        int testId = getIntent().getIntExtra("TEST_ID", -1);
        isFillBlankMode = getIntent().getBooleanExtra("IS_FILL_BLANK_MODE", false);
        isSentenceArrangeMode = getIntent().getBooleanExtra("IS_SENTENCE_ARRANGE_MODE", false);

        if (testId == -1) {
            Toast.makeText(this, "Không tìm thấy bài kiểm tra", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentTest = TestDataRepository.getTestById(testId);
        if (currentTest == null) {
            Toast.makeText(this, "Bài kiểm tra không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
    }

    private void setupUI() {
        TextView txtTestTitle = findViewById(R.id.txtTestTitle);
        TextView txtPassage = findViewById(R.id.txtPassage);
        questionsContainer = findViewById(R.id.questionsContainer);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtScore = findViewById(R.id.txtScore);
        progressQuestionBar = findViewById(R.id.progressQuestionBar);
        txtQuestionProgress = findViewById(R.id.txtQuestionProgress);
        questionProgressLayout = findViewById(R.id.questionProgressLayout);

        txtTestTitle.setText(currentTest.getTitle());
        txtPassage.setText(currentTest.getPassage());

        if (isFillBlankMode) {
            setupSingleQuestionMode();
            return;
        }

        setupFullFormMode();
    }

    private void setupFullFormMode() {
        if (questionProgressLayout != null) {
            questionProgressLayout.setVisibility(View.GONE);
        }

        questionRadioGroups.clear();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < currentTest.getQuestions().size(); i++) {
            ReadingTest.Question q = currentTest.getQuestions().get(i);
            View questionView = inflater.inflate(R.layout.item_question, questionsContainer, false);

            TextView txtQuestionNumber = questionView.findViewById(R.id.txtQuestionNumber);
            TextView txtQuestionContent = questionView.findViewById(R.id.txtQuestionContent);
            RadioGroup radioGroup = questionView.findViewById(R.id.radioGroupOptions);
            RadioButton optionA = questionView.findViewById(R.id.radioOptionA);
            RadioButton optionB = questionView.findViewById(R.id.radioOptionB);
            RadioButton optionC = questionView.findViewById(R.id.radioOptionC);
            RadioButton optionD = questionView.findViewById(R.id.radioOptionD);

            txtQuestionNumber.setText("Câu hỏi " + (i + 1));
            txtQuestionContent.setText(q.getQuestionText());

            List<String> options = q.getOptions();
            if (options.size() >= 4) {
                optionA.setText(options.get(0));
                optionB.setText(options.get(1));
                optionC.setText(options.get(2));
                optionD.setText(options.get(3));
            }

            radioGroup.setTag(i);
            questionRadioGroups.add(radioGroup);
            questionsContainer.addView(questionView);
        }

        btnSubmit.setText("Nộp bài");
        btnSubmit.setEnabled(true);
        btnSubmit.setOnClickListener(v -> checkAnswers());
    }

    private void setupSingleQuestionMode() {
        if (questionProgressLayout != null) {
            questionProgressLayout.setVisibility(View.VISIBLE);
        }

        int totalQuestions = currentTest.getQuestions().size();
        if (progressQuestionBar != null) {
            progressQuestionBar.setMax(totalQuestions);
            progressQuestionBar.setProgress(0);
        }
        if (txtQuestionProgress != null) {
            txtQuestionProgress.setText("0/" + totalQuestions);
        }

        btnSubmit.setText("Câu tiếp theo");
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(v -> goToNextQuestion());

        renderCurrentQuestion();
    }

    private void renderCurrentQuestion() {
        questionsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.item_question, questionsContainer, false);

        ReadingTest.Question q = currentTest.getQuestions().get(currentQuestionIndex);
        int totalQuestions = currentTest.getQuestions().size();

        TextView txtQuestionNumber = questionView.findViewById(R.id.txtQuestionNumber);
        TextView txtQuestionContent = questionView.findViewById(R.id.txtQuestionContent);
        RadioGroup radioGroup = questionView.findViewById(R.id.radioGroupOptions);
        RadioButton optionA = questionView.findViewById(R.id.radioOptionA);
        RadioButton optionB = questionView.findViewById(R.id.radioOptionB);
        RadioButton optionC = questionView.findViewById(R.id.radioOptionC);
        RadioButton optionD = questionView.findViewById(R.id.radioOptionD);

        txtQuestionNumber.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        txtQuestionContent.setText(q.getQuestionText());

        List<String> options = q.getOptions();
        if (options.size() >= 4) {
            optionA.setText(options.get(0));
            optionB.setText(options.get(1));
            optionC.setText(options.get(2));
            optionD.setText(options.get(3));
        }

        currentQuestionAnswered = false;
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!currentQuestionAnswered) {
                handleSingleQuestionAnswer(questionView, radioGroup, q, checkedId);
            }
        });

        questionsContainer.addView(questionView);
    }

    private void handleSingleQuestionAnswer(View questionView, RadioGroup radioGroup, ReadingTest.Question q, int checkedId) {
        int selectedIndex = getSelectedIndexFromCheckedId(checkedId);
        if (selectedIndex == -1) {
            return;
        }

        currentQuestionAnswered = true;
        answeredCount++;

        boolean isCorrect = selectedIndex == q.getCorrectOptionIndex();
        if (isCorrect) {
            score++;
        }

        highlightOptions(radioGroup, q.getCorrectOptionIndex(), selectedIndex);

        LinearLayout explanationLayout = questionView.findViewById(R.id.explanationLayout);
        TextView txtResultStatus = questionView.findViewById(R.id.txtResultStatus);
        TextView txtExplanation = questionView.findViewById(R.id.txtExplanation);

        explanationLayout.setVisibility(View.VISIBLE);
        if (isCorrect) {
            txtResultStatus.setText("Chính xác!");
            txtResultStatus.setTextColor(Color.parseColor("#299D2D"));
        } else {
            txtResultStatus.setText("Sai rồi. Đáp án đúng là: " + getOptionLetter(q.getCorrectOptionIndex()));
            txtResultStatus.setTextColor(Color.parseColor("#D32F2F"));
        }
        txtExplanation.setText(q.getExplanation());

        disableRadioGroup(radioGroup);
        updateProgress();

        if (currentQuestionIndex == currentTest.getQuestions().size() - 1) {
            btnSubmit.setText("Xem kết quả");
        } else {
            btnSubmit.setText("Câu tiếp theo");
        }
        btnSubmit.setEnabled(true);
    }

    private void goToNextQuestion() {
        if (!currentQuestionAnswered) {
            Toast.makeText(this, "Hãy chọn đáp án trước", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentQuestionIndex == currentTest.getQuestions().size() - 1) {
            showFinalScoreForSingleMode();
            return;
        }

        currentQuestionIndex++;
        btnSubmit.setEnabled(false);
        renderCurrentQuestion();
    }

    private void updateProgress() {
        int totalQuestions = currentTest.getQuestions().size();
        if (progressQuestionBar != null) {
            progressQuestionBar.setProgress(answeredCount);
        }
        if (txtQuestionProgress != null) {
            txtQuestionProgress.setText(answeredCount + "/" + totalQuestions);
        }
    }

    private void showFinalScoreForSingleMode() {
        int totalQuestions = currentTest.getQuestions().size();

        txtScore.setVisibility(View.VISIBLE);
        txtScore.setText("Điểm: " + score + "/" + totalQuestions);

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đã hoàn thành");
        btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));

        saveResultToDatabase(score, totalQuestions);
    }

    private void checkAnswers() {
        int localScore = 0;
        int totalQuestions = currentTest.getQuestions().size();

        for (int i = 0; i < totalQuestions; i++) {
            ReadingTest.Question q = currentTest.getQuestions().get(i);
            RadioGroup radioGroup = questionRadioGroups.get(i);
            View questionView = questionsContainer.getChildAt(i);

            LinearLayout explanationLayout = questionView.findViewById(R.id.explanationLayout);
            TextView txtResultStatus = questionView.findViewById(R.id.txtResultStatus);
            TextView txtExplanation = questionView.findViewById(R.id.txtExplanation);

            int checkedId = radioGroup.getCheckedRadioButtonId();
            int selectedIndex = getSelectedIndexFromCheckedId(checkedId);

            boolean isCorrect = selectedIndex == q.getCorrectOptionIndex();
            if (isCorrect) {
                localScore++;
            }

            highlightOptions(radioGroup, q.getCorrectOptionIndex(), selectedIndex);

            explanationLayout.setVisibility(View.VISIBLE);
            if (isCorrect) {
                txtResultStatus.setText("Chính xác!");
                txtResultStatus.setTextColor(Color.parseColor("#299D2D"));
            } else {
                txtResultStatus.setText("Sai rồi. Đáp án đúng là: " + getOptionLetter(q.getCorrectOptionIndex()));
                txtResultStatus.setTextColor(Color.parseColor("#D32F2F"));
            }
            txtExplanation.setText(q.getExplanation());

            disableRadioGroup(radioGroup);
        }

        txtScore.setVisibility(View.VISIBLE);
        txtScore.setText("Điểm: " + localScore + "/" + totalQuestions);

        btnSubmit.setEnabled(false);
        btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));

        saveResultToDatabase(localScore, totalQuestions);
    }

    private void saveResultToDatabase(int scoreValue, int totalQuestions) {
        if (resultSaved) {
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());
        int currentTestId = getIntent().getIntExtra("TEST_ID", -1);
        long rowId = dbHelper.insertTestResult(currentTestId, currentTest.getTitle(), scoreValue, totalQuestions, currentDate);

        if (rowId > 0) {
            resultSaved = true;
            Toast.makeText(this, "Đã lưu kết quả bài làm!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể lưu kết quả bài làm.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedIndexFromCheckedId(int checkedId) {
        if (checkedId == R.id.radioOptionA) {
            return 0;
        }
        if (checkedId == R.id.radioOptionB) {
            return 1;
        }
        if (checkedId == R.id.radioOptionC) {
            return 2;
        }
        if (checkedId == R.id.radioOptionD) {
            return 3;
        }
        return -1;
    }

    private String getOptionLetter(int index) {
        switch (index) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return "?";
        }
    }

    private void disableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void highlightOptions(RadioGroup radioGroup, int correctIndex, int selectedIndex) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (i == correctIndex) {
                rb.setTextColor(Color.parseColor("#299D2D"));
                rb.setTypeface(null, android.graphics.Typeface.BOLD);
            } else if (i == selectedIndex) {
                rb.setTextColor(Color.parseColor("#D32F2F"));
            }
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
