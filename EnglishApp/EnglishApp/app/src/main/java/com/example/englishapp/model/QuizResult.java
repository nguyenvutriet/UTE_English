package com.example.englishapp.model;

public class QuizResult {
    public String id;          // Dùng timestamp làm ID duy nhất
    public String title;       // Tên bài tập (mặc định là ngày giờ)
    public String originalText; // Thêm trường này để lưu đoạn văn OCR
    public int score;          // Số câu đúng
    public int total;          // Tổng số câu
    public String dateTime;    // Chuỗi ngày giờ hiển thị
    public String detailJson;  // Lưu danh sách câu hỏi + đáp án đã chọn (JSON)

    public QuizResult() {
        this.id = String.valueOf(System.currentTimeMillis());
    }
}