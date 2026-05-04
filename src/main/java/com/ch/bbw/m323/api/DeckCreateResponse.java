package com.ch.bbw.m323.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeckCreateResponse {
    public boolean success;
    public String deck_id;
    public int remaining;
    public boolean shuffled;
}