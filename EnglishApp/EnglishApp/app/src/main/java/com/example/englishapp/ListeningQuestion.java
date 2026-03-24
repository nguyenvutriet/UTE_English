package com.example.englishapp;

import java.util.List;

public class ListeningQuestion {
    private int imageResourceId;      // Ảnh hiển thị (e.g., R.drawable.u1_c1)
    private int audioResourceId;      // Âm thanh (e.g., R.raw.u1_c1)
    private String questionText;      // Câu hỏi (e.g., "Điều nào đúng?")
    private List<String> options;     // 4 tùy chọn (A, B, C, D)
    private int correctOptionIndex;   // Đáp án đúng (0-3 tương ứng A-D)
    private String vietnameseTranslation;  // Bản dịch tiếng Việt của tất cả tùy chọn
    private String englishText;       // Văn bản tiếng Anh
    private String explanation;       // Giải thích

    public ListeningQuestion(int imageResourceId, int audioResourceId, String questionText,
                            List<String> options, int correctOptionIndex,
                            String vietnameseTranslation, String englishText, String explanation) {
        this.imageResourceId = imageResourceId;
        this.audioResourceId = audioResourceId;
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.vietnameseTranslation = vietnameseTranslation;
        this.englishText = englishText;
        this.explanation = explanation;
    }

    // Getters
    public int getImageResourceId() { return imageResourceId; }
    public int getAudioResourceId() { return audioResourceId; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public String getVietnameseTranslation() { return vietnameseTranslation; }
    public String getEnglishText() { return englishText; }
    public String getExplanation() { return explanation; }

    public String getCorrectOption() {
        if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return options.get(correctOptionIndex);
        }
        return "";
    }
}

