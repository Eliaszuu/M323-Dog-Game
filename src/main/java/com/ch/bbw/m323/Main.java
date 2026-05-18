package com.ch.bbw.m323;

import com.ch.bbw.m323.engine.GameEngine;
import com.ch.bbw.m323.engine.GameInitializer;
import com.ch.bbw.m323.model.*;
import io.vavr.collection.List;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        GameState state = GameInitializer.newGame(4);
        Scanner scanner = new Scanner(System.in);

        while (true) {

            state = GameInitializer.dealCardsFromApi(state);
            state = doCardSwitch(state, scanner);

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
                    System.out.println("Ball " + i + ": Position " + current.balls().get(i).position());
                }

                if (!GameEngine.hasAnyValidMove(current)) {
                    System.out.println("Kein gültiger Zug möglich – Karten werden in die Mitte geworfen.");
                    state = state.updatePlayer(current.withHand(List.empty())).nextPlayer();
                    continue;
                }

                System.out.println("\nHand:");
                for (int i = 0; i < current.hand().size(); i++) {
                    System.out.println("  " + i + ": " + current.hand().get(i));
                }

                System.out.print("Kartenindex wählen: ");
                int cardIndex = scanner.nextInt();

                if (cardIndex < 0 || cardIndex >= current.hand().size()) {
                    System.out.println("Ungültig!");
                    continue;
                }

                Card card = current.hand().get(cardIndex);

                if (card.rank() == Rank.JACK) {
                    System.out.println("Alle Bälle auf dem Feld:");
                    for (int p = 0; p < state.players().size(); p++) {
                        Player pl = state.players().get(p);
                        System.out.print("  Spieler " + pl.id() + ": ");
                        for (int b = 0; b < 4; b++) {
                            System.out.print("Ball" + b + "@" + pl.balls().get(b).position() + "  ");
                        }
                        System.out.println();
                    }
                    System.out.print("Eigenen Ball wählen (0-3): ");
                    int ownBall = scanner.nextInt();
                    System.out.print("Spieler des Zielballs (0-3): ");
                    int targetPlayer = scanner.nextInt();
                    System.out.print("Zielball wählen (0-3): ");
                    int targetBall = scanner.nextInt();
                    state = GameEngine.playJack(state, card, ownBall, targetPlayer, targetBall);
                    continue;
                }

                System.out.print("Ball wählen (0-3): ");
                int ballIndex = scanner.nextInt();

                if (ballIndex < 0 || ballIndex > 3) {
                    System.out.println("Ungültig!");
                    continue;
                }

                if (card.rank() == Rank.SEVEN) {
                    System.out.print("Schritte für Ball " + ballIndex + " (1–6 = aufteilen, 7 = alles): ");
                    int steps1 = scanner.nextInt();
                    if (steps1 < 1 || steps1 > 7) {
                        System.out.println("Ungültig!");
                        continue;
                    }
                    if (steps1 == 7) {
                        state = GameEngine.playCard(state, ballIndex, card, 7);
                    } else {
                        int steps2 = 7 - steps1;
                        System.out.print("Zweiten Ball wählen (bekommt die restlichen " + steps2 + " Schritte): ");
                        int ball2 = scanner.nextInt();
                        if (ball2 < 0 || ball2 > 3) {
                            System.out.println("Ungültig!");
                            continue;
                        }
                        state = GameEngine.playSevenSplit(state, card, ballIndex, steps1, ball2, steps2);
                    }
                    continue;
                }

                if (card.rank() == Rank.FOUR) {
                    System.out.print("Richtung – (v)orwärts +4 oder (r)ückwärts -4? ");
                    String dir = scanner.next().trim().toLowerCase();
                    int steps = dir.startsWith("r") ? -4 : 4;
                    state = GameEngine.playCard(state, ballIndex, card, steps);
                    continue;
                }

                if (card.rank() == Rank.JOKER) {
                    Ball selectedBall = current.balls().get(ballIndex);
                    if (!selectedBall.isAtHome()) {
                        System.out.print("Joker: Wie viele Schritte? (1–13): ");
                        int jokerSteps = scanner.nextInt();
                        if (jokerSteps < 1 || jokerSteps > 13) {
                            System.out.println("Ungültig!");
                            continue;
                        }
                        state = GameEngine.playCard(state, ballIndex, card, jokerSteps);
                        continue;
                    }
                    // Joker aus dem Haus: steps wird ignoriert
                    state = GameEngine.playCard(state, ballIndex, card, 0);
                    continue;
                }

                // ── Standardzug ─────────────────────────────────────────────────────
                state = GameEngine.playCard(state, ballIndex, card, card.getMoveValue());
            }

            System.out.println("\n=== Runde beendet – neue Karten werden verteilt ===");
        }
    }

    private static GameState doCardSwitch(GameState state, Scanner scanner) {
        System.out.println("\n=== Kartentausch ===");
        System.out.println("Jeder Spieler gibt eine Karte an den rechten Nachbarn weiter.");

        int n = state.players().size();
        Card[] cardsToPass = new Card[n];

        for (int i = 0; i < n; i++) {
            Player p = state.players().get(i);
            System.out.println("\nSpieler " + p.id() + " – welche Karte weitergeben?");
            for (int j = 0; j < p.hand().size(); j++) {
                System.out.println("  " + j + ": " + p.hand().get(j));
            }
            System.out.print("Index: ");
            int idx = scanner.nextInt();
            if (idx < 0 || idx >= p.hand().size()) {
                System.out.println("Ungültig – erste Karte wird gewählt.");
                idx = 0;
            }
            cardsToPass[i] = p.hand().get(idx);
        }


        GameState result = state;
        for (int i = 0; i < n; i++) {
            int fromIdx = (i - 1 + n) % n;
            Player p = result.players().get(i);
            result = result.updatePlayerAt(i, p.replaceCard(cardsToPass[i], cardsToPass[fromIdx]));
        }
        return result;
    }

    private static boolean isRoundFinished(GameState state) {
        return state.players().forAll(p -> p.hand().isEmpty());
    }
}