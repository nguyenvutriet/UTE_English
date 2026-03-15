package com.example.englishapp.model;

import java.util.List;

public class Channel {
    private String id;
    private String name;
    private String iconUrl;
    private List<Video> videos;

    public Channel(String id, String name, String iconUrl, List<Video> videos) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.videos = videos;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public List<Video> getVideos() {
        return videos;
    }
}