package com.example.englishapp.model;

public class Video {

    private String videoId;
    private String title;
    private String thumbnail;
    private float watchedDuration;

    public Video(String videoId, String title, String thumbnail) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public float getWatchedDuration() {
        return watchedDuration;
    }

    public void setWatchedDuration(float watchedDuration) {
        this.watchedDuration = watchedDuration;
    }
}