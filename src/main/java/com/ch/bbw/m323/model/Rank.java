package com.ch.bbw.m323.model;

public enum Rank {
    ACE("ACE"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("JACK"),
    QUEEN("QUEEN"),
    KING("KING");

    private final String apiValue;

    Rank(String apiValue) {
        this.apiValue = apiValue;
    }

    public static Rank fromApi(String value) {
        for (Rank r : values()) {
            if (r.apiValue.equals(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown card value: " + value);
    }
}