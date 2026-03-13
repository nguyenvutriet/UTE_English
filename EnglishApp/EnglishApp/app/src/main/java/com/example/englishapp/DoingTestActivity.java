package com.example.englishapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private List<RadioGroup> questionRadioGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doing_test);

        applyWindowInsets();
        setupBackButton();

        int testId = getIntent().getIntExtra("TEST_ID", -1);
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
        Button btnSubmit = findViewById(R.id.btnSubmit);

        txtTestTitle.setText(currentTest.getTitle());
        txtPassage.setText(currentTest.getPassage());

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

            // Assign tag to the radio group to know which question it belongs to
            radioGroup.setTag(i);
            questionRadioGroups.add(radioGroup);

            questionsContainer.addView(questionView);
        }

        btnSubmit.setOnClickListener(v -> checkAnswers());
    }

    private void checkAnswers() {
        int score = 0;
        int totalQuestions = currentTest.getQuestions().size();

        for (int i = 0; i < totalQuestions; i++) {
            ReadingTest.Question q = currentTest.getQuestions().get(i);
            RadioGroup radioGroup = questionRadioGroups.get(i);
            View questionView = questionsContainer.getChildAt(i);
            
            LinearLayout explanationLayout = questionView.findViewById(R.id.explanationLayout);
            TextView txtResultStatus = questionView.findViewById(R.id.txtResultStatus);
            TextView txtExplanation = questionView.findViewById(R.id.txtExplanation);
            
            int checkedId = radioGroup.getCheckedRadioButtonId();
            int selectedIndex = -1;
            
            if (checkedId == R.id.radioOptionA) selectedIndex = 0;
            else if (checkedId == R.id.radioOptionB) selectedIndex = 1;
            else if (checkedId == R.id.radioOptionC) selectedIndex = 2;
            else if (checkedId == R.id.radioOptionD) selectedIndex = 3;

            boolean isCorrect = selectedIndex == q.getCorrectOptionIndex();
            
            if (isCorrect) score++;

            // Highlight the correct answer and show explanation
            highlightOptions(radioGroup, q.getCorrectOptionIndex(), selectedIndex);
            
            explanationLayout.setVisibility(View.VISIBLE);
            if (isCorrect) {
                txtResultStatus.setText("Chính xác!");
                txtResultStatus.setTextColor(Color.parseColor("#299D2D")); // Green
            } else {
                txtResultStatus.setText("Sai rồi. Đáp án đúng là: " + q.getOptions().get(q.getCorrectOptionIndex()).substring(0, 1));
                txtResultStatus.setTextColor(Color.parseColor("#D32F2F")); // Red
            }
            txtExplanation.setText(q.getExplanation());
            
            // Disable radio buttons so user can't change answer after submitting
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                radioGroup.getChildAt(j).setEnabled(false);
            }
        }

        TextView txtScore = findViewById(R.id.txtScore);
        txtScore.setVisibility(View.VISIBLE);
        txtScore.setText("Điểm: " + score + "/" + totalQuestions);
        
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setEnabled(false);
        btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));

        // Save result to Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
        int currentTestId = getIntent().getIntExtra("TEST_ID", -1);
        dbHelper.insertTestResult(currentTestId, currentTest.getTitle(), score, totalQuestions, currentDate);
        
        Toast.makeText(this, "Đã lưu kết quả bài làm!", Toast.LENGTH_SHORT).show();
    }
    
    private void highlightOptions(RadioGroup radioGroup, int correctIndex, int selectedIndex) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (i == correctIndex) {
                rb.setTextColor(Color.parseColor("#299D2D")); // Green for correct
                rb.setTypeface(null, android.graphics.Typeface.BOLD);
            } else if (i == selectedIndex && !isCorrect(selectedIndex, correctIndex)) {
                rb.setTextColor(Color.parseColor("#D32F2F")); // Red for incorrect selected
            }
        }
    }
    
    private boolean isCorrect(int selected, int correct) {
        return selected == correct;
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
