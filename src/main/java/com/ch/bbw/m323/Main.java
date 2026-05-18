package com.ch.bbw.m323;

import com.ch.bbw.m323.engine.GameEngine;
import com.ch.bbw.m323.engine.GameInitializer;
import com.ch.bbw.m323.model.*;
import io.vavr.collection.List;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        GameState state = GameInitializer.newGame(4);
        GameHistory history = GameHistory.empty();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            state = dealAndSwitch(state, scanner);
            System.out.println("\n=== Neue Runde gestartet ===");

            while (!isRoundFinished(state)) {
                var result = playTurn(state, history, scanner);
                state = result._1;
                history = result._2;
            }

            System.out.println("\n=== Runde beendet – neue Karten werden verteilt ===");
            history = GameHistory.empty();
        }
    }


    private static GameState dealAndSwitch(GameState state, Scanner scanner) throws Exception {
        state = GameInitializer.dealCardsFromApi(state);
        GameState stateBeforeSwitch = state;
        state = doCardSwitch(state, scanner);

        System.out.print("Kartentausch rückgängig machen? (u = ja, f = weiter): ");
        if (scanner.next().trim().equalsIgnoreCase("u")) {
            System.out.println("↩ Kartentausch rückgängig gemacht.");
            state = doCardSwitch(stateBeforeSwitch, scanner);
        }
        return state;
    }

    private static GameState doCardSwitch(GameState state, Scanner scanner) {
        System.out.println("\n=== Kartentausch ===");
        System.out.println("Jeder Spieler gibt eine Karte an den rechten Nachbarn weiter.");

        int n = state.players().size();
        Card[] cardsToPass = List.range(0, n)
                .map(i -> pickCardToPass(state.players().get(i), scanner))
                .toJavaArray(Card[]::new);

        return List.range(0, n).foldLeft(state, (s, i) -> {
            int fromIdx = (i - 1 + n) % n;
            Player p = s.players().get(i);
            return s.updatePlayerAt(i, p.replaceCard(cardsToPass[i], cardsToPass[fromIdx]));
        });
    }

    private static Card pickCardToPass(Player player, Scanner scanner) {
        System.out.println("\nSpieler " + player.id() + " → welche Karte weitergeben?");
        printHand(player);
        int idx = readInt(scanner);
        if (idx < 0 || idx >= player.hand().size()) {
            System.out.println("Ungültig → erste Karte wird gewählt.");
            return player.hand().get(0);
        }
        return player.hand().get(idx);
    }


    private static io.vavr.Tuple2<GameState, GameHistory> playTurn(
            GameState state, GameHistory history, Scanner scanner) {

        Player current = state.getCurrentPlayer();

        if (GameEngine.hasWon(current)) {
            System.out.println("\n✓ Spieler " + current.id() + " hat gewonnen!");
            System.exit(0);
        }

        printPlayerStatus(current);

        if (!GameEngine.hasAnyValidMove(current)) {
            System.out.println("Kein gültiger Zug möglich → Karten werden in die Mitte geworfen.");
            return io.vavr.Tuple.of(
                    state.updatePlayer(current.withHand(List.empty())).nextPlayer(),
                    history.push(state)
            );
        }

        printHand(current);
        return readAndExecuteMove(state, history, current, scanner);
    }

    private static io.vavr.Tuple2<GameState, GameHistory> readAndExecuteMove(
            GameState state, GameHistory history, Player current, Scanner scanner) {

        String input = readCardInput(scanner);

        if (input.equals("u")) {
            return applyUndo(state, history);
        }

        int cardIndex = parseCardIndex(input, current);
        if (cardIndex < 0) return io.vavr.Tuple.of(state, history);

        Card card = current.hand().get(cardIndex);
        GameState newState = executeCard(state, card, current, scanner);
        if (newState == state) return io.vavr.Tuple.of(state, history);

        return io.vavr.Tuple.of(newState, history.push(state));
    }

    private static String readCardInput(Scanner scanner) {
        while (true) {
            System.out.print("Kartenindex wählen (oder 'u' für Undo): ");
            String input = scanner.next().trim().toLowerCase();
            if (input.equals("u") || input.matches("\\d+")) return input;
            System.out.println("\u001B[31m Bitte eine gültige Zahl oder 'u' eingeben!\u001B[0m");
        }
    }

    private static io.vavr.Tuple2<GameState, GameHistory> applyUndo(
            GameState state, GameHistory history) {
        if (!history.canUndo()) {
            System.out.println("Kein Zug zum Rückgängigmachen vorhanden.");
            return io.vavr.Tuple.of(state, history);
        }
        System.out.println("↩ Letzter Zug rückgängig gemacht.");
        return io.vavr.Tuple.of(history.previous(), history.pop());
    }

    private static int parseCardIndex(String input, Player current) {
        int idx = Integer.parseInt(input);
        if (idx < 0 || idx >= current.hand().size()) {
            System.out.println("Ungültig!");
            return -1;
        }
        return idx;
    }


    private static GameState executeCard(GameState state, Card card, Player current, Scanner scanner) {
        return switch (card.rank()) {
            case JACK -> executeJack(state, card, scanner);
            case SEVEN -> executeSeven(state, card, scanner);
            case FOUR -> executeFour(state, card, scanner);
            case JOKER -> executeJoker(state, card, current, scanner);
            default -> executeStandard(state, card, scanner);
        };
    }

    private static GameState executeJack(GameState state, Card card, Scanner scanner) {
        printAllBalls(state);
        System.out.print("Eigenen Ball wählen (0-3): ");
        int ownBall = scanner.nextInt();
        System.out.print("Spieler des Zielballs (0-3): ");
        int targetPlayer = scanner.nextInt();
        System.out.print("Zielball wählen (0-3): ");
        int targetBall = scanner.nextInt();
        return GameEngine.playJack(state, card, ownBall, targetPlayer, targetBall);
    }

    private static GameState executeSeven(GameState state, Card card, Scanner scanner) {
        System.out.print("Ball wählen (0-3): ");
        int ballIndex = scanner.nextInt();
        System.out.print("Schritte für Ball " + ballIndex + " (1-6 = aufteilen, 7 = alles): ");
        int steps1 = scanner.nextInt();
        if (steps1 < 1 || steps1 > 7) {
            System.out.println("Ungültig!");
            return state;
        }
        if (steps1 == 7) return GameEngine.playCard(state, ballIndex, card, 7);

        int steps2 = 7 - steps1;
        System.out.print("Zweiten Ball wählen (bekommt " + steps2 + " Schritte): ");
        int ball2 = scanner.nextInt();
        if (ball2 < 0 || ball2 > 3) {
            System.out.println("Ungültig!");
            return state;
        }
        return GameEngine.playSevenSplit(state, card, ballIndex, steps1, ball2, steps2);
    }

    private static GameState executeFour(GameState state, Card card, Scanner scanner) {
        System.out.print("Ball wählen (0-3): ");
        int ballIndex = scanner.nextInt();
        System.out.print("Richtung: (v)orwärts +4 oder (r)ückwärts -4? ");
        int steps = scanner.next().trim().toLowerCase().startsWith("r") ? -4 : 4;
        return GameEngine.playCard(state, ballIndex, card, steps);
    }

    private static GameState executeJoker(GameState state, Card card, Player current, Scanner scanner) {
        System.out.print("Ball wählen (0-3): ");
        int ballIndex = scanner.nextInt();
        if (current.balls().get(ballIndex).isAtHome()) {
            return GameEngine.playCard(state, ballIndex, card, 0);
        }
        System.out.print("Joker: Wie viele Schritte? (1-13): ");
        int jokerSteps = scanner.nextInt();
        if (jokerSteps < 1 || jokerSteps > 13) {
            System.out.println("Ungültig!");
            return state;
        }
        return GameEngine.playCard(state, ballIndex, card, jokerSteps);
    }

    private static GameState executeStandard(GameState state, Card card, Scanner scanner) {
        System.out.print("Ball wählen (0-3): ");
        int ballIndex = scanner.nextInt();
        if (ballIndex < 0 || ballIndex > 3) {
            System.out.println("Ungültig!");
            return state;
        }
        return GameEngine.playCard(state, ballIndex, card, card.getMoveValue());
    }


    private static void printPlayerStatus(Player current) {
        System.out.println("\n--------------------------------");
        System.out.println("Spieler " + current.id());
        List.range(0, 4).forEach(i ->
                System.out.println("Ball " + i + ": Position " + current.balls().get(i).position()));
    }

    private static void printHand(Player player) {
        System.out.println("\nHand:");
        List.range(0, player.hand().size()).forEach(i ->
                System.out.println("  " + i + ": " + player.hand().get(i)));
    }

    private static void printAllBalls(GameState state) {
        System.out.println("Alle Bälle auf dem Feld:");
        state.players().forEach(pl -> {
            System.out.print("  Spieler " + pl.id() + ": ");
            List.range(0, 4).forEach(b ->
                    System.out.print("Ball" + b + "@" + pl.balls().get(b).position() + "  "));
            System.out.println();
        });
    }


    private static int readInt(Scanner scanner) {
        while (true) {
            System.out.print("Index: ");
            if (scanner.hasNextInt()) return scanner.nextInt();
            System.out.println("\u001B[31m Bitte eine gültige Zahl eingeben!\u001B[0m");
            scanner.next();
        }
    }

    private static boolean isRoundFinished(GameState state) {
        return state.players().forAll(p -> p.hand().isEmpty());
    }
}