package com.ch.bbw.m323.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vavr.collection.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrawResponse(List<ApiCard> cards) {

    @JsonCreator
    public static DrawResponse create(
            @JsonProperty("cards") java.util.List<ApiCard> cards) {
        return new DrawResponse(List.ofAll(cards != null ? cards : new ArrayList<>()));
    }
}