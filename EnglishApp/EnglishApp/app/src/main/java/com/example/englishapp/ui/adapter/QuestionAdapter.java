package com.example.englishapp.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> list;
    private boolean isReviewMode = false; // Biến kiểm soát chế độ hiển thị

    // Constructor cập nhật để nhận thêm trạng thái Review
    public QuestionAdapter(List<Question> list, boolean isReviewMode) {
        this.list = list;
        this.isReviewMode = isReviewMode;
    }

    // Hàm để cập nhật chế độ xem lại từ Activity
    public void setReviewMode(boolean reviewMode) {
        isReviewMode = reviewMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, A, B, C, D;

        public ViewHolder(View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            A = itemView.findViewById(R.id.optionA);
            B = itemView.findViewById(R.id.optionB);
            C = itemView.findViewById(R.id.optionC);
            D = itemView.findViewById(R.id.optionD);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_ai, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        Question q = list.get(pos);

        // Hiển thị nội dung
        h.tvQuestion.setText("Câu " + (pos + 1) + ": " + q.question);
        h.A.setText("A. " + q.A);
        h.B.setText("B. " + q.B);
        h.C.setText("C. " + q.C);
        h.D.setText("D. " + q.D);

        // 1. Reset màu nền về mặc định trước khi vẽ
        reset(h);

        if (isReviewMode) {
            // --- CHẾ ĐỘ XEM LẠI ---
            // Khóa không cho người dùng tương tác nữa
            h.A.setOnClickListener(null);
            h.B.setOnClickListener(null);
            h.C.setOnClickListener(null);
            h.D.setOnClickListener(null);

            // Tô màu XANH LÁ cho đáp án ĐÚNG
            highlightOption(h, q.correct, "#C8E6C9");

            // Nếu người dùng chọn SAI, tô màu ĐỎ cho đáp án đã chọn
            if (q.selected != null && !q.selected.equals(q.correct)) {
                highlightOption(h, q.selected, "#FFCDD2");
            }

        } else {
            // --- CHẾ ĐỘ ĐANG LÀM BÀI ---
            // Nếu đã chọn rồi thì hiện màu xanh dương nhạt để đánh dấu
            if (q.selected != null) {
                highlightOption(h, q.selected, "#BBDEFB");
            }

            // Gán sự kiện click
            h.A.setOnClickListener(v -> select(q, "A", pos));
            h.B.setOnClickListener(v -> select(q, "B", pos));
            h.C.setOnClickListener(v -> select(q, "C", pos));
            h.D.setOnClickListener(v -> select(q, "D", pos));
        }
    }

    // Hàm tô màu dựa trên ký tự A, B, C, D
    private void highlightOption(ViewHolder h, String option, String colorHex) {
        int color = Color.parseColor(colorHex);
        if (option == null) return;

        switch (option.trim().toUpperCase()) {
            case "A": h.A.setBackgroundColor(color); break;
            case "B": h.B.setBackgroundColor(color); break;
            case "C": h.C.setBackgroundColor(color); break;
            case "D": h.D.setBackgroundColor(color); break;
        }
    }

    private void select(Question q, String ans, int position) {
        q.selected = ans;
        // Chỉ cập nhật lại item này để tối ưu hiệu năng
        notifyItemChanged(position);
    }

    private void reset(ViewHolder h) {
        int defaultColor = Color.parseColor("#EEEEEE");
        h.A.setBackgroundColor(defaultColor);
        h.B.setBackgroundColor(defaultColor);
        h.C.setBackgroundColor(defaultColor);
        h.D.setBackgroundColor(defaultColor);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}