package com.example.englishapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Question;
import com.example.englishapp.model.QuizResult;
import com.example.englishapp.ui.adapter.QuestionAdapter;
import com.example.englishapp.utils.HistoryManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnSubmit;
    private TextView tvOriginalText;

    private List<Question> questionList = new ArrayList<>();
    private QuestionAdapter adapter;
    private boolean isReview = false;
    private String originalDocText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_ai);

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerView);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvOriginalText = findViewById(R.id.tvOriginalText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. Lấy dữ liệu từ Intent
        isReview = getIntent().getBooleanExtra("isReview", false);
        String jsonData = getIntent().getStringExtra("data");
        originalDocText = getIntent().getStringExtra("original_text");

        // 2. Xử lý hiển thị đoạn văn gốc
        if (originalDocText != null && !originalDocText.isEmpty()) {
            tvOriginalText.setText(originalDocText);
        } else {
            tvOriginalText.setText("Không có nội dung đính kèm.");
        }

        // 3. Xử lý danh sách câu hỏi
        if (isReview) {
            // CHẾ ĐỘ XEM LẠI: Parse từ detailJson (List Question đã có đáp án chọn)
            try {
                questionList = new Gson().fromJson(jsonData, new TypeToken<List<Question>>(){}.getType());
                btnSubmit.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi tải dữ liệu lịch sử!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // CHẾ ĐỘ LÀM MỚI: Parse từ JSON thô của AI
            questionList = parseJsonFromGemini(jsonData);
        }

        // 4. Thiết lập Adapter
        adapter = new QuestionAdapter(questionList, isReview);
        recyclerView.setAdapter(adapter);

        // 5. Sự kiện Nộp bài
        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void submitQuiz() {
        int score = 0;
        // Tính điểm
        for (Question q : questionList) {
            if (q.selected != null && q.selected.trim().equalsIgnoreCase(q.correct.trim())) {
                score++;
            }
        }

        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        // Tạo đối tượng lưu trữ
        QuizResult result = new QuizResult();
        result.title = "Bài tập " + currentTime;
        result.score = score;
        result.total = questionList.size();
        result.dateTime = currentTime;
        result.originalText = originalDocText; // Lưu lại đoạn văn gốc
        result.detailJson = new Gson().toJson(questionList); // Lưu lại list câu hỏi kèm đáp án đã chọn

        // Lưu vào SharedPreferences
        HistoryManager.saveResult(this, result);

        // Chuyển sang trạng thái xem lại tại chỗ
        isReview = true;
        btnSubmit.setVisibility(View.GONE);
        adapter.setReviewMode(true);

        // Hiển thị Dialog thông báo
        new AlertDialog.Builder(this)
                .setTitle("Kết quả bài làm")
                .setMessage("Bạn đúng: " + score + "/" + questionList.size())
                .setCancelable(false)
                .setPositiveButton("Xem lại tại đây", null)
                .setNegativeButton("Xem Lịch sử", (d, w) -> {
                    startActivity(new Intent(this, HistoryActivity.class));
                    finish();
                })
                .show();
    }

    private List<Question> parseJsonFromGemini(String rawJson) {
        List<Question> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(rawJson);

            // Xử lý chuỗi JSON từ cấu trúc API Gemini
            String content = root.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // Làm sạch Markdown nếu có
            content = content.replace("```json", "").replace("```", "").trim();

            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Question q = new Question();
                q.question = obj.getString("question");
                q.A = obj.getString("A");
                q.B = obj.getString("B");
                q.C = obj.getString("C");
                q.D = obj.getString("D");
                q.correct = obj.getString("correct");
                q.selected = ""; // Khởi tạo chưa chọn
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Lỗi xử lý đề bài từ AI!", Toast.LENGTH_SHORT).show());
        }
        return list;
    }
}