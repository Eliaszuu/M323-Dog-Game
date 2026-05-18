package com.ch.bbw.m323.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DrawResponse {
    public List<ApiCard> cards;
}