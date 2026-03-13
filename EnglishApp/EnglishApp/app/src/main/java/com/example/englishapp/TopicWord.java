package com.example.englishapp;

public class TopicWord {

    final String topicId;
    final String word;
    final String type;
    final String uk;
    final String us;
    final String meaning;
    final String example;
    final int imageRes;

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

