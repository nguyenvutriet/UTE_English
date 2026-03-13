package com.example.englishapp;

import java.util.List;

public class ReadingTest {
    private String title;
    private String passage;
    private List<Question> questions;

    public ReadingTest(String title, String passage, List<Question> questions) {
        this.title = title;
        this.passage = passage;
        this.questions = questions;
    }

    public String getTitle() { return title; }
    public String getPassage() { return passage; }
    public List<Question> getQuestions() { return questions; }

    public static class Question {
        private String questionText;
        private List<String> options;
        private int correctOptionIndex; // 0 for A, 1 for B, 2 for C, 3 for D
        private String explanation;

        public Question(String questionText, List<String> options, int correctOptionIndex, String explanation) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.explanation = explanation;
        }

        public String getQuestionText() { return questionText; }
        public List<String> getOptions() { return options; }
        public int getCorrectOptionIndex() { return correctOptionIndex; }
        public String getExplanation() { return explanation; }
    }
}
