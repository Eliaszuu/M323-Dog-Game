package com.ch.bbw.m323.model;

public record Ball(int position) {

    public static final int HOME = 0;
    public static final int BOARD_END = 63;
    public static final int GOAL_START = 64;
    public static final int GOAL_END = 67;

    public boolean isAtHome() {
        return position == HOME;
    }

    public boolean isInGoal() {
        return position >= GOAL_START && position <= GOAL_END;
    }

    public Ball move(int steps) {
        if (isAtHome()) throw new IllegalStateException("Ball is at home");
        int newPos = position + steps;
        // Rückwärts: darf nicht unter Position 1 fallen
        if (newPos < 1) return this;
        // Vorwärts: darf nicht über das Zielende hinausschiessen
        if (newPos > GOAL_END) return this;
        return new Ball(newPos);
    }

    public boolean wouldMove(int steps) {
        if (isAtHome()) return false;
        int newPos = position + steps;
        return newPos >= 1 && newPos <= GOAL_END;
    }

    public static Ball enterBoard() {
        return new Ball(1); // Startfeld
    }
}