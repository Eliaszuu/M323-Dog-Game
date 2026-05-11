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

        if (isAtHome()) {
            throw new IllegalStateException("Ball is at home");
        }

        int newPos = position + steps;

        if (newPos > GOAL_END) {
            return this; // nicht bewegen wenn über Ziel
        }

        return new Ball(newPos);
    }

    public static Ball enterBoard() {
        return new Ball(1); // Startfeld
    }
}