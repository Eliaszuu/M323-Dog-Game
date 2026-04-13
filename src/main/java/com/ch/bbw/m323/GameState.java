package com.ch.bbw.m323;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GameState {
    public int currentPlayer;
    public List<Set<String>> cardSets;
    public int countCardsThisRound;
    public HashMap<Integer, Integer> ballPosition;
    public int roundMode;
}
