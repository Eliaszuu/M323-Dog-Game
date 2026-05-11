package com.ch.bbw.m323;

import com.ch.bbw.m323.engine.GameEngine;
import com.ch.bbw.m323.engine.GameInitializer;
import com.ch.bbw.m323.model.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        GameState state = GameInitializer.newGame(4);
        state = GameInitializer.dealCardsFromApi(state);

        Scanner scanner = new Scanner(System.in);

        while (!isRoundFinished(state)) {

            Player current = state.getCurrentPlayer();

            System.out.println("\n=================================");
            System.out.println("Player " + current.id() + " ist am Zug");
            System.out.println("Balls: ");

            for (int i = 0; i < current.balls().size(); i++) {
                System.out.println(i + ": Position " + current.balls().get(i).position());
            }

            System.out.println("\nHandkarten:");
            for (int i = 0; i < current.hand().size(); i++) {
                System.out.println(i + ": " + current.hand().get(i));
            }

            System.out.print("\nWelche Karte spielen? (Index): ");
            int cardIndex = scanner.nextInt();

            if (cardIndex < 0 || cardIndex >= current.hand().size()) {
                System.out.println("Ungültige Auswahl!");
                continue;
            }

            Card chosenCard = current.hand().get(cardIndex);

            System.out.print("Welchen Ball bewegen? (Index 0-3): ");
            int ballIndex = scanner.nextInt();

            if (ballIndex < 0 || ballIndex >= 4) {
                System.out.println("Ungültiger Ball!");
                continue;
            }

            state = GameEngine.playCard(state, ballIndex, chosenCard);
        }

        System.out.println("\nRunde beendet!");
    }

    private static boolean isRoundFinished(GameState state) {
        return state.players()
                .forAll(player -> player.hand().isEmpty());
    }
}