package com.ch.bbw.m323.model;

import io.vavr.collection.List;

public record  GameState(
        int currentPlayer,
        List<Player> players,
        RoundMode roundMode,
        int cardsPerRound
) {

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public GameState nextPlayer() {
        int next = (currentPlayer + 1) % players.size();
        return new GameState(next, players, roundMode, cardsPerRound);
    }

    public GameState updatePlayer(Player updatedPlayer) {
        return new GameState(
                currentPlayer,
                players.update(currentPlayer, updatedPlayer),
                roundMode,
                cardsPerRound
        );
    }

    public GameState updatePlayerAt(int index, Player updatedPlayer) {
        return new GameState(
                currentPlayer,
                players.update(index, updatedPlayer),
                roundMode,
                cardsPerRound
        );
    }
}