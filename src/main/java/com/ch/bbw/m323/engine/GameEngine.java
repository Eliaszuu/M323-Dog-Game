package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.model.*;

public class GameEngine {

    public static GameState playCard(GameState state, int ballIndex, Card card) {

        Player player = state.getCurrentPlayer();

        if (!player.hand().contains(card)) {
            throw new IllegalArgumentException("Card not in hand");
        }

        Ball ball = player.balls().get(ballIndex);

        Ball movedBall = ball.move(card.getMoveValue());

        Player updatedPlayer =
                player.updateBall(ballIndex, movedBall)
                        .removeCard(card);

        GameState updatedState = state.updatePlayer(updatedPlayer);

        return updatedState.nextPlayer();
    }
}