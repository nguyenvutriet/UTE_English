package com.example.englishapp.model;

public class TopicVideo {
    private String name;
    private String channelId;
    private String imageUrl;
    private int watchedCount;
    private int totalVideos;

    public TopicVideo(String name, String channelId, String imageUrl, int totalVideos) {
        this.name = name;
        this.channelId = channelId;
        this.imageUrl = imageUrl;
        this.totalVideos = totalVideos;
        this.watchedCount = 0;
    }

    public String getName() { return name; }
    public String getChannelId() { return channelId; }
    public String getImageUrl() { return imageUrl; }
    public int getWatchedCount() { return watchedCount; }
    public int getTotalVideos() { return totalVideos; }
    
    public void setWatchedCount(int watchedCount) { this.watchedCount = watchedCount; }
    public void setTotalVideos(int totalVideos) { this.totalVideos = totalVideos; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
