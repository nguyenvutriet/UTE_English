package com.example.englishapp;

import java.util.ArrayList;

public class TopicGrammarData {

    public static ArrayList<TopicGrammar> getTopics(){

        ArrayList<TopicGrammar> list = new ArrayList<>();

        list.add(new TopicGrammar("Tenses"));
        list.add(new TopicGrammar("Sentence Structure"));
        list.add(new TopicGrammar("Passive Voice"));
        list.add(new TopicGrammar("Conditionals"));

        return list;
    }
}
