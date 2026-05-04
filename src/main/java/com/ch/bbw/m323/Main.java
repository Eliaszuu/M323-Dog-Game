package com.ch.bbw.m323;

import com.ch.bbw.m323.engine.GameInitializer;
import com.ch.bbw.m323.model.GameState;

public class Main {

    public static void main(String[] args) throws Exception {

        GameState state = GameInitializer.newGame(4);

        state = GameInitializer.dealCardsFromApi(state);

        System.out.println("Game started:");
        System.out.println(state);
    }
}