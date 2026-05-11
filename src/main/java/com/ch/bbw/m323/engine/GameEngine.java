package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.model.*;

public class GameEngine {

    /**
     * Spielt eine Karte aus. Bei einem Joker auf dem Feld wird jokerSteps (1–13)
     * als Schrittweite verwendet. Beim Herausstellen aus dem Haus wird jokerSteps ignoriert.
     */
    public static GameState playCard(GameState state, int ballIndex, Card card, int jokerSteps) {

        Player player = state.getCurrentPlayer();
        Ball ball = player.balls().get(ballIndex);

        Ball newBall;

        // AUS HAUS LOGIK
        if (ball.isAtHome()) {

            if (card.rank() == Rank.ACE
                    || card.rank() == Rank.KING
                    || card.rank() == Rank.JOKER) {

                newBall = Ball.enterBoard();
            } else {
                System.out.println("Du brauchst ASS, KÖNIG oder JOKER!");
                return state;
            }

        } else {

            // Joker: Spieler hat die Schrittanzahl selbst gewählt
            int steps = (card.rank() == Rank.JOKER) ? jokerSteps : card.getMoveValue();
            newBall = ball.move(steps);

            if (newBall.position() == ball.position()) {
                System.out.println("Dieser Zug würde über das Ziel hinausschießen – ungültig!");
                return state;
            }
        }

        Player updatedPlayer =
                player.updateBall(ballIndex, newBall)
                        .removeCard(card);

        GameState updatedState = state.updatePlayer(updatedPlayer);

        return updatedState.nextPlayer();
    }

    /**
     * Prüft, ob ein Spieler überhaupt einen gültigen Zug hat.
     * Wenn nicht, werden die Karten in die Mitte geworfen.
     */
    public static boolean hasAnyValidMove(Player player) {
        return player.hand().exists(card ->
                player.balls().exists(ball -> isValidMove(ball, card))
        );
    }

    /**
     * Prüft ob Karte + Ball eine gültige Aktion ergibt.
     */
    private static boolean isValidMove(Ball ball, Card card) {

        if (ball.isAtHome()) {
            return card.rank() == Rank.ACE
                    || card.rank() == Rank.KING
                    || card.rank() == Rank.JOKER;
        }

        // Joker kann 1–13 Schritte machen – gültig wenn mind. eine Option funktioniert
        if (card.rank() == Rank.JOKER) {
            for (int i = 1; i <= 13; i++) {
                if (ball.wouldMove(i)) return true;
            }
            return false;
        }

        return ball.wouldMove(card.getMoveValue());
    }

    public static boolean hasWon(Player player) {
        return player.balls()
                .forAll(Ball::isInGoal);
    }
}