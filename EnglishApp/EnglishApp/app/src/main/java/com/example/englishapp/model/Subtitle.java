package com.example.englishapp.model;

public class Subtitle {

    private String text;
    private float startTime;
    private float endTime;

    public Subtitle(String text, float startTime, float endTime) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getText() {
        return text;
    }

    public float getStartTime() {
        return startTime;
    }

    public float getEndTime() {
        return endTime;
    }
}