package com.example.englishapp;

import java.util.ArrayList;

public class QuizData {
    public static ArrayList<Question> resultList;

    public static ArrayList<Question> getQuestions(){

        ArrayList<Question> list = new ArrayList<>();

        list.add(new Question(
                "She ____ to school every day.",
                "go",
                "goes",
                "going",
                "goes"
        ));

        list.add(new Question(
                "They ____ football now.",
                "play",
                "are playing",
                "played",
                "are playing"
        ));

        list.add(new Question(
                "I ____ a movie yesterday.",
                "watch",
                "watched",
                "watching",
                "watched"
        ));

        return list;
    }
}
