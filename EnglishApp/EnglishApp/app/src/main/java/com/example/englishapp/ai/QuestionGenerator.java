package com.example.englishapp.ai;

public class QuestionGenerator {
    public static String buildPrompt(String text) {
        return "Dựa vào nội dung sau, hãy tạo 15 câu hỏi trắc nghiệm tiếng Anh. " +
                "Chỉ trả về duy nhất một mảng JSON, không giải thích gì thêm. " +
                "Format: [{\"question\":\"\", \"A\":\"\", \"B\":\"\", \"C\":\"\", \"D\":\"\", \"correct\":\"A\"}]\n\n" +
                "Nội dung:\n" +
                text;
    }
}