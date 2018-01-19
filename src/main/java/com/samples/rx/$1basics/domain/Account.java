package com.samples.rx.$1basics.domain;

public class Account {

    private String name;
    private float availableAmount;
    private int score;

    public Account() {
    }

    public Account(String name, float availableAmount, int score) {
        this.name = name;
        this.availableAmount = availableAmount;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public float getAvailableAmount() {
        return availableAmount;
    }

    public int getScore() {
        return score;
    }

}
