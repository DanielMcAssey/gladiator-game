package com.dimensionalwave.gladiator.enums;

public enum GameStates {

    MENU(32980),
    PLAY(123123),
    LEADERBOARD(2131244124);

    private final int Id;
    GameStates(int Id) { this.Id = Id; }
    public int getValue() { return Id; }
}
