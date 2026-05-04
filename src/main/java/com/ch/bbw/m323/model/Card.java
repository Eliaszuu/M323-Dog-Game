package com.ch.bbw.m323.model;

public record Card(Rank rank, Suit suit) {

    public int getMoveValue() {
        return switch (rank) {
            case ACE -> 11;
            case JACK -> 11;   // später: Sonderlogik
            case QUEEN -> 12;
            case KING -> 13;
            default -> rank.ordinal() + 1;
        };
    }
}
