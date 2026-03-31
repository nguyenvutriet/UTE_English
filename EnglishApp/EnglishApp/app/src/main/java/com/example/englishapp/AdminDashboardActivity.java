package com.example.englishapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // 1. Vẽ biểu đồ dữ liệu tĩnh
        setupFakeChart();

//        // 2. Ánh xạ các nút bấm từ XML
//        Button btnQuiz = findViewById(R.id.btnQuiz);
//        Button btnVocab = findViewById(R.id.btnVocab);
        Button btnUser = findViewById(R.id.btnUser);

//        // 3. Xử lý sự kiện Click (Controller logic)
//        btnQuiz.setOnClickListener(v -> {
//            Intent intent = new Intent(AdminDashboardActivity.this, QuizManageActivity.class);
//            startActivity(intent);
//        });
//
//        btnVocab.setOnClickListener(v -> {
//            Intent intent = new Intent(AdminDashboardActivity.this, VocabManageActivity.class);
//            startActivity(intent);
//        });

        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AccountManageActivity.class);
            startActivity(intent);
        });
    }

    private void setupFakeChart() {
        LinearLayout chartContainer = findViewById(R.id.chartContainer);
        LinearLayout labelContainer = findViewById(R.id.labelContainer);

        // Dữ liệu cột (Số người dùng)
        List<Integer> data = Arrays.asList(40, 80, 60, 110, 150, 100, 130);
        // Đã đổi sang Ngày (Dữ liệu tĩnh 7 ngày gần nhất)
        List<String> labels = Arrays.asList("26/03", "27/03", "28/03", "29/03", "30/03", "31/03", "01/04");

        chartContainer.removeAllViews();
        labelContainer.removeAllViews();

        for (int i = 0; i < data.size(); i++) {
            // 1. Vẽ Cột (Bar)
            View bar = new View(this);
            // Chiều cao cột = giá trị * 3 (để nhìn cho rõ)
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(0, data.get(i) * 3, 1f);
            barParams.setMargins(10, 0, 10, 0);
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(Color.parseColor("#1A73E8"));
            chartContainer.addView(bar);

            // 2. Viết ngày dưới chân cột
            TextView tvLabel = new TextView(this);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvLabel.setLayoutParams(labelParams);
            tvLabel.setText(labels.get(i));
            tvLabel.setGravity(Gravity.CENTER);
            tvLabel.setTextSize(10); // Cho size nhỏ lại chút vì chuỗi ngày dài hơn chữ Thứ
            tvLabel.setTextColor(Color.GRAY);
            labelContainer.addView(tvLabel);
        }
    }
}