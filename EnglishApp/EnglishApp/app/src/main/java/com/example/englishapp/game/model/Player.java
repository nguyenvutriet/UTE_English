package com.example.englishapp.game.model;

public class Player {

    String name;
    int score;
    int avatar;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        this.avatar = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getAvatar() {
        return avatar;
    }
}