package com.example.englishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.DatabaseHelper;
import com.example.englishapp.PronunciationHelper;
import com.example.englishapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FillInTheBlanksActivity extends AppCompatActivity {

    private static class Question {
        String english;
        String vietnamese;
        String answer;

        Question(String english, String vietnamese, String answer) {
            this.english = english;
            this.vietnamese = vietnamese;
            this.answer = answer;
        }
    }

    private List<Question> allQuestions;
    private List<Question> currentQuestions;
    private int currentQuestionIndex = 0;
    private int correctCount = 0;

    private ImageView btnBack;
    private Button btnPlayAudio;
    private TextView txtQuestionNumber;
    private TextView txtEnglishQuestion;
    private TextView txtVietnamese;
    private EditText edtAnswer;
    private Button btnCheck;
    private Button btnNext;
    private ProgressBar progressBar;
    private TextView txtProgress;
    private ScrollView scrollView;

    private PronunciationHelper pronunciationHelper;

    private static final int TOTAL_QUESTIONS = 10;
    private static final int LISTENING_FILL_BLANK_TEST_ID = 2101;
    private boolean resultSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fill_in_the_blanks);

        applyWindowInsets();
        initializeViews();
        initializeQuestions();
        loadQuestion();
        pronunciationHelper = new PronunciationHelper(this);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        txtQuestionNumber = findViewById(R.id.txtQuestionNumber);
        txtEnglishQuestion = findViewById(R.id.txtEnglishQuestion);
        txtVietnamese = findViewById(R.id.txtVietnamese);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnCheck = findViewById(R.id.btnCheck);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        scrollView = findViewById(R.id.scrollView);

        btnBack.setOnClickListener(v -> finish());
        btnPlayAudio.setOnClickListener(v -> playAudio());
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());

        edtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnCheck.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCheck.setEnabled(false);
        btnNext.setVisibility(View.GONE);
    }

    private void initializeQuestions() {
        allQuestions = new ArrayList<>();
        // Các câu hỏi từ bộ 30
        allQuestions.add(new Question("Could you please ______ the door?", "Bạn có thể vui lòng đóng cửa lại được không?", "close"));
        allQuestions.add(new Question("I usually go to ______ at 10 PM.", "Tôi thường đi ngủ vào lúc 10 giờ tối.", "bed"));
        allQuestions.add(new Question("What time does the ______ start?", "Mấy giờ thì buổi triển lãm bắt đầu?", "exhibition"));
        allQuestions.add(new Question("She is ______ for a new job.", "Cô ấy đang tìm kiếm một công việc mới.", "looking"));
        allQuestions.add(new Question("Please ______ your name at the bottom.", "Vui lòng ký tên của bạn ở phía dưới.", "sign"));
        allQuestions.add(new Question("I need to ______ some money from the ATM.", "Tôi cần rút một ít tiền từ máy ATM.", "withdraw"));
        allQuestions.add(new Question("The weather is very ______ today.", "Thời tiết hôm nay rất đẹp/dễ chịu.", "pleasant"));
        allQuestions.add(new Question("Have you ______ your breakfast yet?", "Bạn đã ăn sáng chưa?", "eaten"));
        allQuestions.add(new Question("Turn ______ at the next corner.", "Hãy rẽ trái ở góc đường tiếp theo.", "left"));
        allQuestions.add(new Question("I'll ______ you back in five minutes.", "Tôi sẽ gọi lại cho bạn trong 5 phút nữa.", "call"));
        allQuestions.add(new Question("The meeting has been ______ until Friday.", "Cuộc họp đã được hoãn cho đến thứ Sáu.", "postponed"));
        allQuestions.add(new Question("Can I ______ your pen for a moment?", "Tôi có thể mượn bút của bạn một lát không?", "borrow"));
        allQuestions.add(new Question("He is the most ______ person I know.", "Anh ấy là người thông minh nhất mà tôi biết.", "intelligent"));
        allQuestions.add(new Question("We should ______ more water every day.", "Chúng ta nên uống nhiều nước hơn mỗi ngày.", "drink"));
        allQuestions.add(new Question("I am ______ for being late.", "Tôi xin lỗi vì đã đến muộn.", "sorry"));
        allQuestions.add(new Question("The supermarket is ______ the street.", "Siêu thị ở phía bên kia đường.", "across"));
        allQuestions.add(new Question("Don't forget to ______ off the lights.", "Đừng quên tắt đèn nhé.", "turn"));
        allQuestions.add(new Question("My sister is ______ in a hospital.", "Chị gái tôi là y tá trong một bệnh viện.", "nurse"));
        allQuestions.add(new Question("How ______ does this laptop cost?", "Chiếc máy tính xách tay này giá bao nhiêu?", "much"));
        allQuestions.add(new Question("I'm looking ______ to seeing you again.", "Tôi rất mong được gặp lại bạn.", "forward"));
        allQuestions.add(new Question("There is a ______ of bread on the table.", "Có một ổ bánh mì ở trên bàn.", "loaf"));
        allQuestions.add(new Question("Please ______ your seatbelt.", "Vui lòng thắt dây an toàn của bạn.", "fasten"));
        allQuestions.add(new Question("The train ______ at 8:30 AM.", "Chuyến tàu khởi hành lúc 8 giờ 30 sáng.", "departs"));
        allQuestions.add(new Question("I forgot to ______ the document.", "Tôi đã quên đính kèm tài liệu.", "attach"));
        allQuestions.add(new Question("Can you ______ me the salt, please?", "Bạn có thể đưa cho tôi lọ muối được không?", "pass"));
        allQuestions.add(new Question("It takes 20 minutes to ______ to work.", "Mất 20 phút để lái xe đi làm.", "drive"));
        allQuestions.add(new Question("She enjoys ______ books in her free time.", "Cô ấy thích đọc sách vào thời gian rảnh.", "reading"));
        allQuestions.add(new Question("The coffee is too ______ to drink.", "Cà phê nóng quá không uống được.", "hot"));
        allQuestions.add(new Question("We need to ______ a decision soon.", "Chúng ta cần đưa ra quyết định sớm.", "make"));
        allQuestions.add(new Question("Happy ______ to you!", "Chúc mừng sinh nhật bạn!", "birthday"));

        // Random 10 câu từ 30
        shuffleAndSelectQuestions();
    }

    private void shuffleAndSelectQuestions() {
        Collections.shuffle(allQuestions);
        currentQuestions = new ArrayList<>(allQuestions.subList(0, 10));
        currentQuestionIndex = 0;
        correctCount = 0;
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= currentQuestions.size()) {
            showResults();
            return;
        }

        Question question = currentQuestions.get(currentQuestionIndex);
        txtQuestionNumber.setText("Câu " + (currentQuestionIndex + 1) + "/" + TOTAL_QUESTIONS);
        txtEnglishQuestion.setText(question.english);
        txtVietnamese.setText("(" + question.vietnamese + ")");
        txtVietnamese.setVisibility(View.GONE);
        edtAnswer.setText("");
        edtAnswer.setEnabled(true);
        btnCheck.setEnabled(false);
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);

        progressBar.setProgress(currentQuestionIndex);
        txtProgress.setText((currentQuestionIndex + 1) + "/" + TOTAL_QUESTIONS);

        scrollView.scrollTo(0, 0);
    }

    private void playAudio() {
        if (pronunciationHelper == null || currentQuestionIndex >= currentQuestions.size()) {
            return;
        }

        Question question = currentQuestions.get(currentQuestionIndex);
        // Đọc đầy đủ câu có từ đúng để người học nghe và điền.
        String spokenSentence = question.english.replace("______", question.answer);
        pronunciationHelper.speakUs(spokenSentence);
    }

    private void checkAnswer() {
        String userAnswer = edtAnswer.getText().toString().trim().toLowerCase();
        Question question = currentQuestions.get(currentQuestionIndex);
        String correctAnswer = question.answer.toLowerCase();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            correctCount++;
            Toast.makeText(this, "✅ Chính xác!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "❌ Sai! Đáp án đúng: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        txtVietnamese.setVisibility(View.VISIBLE);

        // Single-attempt behavior: lock current question and go next.
        edtAnswer.setEnabled(false);
        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    private void showResults() {
        int total = TOTAL_QUESTIONS;
        int percentage = (correctCount * 100) / total;

        saveResultToDatabase(correctCount, total);

        Intent intent = new Intent(this, FillInTheBlanksResultActivity.class);
        intent.putExtra("score", correctCount);
        intent.putExtra("total", total);
        intent.putExtra("percentage", percentage);
        startActivity(intent);
        finish();
    }

    private void saveResultToDatabase(int score, int totalQuestions) {
        if (resultSaved) {
            return;
        }

        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long rowId = dbHelper.insertTestResult(
                LISTENING_FILL_BLANK_TEST_ID,
                "Nghe dien tu",
                score,
                totalQuestions,
                currentDate
        );

        if (rowId > 0) {
            resultSaved = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pronunciationHelper != null) {
            pronunciationHelper.release();
        }
    }
}
