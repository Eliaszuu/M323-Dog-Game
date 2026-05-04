package com.ch.bbw.m323.engine;

import com.ch.bbw.m323.model.Card;
import com.ch.bbw.m323.model.Rank;
import com.ch.bbw.m323.model.Suit;
import io.vavr.collection.List;

import java.util.Collections;
import java.util.ArrayList;

public class Deck {

    public static List<Card> createShuffledDeck() {

        List<Card> deck =
                List.of(Suit.values())
                        .flatMap(suit ->
                                List.of(Rank.values())
                                        .map(rank -> new Card(rank, suit))
                        );

        java.util.List<Card> javaDeck = new ArrayList<>(deck.toJavaList());
        Collections.shuffle(javaDeck);

        return List.ofAll(javaDeck);
    }
}
