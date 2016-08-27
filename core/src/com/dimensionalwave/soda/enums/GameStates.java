package com.dimensionalwave.soda.enums;

public enum GameStates {

    PLAY(123123);

    private final int Id;
    GameStates(int Id) { this.Id = Id; }
    public int getValue() { return Id; }
}
