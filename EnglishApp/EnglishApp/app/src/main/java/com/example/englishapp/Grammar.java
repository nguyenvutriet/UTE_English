package com.example.englishapp;

public class Grammar {

    private String title;
    private String formula;
    private String example;
    private String explanation;

    public Grammar(String title, String formula, String example, String explanation) {
        this.title = title;
        this.formula = formula;
        this.example = example;
        this.explanation = explanation;
    }

    public String getTitle() {
        return title;
    }

    public String getFormula() {
        return formula;
    }

    public String getExample() {
        return example;
    }

    public String getExplanation() {
        return explanation;
    }
}