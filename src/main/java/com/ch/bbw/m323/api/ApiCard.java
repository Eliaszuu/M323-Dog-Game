package com.ch.bbw.m323.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiCard {
    public String value;
    public String suit;
    public String code;
}