package com.ch.bbw.m323.model;

import io.vavr.collection.List;

public record GameHistory(List<GameState> past) {

    public static GameHistory empty() {
        return new GameHistory(List.empty());
    }

    public GameHistory push(GameState state) {
        return new GameHistory(past.prepend(state));
    }

    public boolean canUndo() {
        return !past.isEmpty();
    }

    public GameState previous() {
        return past.head();
    }

    public GameHistory pop() {
        return new GameHistory(past.tail());
    }
}