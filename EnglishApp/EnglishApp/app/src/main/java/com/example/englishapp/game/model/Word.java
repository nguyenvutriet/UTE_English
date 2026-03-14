package com.example.englishapp.game.model;

public class Word {

    String english;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    String correct;

    public Word(String english, String a, String b, String c, String d, String correct) {
        this.english = english;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correct = correct;
    }

    public String getEnglish() {
        return english;
    }

    public String getA() {
        return optionA;
    }

    public String getB() {
        return optionB;
    }

    public String getC() {
        return optionC;
    }

    public String getD() {
        return optionD;
    }

    public String getCorrect() {
        return correct;
    }
}