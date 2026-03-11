package com.example.englishapp;

public class Question {

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correct;
    private String userAnswer;

    public Question(String question, String optionA, String optionB, String optionC, String correct) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correct = correct;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getCorrect() {
        return correct;
    }

    public void setUserAnswer(String userAnswer){
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer(){
        return userAnswer;
    }
}