package com.example.englishapp;

public class Topic {

    String id;
    String name;
    int image;
    int total;
    int learned;

    public Topic(String id, String name, int image, int total, int learned){
        this.id = id;
        this.name = name;
        this.image = image;
        this.total = total;
        this.learned = learned;
    }

    public Topic(String name,int image,int total,int learned){
        this(name.toLowerCase(), name, image, total, learned);
    }
}