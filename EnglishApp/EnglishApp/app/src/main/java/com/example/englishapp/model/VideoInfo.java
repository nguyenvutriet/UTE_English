package com.example.englishapp.model;

import java.util.List;

public class VideoInfo {
    private String videoId;
    private String title;
    private String thumbnailUrl;
    private List<SubtitleTrack> availableTracks;

    public VideoInfo(String videoId, String title, String thumbnailUrl, List<SubtitleTrack> availableTracks) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.availableTracks = availableTracks;
    }

    public String getVideoId() { return videoId; }
    public String getTitle() { return title; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public List<SubtitleTrack> getAvailableTracks() { return availableTracks; }
}
