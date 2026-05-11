package com.ch.bbw.m323;

import com.ch.bbw.m323.engine.GameEngine;
import com.ch.bbw.m323.engine.GameInitializer;
import com.ch.bbw.m323.model.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        GameState state = GameInitializer.newGame(4);
        Scanner scanner = new Scanner(System.in);

        while (true) {

            state = GameInitializer.dealCardsFromApi(state);

            System.out.println("\n=== Neue Runde gestartet ===");

            while (!isRoundFinished(state)) {

                Player current = state.getCurrentPlayer();

                if (GameEngine.hasWon(current)) {
                    System.out.println("\n🎉 Spieler " + current.id() + " hat gewonnen!");
                    return;
                }

                System.out.println("\n--------------------------------");
                System.out.println("Spieler " + current.id());

                for (int i = 0; i < 4; i++) {
                    System.out.println("Ball " + i + ": " +
                            current.balls().get(i).position());
                }

                for (int i = 0; i < current.hand().size(); i++) {
                    System.out.println(i + ": " + current.hand().get(i));
                }

                System.out.print("Kartenindex wählen: ");
                int cardIndex = scanner.nextInt();

                if (cardIndex < 0 || cardIndex >= current.hand().size()) {
                    System.out.println("Ungültig!");
                    continue;
                }

                Card card = current.hand().get(cardIndex);

                System.out.print("Ball wählen (0-3): ");
                int ballIndex = scanner.nextInt();

                if (ballIndex < 0 || ballIndex > 3) {
                    System.out.println("Ungültig!");
                    continue;
                }

                state = GameEngine.playCard(state, ballIndex, card);
            }

            System.out.println("\n=== Runde beendet – neue Karten werden verteilt ===");
        }
    }

    private static boolean isRoundFinished(GameState state) {
        return state.players()
                .forAll(p -> p.hand().isEmpty());
    }
}