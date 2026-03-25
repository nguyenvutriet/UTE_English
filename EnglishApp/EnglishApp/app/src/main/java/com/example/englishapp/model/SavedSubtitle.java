package com.example.englishapp.model;

public class SavedSubtitle {
    private String videoId;
    private String text;
    private float startTime;
    private float endTime;

    public SavedSubtitle(String videoId, String text, float startTime, float endTime) {
        this.videoId = videoId;
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getVideoId() { return videoId; }
    public String getText() { return text; }
    public float getStartTime() { return startTime; }
    public float getEndTime() { return endTime; }
}
