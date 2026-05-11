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

    /**
     * Gibt true zurück, wenn sich der Ball bei diesem Schrittwert tatsächlich bewegen würde
     * (d.h. nicht über das Zielende hinausschießt und nicht im Haus steht).
     */
    public boolean wouldMove(int steps) {
        if (isAtHome()) return false;
        return (position + steps) <= GOAL_END;
    }

    public static Ball enterBoard() {
        return new Ball(1); // Startfeld
    }
}