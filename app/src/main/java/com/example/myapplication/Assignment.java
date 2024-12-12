package com.example.myapplication;

import java.util.List;

public class Assignment {
    private String name;
    private String description;
    private List<String> questions;

    public Assignment() {

    }

    /*//public Assignment(String title, String description) {
        // Default constructor required for Firebase
    }*/

    public Assignment(String name, String description, List<String> questions) {
        this.name = name;
        this.description = description;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getQuestions() {
        return questions;
    }
}
