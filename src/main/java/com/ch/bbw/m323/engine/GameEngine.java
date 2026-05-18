package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.model.*;

public class GameEngine {
    private static final int START_OFFSET = 16;


    public static int startPositionFor(int playerId) {
        return playerId * START_OFFSET + 1;
    }

    public static GameState playCard(GameState state, int ballIndex, Card card, int steps) {

        Player player = state.getCurrentPlayer();
        Ball ball = player.balls().get(ballIndex);
        Ball newBall;

        // Aus dem Haus
        if (ball.isAtHome()) {
            if (card.rank() == Rank.ACE
                    || card.rank() == Rank.KING
                    || card.rank() == Rank.JOKER) {
                // Jeder Spieler betritt das Feld an seiner eigenen Startposition
                newBall = Ball.enterBoard();
            } else {
                System.out.println("Du brauchst ASS, KÖNIG oder JOKER um aus dem Haus zu kommen!");
                return state;
            }
        } else {
            newBall = ball.move(steps);
            if (newBall.position() == ball.position()) {
                System.out.println("Ungültiger Zug – Ball kann sich nicht bewegen!");
                return state;
            }
        }

        Player updatedPlayer = player.updateBall(ballIndex, newBall).removeCard(card);
        GameState updatedState = state.updatePlayer(updatedPlayer);

        return updatedState.nextPlayer();
    }

    public static GameState playSevenSplit(GameState state, Card card,
                                           int ball1Idx, int steps1,
                                           int ball2Idx, int steps2) {

        Player player = state.getCurrentPlayer();
        Ball ball1 = player.balls().get(ball1Idx);
        Ball ball2 = player.balls().get(ball2Idx);

        if (ball1.isAtHome() || ball2.isAtHome()) {
            System.out.println("SIEBEN kann keine Bälle aus dem Haus holen!");
            return state;
        }
        if (!ball1.wouldMove(steps1)) {
            System.out.println("Ball " + ball1Idx + " kann sich nicht um " + steps1 + " Schritte bewegen!");
            return state;
        }
        if (!ball2.wouldMove(steps2)) {
            System.out.println("Ball " + ball2Idx + " kann sich nicht um " + steps2 + " Schritte bewegen!");
            return state;
        }

        Ball newBall1 = ball1.move(steps1);
        Ball newBall2 = ball2.move(steps2);

        Player updatedPlayer = player
                .updateBall(ball1Idx, newBall1)
                .updateBall(ball2Idx, newBall2)
                .removeCard(card);

        GameState updatedState = state.updatePlayer(updatedPlayer);

        return updatedState.nextPlayer();
    }


    public static GameState playJack(GameState state, Card card,
                                     int ownBallIdx,
                                     int targetPlayerIdx, int targetBallIdx) {

        Player currentPlayer = state.getCurrentPlayer();
        Player targetPlayer = state.players().get(targetPlayerIdx);

        Ball ownBall = currentPlayer.balls().get(ownBallIdx);
        Ball targetBall = targetPlayer.balls().get(targetBallIdx);

        if (ownBall.isAtHome() || ownBall.isInGoal()) {
            System.out.println("Eigener Ball muss auf dem Spielfeld sein (nicht im Haus/Ziel)!");
            return state;
        }
        if (targetBall.isAtHome() || targetBall.isInGoal()) {
            System.out.println("Zielball muss auf dem Spielfeld sein (nicht im Haus/Ziel)!");
            return state;
        }

        Player updatedCurrent = currentPlayer
                .updateBall(ownBallIdx, new Ball(targetBall.position()))
                .removeCard(card);
        Player updatedTarget = targetPlayer
                .updateBall(targetBallIdx, new Ball(ownBall.position()));

        return state
                .updatePlayer(updatedCurrent)
                .updatePlayerAt(targetPlayerIdx, updatedTarget)
                .nextPlayer();
    }

    public static boolean hasAnyValidMove(Player player) {
        return player.hand().exists(card ->
                player.balls().exists(ball -> isValidMove(ball, card))
        );
    }

    public static boolean hasWon(Player player) {
        return player.balls().forAll(Ball::isInGoal);
    }

    private static boolean isValidMove(Ball ball, Card card) {
        if (ball.isAtHome()) {
            return card.rank() == Rank.ACE
                    || card.rank() == Rank.KING
                    || card.rank() == Rank.JOKER;
        }
        return switch (card.rank()) {
            case JACK -> !ball.isInGoal();
            case SEVEN -> ball.wouldMove(1);
            case FOUR -> ball.wouldMove(4) || ball.wouldMove(-4);
            case JOKER -> {
                for (int i = 1; i <= 13; i++) {
                    if (ball.wouldMove(i)) yield true;
                }
                yield false;
            }
            default -> ball.wouldMove(card.getMoveValue());
        };
    }
}