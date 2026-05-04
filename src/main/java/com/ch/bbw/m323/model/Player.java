package com.ch.bbw.m323.model;

import io.vavr.collection.List;

public record Player(
        int id,
        List<Ball> balls,
        List<Card> hand
) {

    public Player withHand(List<Card> newHand) {
        return new Player(id, balls, newHand);
    }

    public Player updateBall(int index, Ball newBall) {
        return new Player(id, balls.update(index, newBall), hand);
    }

    public Player removeCard(Card card) {
        return new Player(id, balls, hand.remove(card));
    }
}
