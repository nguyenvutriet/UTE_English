package com.example.englishapp;

public class TopicWord {

    public final String topicId;
    public final String word;
    public final String type;
    public final String uk;
    public final String us;
    public final String meaning;
    public final String example;
    public final int imageRes;

    public TopicWord(String topicId,
                     String word,
                     String type,
                     String uk,
                     String us,
                     String meaning,
                     String example,
                     int imageRes) {
        this.topicId = topicId;
        this.word = word;
        this.type = type;
        this.uk = uk;
        this.us = us;
        this.meaning = meaning;
        this.example = example;
        this.imageRes = imageRes;
    }
}

