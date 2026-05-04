package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.api.DeckApiService;
import com.ch.bbw.m323.model.*;
import io.vavr.collection.List;

public class GameInitializer {

    public static GameState newGame(int playerCount) {

        List<Player> players =
                List.range(0, playerCount)
                        .map(id ->
                                new Player(
                                        id,
                                        List.of(
                                                new Ball(0),
                                                new Ball(0),
                                                new Ball(0),
                                                new Ball(0)
                                        ),
                                        List.empty()
                                )
                        );

        return new GameState(
                0,
                players,
                RoundMode.PLAY_CARDS,
                6
        );
    }

    public static GameState dealCardsFromApi(GameState state) throws Exception {

        DeckApiService api = new DeckApiService();

        String deckId = api.createNewDeck();

        int totalCards =
                state.players().size() * state.cardsPerRound();

        List<Card> drawnCards =
                api.drawCards(deckId, totalCards);

        List<Player> updatedPlayers =
                state.players().zipWithIndex().map(tuple -> {

                    Player player = tuple._1;
                    int index = tuple._2;

                    int start = index * state.cardsPerRound();
                    int end = start + state.cardsPerRound();

                    List<Card> hand = drawnCards.slice(start, end);

                    return player.withHand(hand);
                });

        return new GameState(
                state.currentPlayer(),
                updatedPlayers,
                state.roundMode(),
                state.cardsPerRound()
        );
    }
}