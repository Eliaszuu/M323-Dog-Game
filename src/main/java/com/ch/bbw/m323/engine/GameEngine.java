package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.model.*;

public class GameEngine {

    public static GameState playCard(GameState state, int ballIndex, Card card) {

        Player player = state.getCurrentPlayer();
        Ball ball = player.balls().get(ballIndex);

        Ball newBall;

        // AUS HAUS LOGIK
        if (ball.isAtHome()) {

            if (card.rank() == Rank.ACE || card.rank() == Rank.KING) {
                newBall = Ball.enterBoard();
            } else {
                System.out.println("Du brauchst ASS oder KÖNIG um rauszukommen!");
                return state;
            }

        } else {

            newBall = ball.move(card.getMoveValue());
        }

        Player updatedPlayer =
                player.updateBall(ballIndex, newBall)
                        .removeCard(card);

        GameState updatedState = state.updatePlayer(updatedPlayer);

        return updatedState.nextPlayer();
    }

    public static boolean hasWon(Player player) {
        return player.balls()
                .forAll(Ball::isInGoal);
    }
}