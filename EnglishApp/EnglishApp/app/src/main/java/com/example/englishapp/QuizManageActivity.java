package com.example.englishapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizManageActivity extends AppCompatActivity {

    private final ArrayList<String> quizTitles = new ArrayList<>();
    private final Map<String, String> quizHeaders = new LinkedHashMap<>();
    private final Map<String, ArrayList<String>> quizQuestions = new LinkedHashMap<>();

    private final ArrayList<String> quizDisplayItems = new ArrayList<>();
    private final ArrayList<String> questionDisplayItems = new ArrayList<>();

    private ArrayAdapter<String> quizAdapter;
    private ArrayAdapter<String> questionAdapter;
    private ArrayAdapter<String> questionQuizAdapter;

    private String selectedQuizTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_manage);

        EditText etQuizTitle = findViewById(R.id.etQuizTitle);
        EditText etQuestionCount = findViewById(R.id.etQuestionCount);
        Spinner spQuizLevel = findViewById(R.id.spQuizLevel);

        EditText etQuestion = findViewById(R.id.etQuestion);
        EditText etOptionA = findViewById(R.id.etOptionA);
        EditText etOptionB = findViewById(R.id.etOptionB);
        EditText etOptionC = findViewById(R.id.etOptionC);
        Spinner spCorrectAnswer = findViewById(R.id.spCorrectAnswer);
        Spinner spQuestionQuiz = findViewById(R.id.spQuestionQuiz);

        ListView lvQuiz = findViewById(R.id.lvQuiz);
        ListView lvQuizQuestions = findViewById(R.id.lvQuizQuestions);
        TextView tvSelectedQuiz = findViewById(R.id.tvSelectedQuiz);

        MaterialButton btnSaveQuiz = findViewById(R.id.btnSaveQuiz);
        MaterialButton btnSaveQuestion = findViewById(R.id.btnSaveQuestion);
        MaterialButton btnBack = findViewById(R.id.btnBackQuizDashboard);

        setupLevelSpinner(spQuizLevel);
        setupCorrectAnswerSpinner(spCorrectAnswer);
        setupQuestionQuizSpinner(spQuestionQuiz);
        setupListAdapters(lvQuiz, lvQuizQuestions);
        enablePanelScroll(lvQuiz);
        enablePanelScroll(lvQuizQuestions);

        setupFakeData();
        refreshQuizList();

        lvQuiz.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= quizTitles.size()) {
                return;
            }
            selectedQuizTitle = quizTitles.get(position);
            tvSelectedQuiz.setText("Câu hỏi của: " + selectedQuizTitle);
            refreshQuestionListForSelectedQuiz();

            int spinnerIndex = quizTitles.indexOf(selectedQuizTitle);
            if (spinnerIndex >= 0) {
                spQuestionQuiz.setSelection(spinnerIndex);
            }
        });

        btnSaveQuiz.setOnClickListener(v -> {
            String title = etQuizTitle.getText().toString().trim();
            String questionCount = etQuestionCount.getText().toString().trim();
            String level = String.valueOf(spQuizLevel.getSelectedItem());

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui long nhap ten quiz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quizHeaders.containsKey(title)) {
                Toast.makeText(this, "Quiz da ton tai", Toast.LENGTH_SHORT).show();
                return;
            }

            if (questionCount.isEmpty()) {
                questionCount = "10";
            }

            addQuiz(title, level, questionCount);
            refreshQuizList();

            selectedQuizTitle = title;
            tvSelectedQuiz.setText("Câu hỏi của: " + selectedQuizTitle);
            refreshQuestionListForSelectedQuiz();
            spQuestionQuiz.setSelection(quizTitles.indexOf(selectedQuizTitle));

            etQuizTitle.setText("");
            etQuestionCount.setText("");
            Toast.makeText(this, "Da luu quiz", Toast.LENGTH_SHORT).show();
        });

        btnSaveQuestion.setOnClickListener(v -> {
            String selectedQuiz = String.valueOf(spQuestionQuiz.getSelectedItem());
            String question = etQuestion.getText().toString().trim();
            String optionA = etOptionA.getText().toString().trim();
            String optionB = etOptionB.getText().toString().trim();
            String optionC = etOptionC.getText().toString().trim();
            String correct = String.valueOf(spCorrectAnswer.getSelectedItem());

            if (quizTitles.isEmpty()) {
                Toast.makeText(this, "Vui long tao quiz truoc", Toast.LENGTH_SHORT).show();
                return;
            }

            if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty()) {
                Toast.makeText(this, "Vui long nhap day du cau hoi va dap an", Toast.LENGTH_SHORT).show();
                return;
            }

            String fakeQuestion = "Q: " + question
                    + "\nA) " + optionA
                    + " | B) " + optionB
                    + " | C) " + optionC
                    + " | Dung: " + correct;

            ArrayList<String> questions = quizQuestions.get(selectedQuiz);
            if (questions == null) {
                questions = new ArrayList<>();
                quizQuestions.put(selectedQuiz, questions);
            }
            questions.add(0, fakeQuestion);

            selectedQuizTitle = selectedQuiz;
            tvSelectedQuiz.setText("Câu hỏi của: " + selectedQuizTitle);
            refreshQuestionListForSelectedQuiz();

            etQuestion.setText("");
            etOptionA.setText("");
            etOptionB.setText("");
            etOptionC.setText("");
            spCorrectAnswer.setSelection(0);
            Toast.makeText(this, "Da tao cau hoi cho quiz: " + selectedQuiz, Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupLevelSpinner(Spinner spinner) {
        List<String> levels = Arrays.asList("Beginner", "Intermediate", "Advanced");
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                levels
        );
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(levelAdapter);
    }

    private void setupCorrectAnswerSpinner(Spinner spinner) {
        List<String> correctAnswers = Arrays.asList("A", "B", "C");
        ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                correctAnswers
        );
        answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(answerAdapter);
    }

    private void setupQuestionQuizSpinner(Spinner spinner) {
        questionQuizAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                quizTitles
        );
        questionQuizAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(questionQuizAdapter);
    }

    private void setupListAdapters(ListView lvQuiz, ListView lvQuizQuestions) {
        quizAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                quizDisplayItems
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
        lvQuiz.setAdapter(quizAdapter);

        questionAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                questionDisplayItems
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
        lvQuizQuestions.setAdapter(questionAdapter);
    }

    private void setupFakeData() {

        // ===== QUIZ LIST =====
        addQuiz("Daily Grammar 01", "Beginner", "5");
        addQuiz("Daily Grammar 02", "Beginner", "5");
        addQuiz("Vocabulary Week 2", "Intermediate", "5");
        addQuiz("TOEIC Mini Test 01", "Intermediate", "5");
        addQuiz("Reading Practice 01", "Intermediate", "5");

        // ===== DAILY GRAMMAR 01 =====
        quizQuestions.get("Daily Grammar 01").add("Q: She ___ to school every day.\nA) go | B) goes | C) going | Dung: B");
        quizQuestions.get("Daily Grammar 01").add("Q: They ___ playing football now.\nA) is | B) are | C) am | Dung: B");
        quizQuestions.get("Daily Grammar 01").add("Q: I ___ a student.\nA) is | B) am | C) are | Dung: B");
        quizQuestions.get("Daily Grammar 01").add("Q: He ___ coffee every morning.\nA) drink | B) drinks | C) drinking | Dung: B");
        quizQuestions.get("Daily Grammar 01").add("Q: We ___ English now.\nA) learn | B) learns | C) learning | Dung: A");

        // ===== DAILY GRAMMAR 02 =====
        quizQuestions.get("Daily Grammar 02").add("Q: He ___ TV yesterday.\nA) watch | B) watched | C) watching | Dung: B");
        quizQuestions.get("Daily Grammar 02").add("Q: We ___ to the park tomorrow.\nA) go | B) went | C) will go | Dung: C");
        quizQuestions.get("Daily Grammar 02").add("Q: She ___ already finished her homework.\nA) have | B) has | C) had | Dung: B");
        quizQuestions.get("Daily Grammar 02").add("Q: They ___ dinner when I arrived.\nA) have | B) had | C) having | Dung: B");
        quizQuestions.get("Daily Grammar 02").add("Q: I ___ never seen this before.\nA) have | B) has | C) had | Dung: A");

        // ===== VOCABULARY =====
        quizQuestions.get("Vocabulary Week 2").add("Q: Choose the synonym of rapid.\nA) slow | B) quick | C) weak | Dung: B");
        quizQuestions.get("Vocabulary Week 2").add("Q: Choose the antonym of expensive.\nA) cheap | B) big | C) nice | Dung: A");
        quizQuestions.get("Vocabulary Week 2").add("Q: Choose the synonym of begin.\nA) start | B) stop | C) end | Dung: A");
        quizQuestions.get("Vocabulary Week 2").add("Q: Choose the synonym of happy.\nA) sad | B) joyful | C) angry | Dung: B");
        quizQuestions.get("Vocabulary Week 2").add("Q: Choose the antonym of strong.\nA) weak | B) big | C) tall | Dung: A");

        // ===== TOEIC MINI =====
        quizQuestions.get("TOEIC Mini Test 01").add("Q: The meeting will ___ at 9 AM.\nA) start | B) starts | C) starting | Dung: A");
        quizQuestions.get("TOEIC Mini Test 01").add("Q: Please send me the report ___ email.\nA) in | B) by | C) on | Dung: B");
        quizQuestions.get("TOEIC Mini Test 01").add("Q: She is responsible ___ managing the team.\nA) in | B) for | C) on | Dung: B");
        quizQuestions.get("TOEIC Mini Test 01").add("Q: This product is ___ than the old one.\nA) good | B) better | C) best | Dung: B");
        quizQuestions.get("TOEIC Mini Test 01").add("Q: He works ___ a big company.\nA) at | B) in | C) on | Dung: A");

        // ===== READING =====
        quizQuestions.get("Reading Practice 01").add("Q: What is the main idea of the passage?\nA) Travel | B) Food | C) Work | Dung: A");
        quizQuestions.get("Reading Practice 01").add("Q: The word 'it' refers to?\nA) Company | B) Product | C) Manager | Dung: B");
        quizQuestions.get("Reading Practice 01").add("Q: What can be inferred from the text?\nA) He is tired | B) He is happy | C) He is busy | Dung: C");
        quizQuestions.get("Reading Practice 01").add("Q: Where does the story take place?\nA) School | B) Office | C) Park | Dung: B");
        quizQuestions.get("Reading Practice 01").add("Q: What is the purpose of the text?\nA) Inform | B) Entertain | C) Describe | Dung: A");

        // ===== DEFAULT SELECT =====
        if (!quizTitles.isEmpty()) {
            selectedQuizTitle = quizTitles.get(0);
            refreshQuestionListForSelectedQuiz();
        }
    }

    private void addQuiz(String title, String level, String questionCount) {
        quizTitles.add(title);
        quizHeaders.put(title, title + " | " + level + " | " + questionCount + " cau");
        quizQuestions.put(title, new ArrayList<>());
        questionQuizAdapter.notifyDataSetChanged();
    }

    private void refreshQuizList() {
        quizDisplayItems.clear();
        for (String title : quizTitles) {
            quizDisplayItems.add(quizHeaders.get(title));
        }
        quizAdapter.notifyDataSetChanged();
    }

    private void refreshQuestionListForSelectedQuiz() {
        questionDisplayItems.clear();
        ArrayList<String> questions = quizQuestions.get(selectedQuizTitle);
        if (questions == null || questions.isEmpty()) {
            questionDisplayItems.add("Chua co cau hoi trong quiz nay");
        } else {
            questionDisplayItems.addAll(questions);
        }
        questionAdapter.notifyDataSetChanged();
    }

    private void enablePanelScroll(ListView listView) {
        listView.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                default:
                    break;
            }
            return false;
        });
    }
}
