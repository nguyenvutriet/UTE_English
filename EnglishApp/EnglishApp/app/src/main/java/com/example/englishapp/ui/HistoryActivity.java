package com.example.englishapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.englishapp.R;
import com.example.englishapp.model.QuizResult;
import com.example.englishapp.ui.adapter.HistoryAdapter;
import com.example.englishapp.utils.HistoryManager;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<QuizResult> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        historyList = HistoryManager.getHistory(this);
        adapter = new HistoryAdapter(historyList, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onRename(QuizResult result) {
                showRenameDialog(result);
            }

            @Override
            public void onItemClick(QuizResult result) {
                // Click vào item để xem lại
                Intent intent = new Intent(HistoryActivity.this, QuizActivity.class);
                intent.putExtra("data", result.detailJson); // Truyền list câu hỏi đã lưu
                intent.putExtra("original_text", result.originalText); // Đề bài gốc
                intent.putExtra("isReview", true);          // Bật chế độ xem lại
                startActivity(intent);
            }
        });
        rvHistory.setAdapter(adapter);
    }

    private void showRenameDialog(QuizResult result) {
        EditText input = new EditText(this);
        input.setText(result.title);
        new AlertDialog.Builder(this)
                .setTitle("Đổi tên bài tập")
                .setView(input)
                .setPositiveButton("Lưu", (d, w) -> {
                    HistoryManager.updateTitle(this, result.id, input.getText().toString());
                    loadData(); // Refresh danh sách
                })
                .setNegativeButton("Hủy", null).show();
    }
}