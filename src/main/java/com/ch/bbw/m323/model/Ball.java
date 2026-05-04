package com.ch.bbw.m323.model;

public record Ball(int position) {

    public boolean isAtHome() {
        return position == 0;
    }

    public Ball move(int steps) {
        int newPosition = (position + steps) % 64; // 64 Felder Beispiel
        return new Ball(newPosition);
    }
}
