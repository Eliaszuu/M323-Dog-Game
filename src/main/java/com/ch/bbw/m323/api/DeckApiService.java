package com.ch.bbw.m323.api;

import com.ch.bbw.m323.model.*;
import com.fasterxml.jackson.databind.JsonNode;
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

        String url =
                BASE_URL + "/new/shuffle/?deck_count=1&jokers_enabled=true";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode node = mapper.readTree(response.body());

        return node.get("deck_id").asText();
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

        if (value.equalsIgnoreCase("JOKER")) {
            return Rank.JOKER;
        }

        return switch (value) {
            case "ACE" -> Rank.ACE;
            case "KING" -> Rank.KING;
            case "QUEEN" -> Rank.QUEEN;
            case "JACK" -> Rank.JACK;
            case "10" -> Rank.TEN;
            case "9" -> Rank.NINE;
            case "8" -> Rank.EIGHT;
            case "7" -> Rank.SEVEN;
            case "6" -> Rank.SIX;
            case "5" -> Rank.FIVE;
            case "4" -> Rank.FOUR;
            case "3" -> Rank.THREE;
            case "2" -> Rank.TWO;
            default -> throw new IllegalArgumentException("Unknown rank: " + value);
        };
    }

    private Suit mapSuit(String suit) {

        if (suit.equalsIgnoreCase("BLACK")
                || suit.equalsIgnoreCase("RED")) {

            // Joker -> Suit egal
            return Suit.SPADES;
        }

        return Suit.valueOf(suit.toUpperCase());
    }
}