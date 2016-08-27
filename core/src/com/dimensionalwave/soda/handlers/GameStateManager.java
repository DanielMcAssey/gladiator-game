package com.dimensionalwave.soda.handlers;

import com.dimensionalwave.soda.SodaGame;
import com.dimensionalwave.soda.enums.GameStates;
import com.dimensionalwave.soda.states.GameState;
import com.dimensionalwave.soda.states.PlayState;

import java.util.Stack;

public class GameStateManager {

    private SodaGame game;

    private Stack<GameState> gameStateStack;

    public GameStateManager(SodaGame game) {
        this.game = game;
        gameStateStack = new Stack<GameState>();
        pushState(GameStates.PLAY);
    }

    public SodaGame game() {
        return game;
    }

    public void update(float delta) {
        gameStateStack.peek().update(delta);
    }

    public void render() {
        gameStateStack.peek().render();
    }

    public void setState(GameStates newState) {
        popState();
        pushState(newState);
    }

    public void pushState(GameStates newState) {
        gameStateStack.push(getState(newState));
    }

    public void popState() {
        GameState state = gameStateStack.pop();
        state.dispose();
    }

    private GameState getState(GameStates gameState) {

        switch (gameState) {
            case PLAY:
                return new PlayState(this);
            default:
                return null;
        }
    }
}
