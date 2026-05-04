package com.ch.bbw.m323.api;

import com.ch.bbw.m323.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeckApiService {

    private static final String BASE_URL = "https://deckofcardsapi.com/api/deck";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String createNewDeck() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/new/shuffle/?deck_count=1"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        DeckCreateResponse result =
                mapper.readValue(response.body(), DeckCreateResponse.class);

        return result.deck_id;
    }

    public List<Card> drawCards(String deckId, int count) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + deckId + "/draw/?count=" + count))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        DrawResponse result =
                mapper.readValue(response.body(), DrawResponse.class);

        return List.ofAll(result.cards)
                .map(this::mapToCard);
    }

    private Card mapToCard(ApiCard apiCard) {
        Rank rank = mapRank(apiCard.value);
        Suit suit = mapSuit(apiCard.suit);
        return new Card(rank, suit);
    }

    private Rank mapRank(String value) {
        return Rank.fromApi(value);
    }

    private Suit mapSuit(String suit) {
        return Suit.valueOf(suit);
    }
}