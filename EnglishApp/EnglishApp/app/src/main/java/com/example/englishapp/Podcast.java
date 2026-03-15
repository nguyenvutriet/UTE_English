package com.example.englishapp;

public class Podcast {

    private String title;
    private String subtitle;
    private int image;
    private int audio;

    public Podcast(String title, String subtitle, int image, int audio) {
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.audio = audio;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImage() {
        return image;
    }

    public int getAudio() {
        return audio;
    }
}