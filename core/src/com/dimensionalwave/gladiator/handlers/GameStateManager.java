package com.dimensionalwave.gladiator.handlers;

import com.dimensionalwave.gladiator.GladiatorGame;
import com.dimensionalwave.gladiator.enums.GameStates;
import com.dimensionalwave.gladiator.states.GameState;
import com.dimensionalwave.gladiator.states.LeaderboardState;
import com.dimensionalwave.gladiator.states.MenuState;
import com.dimensionalwave.gladiator.states.PlayState;

import java.util.Stack;

public class GameStateManager {

    private GladiatorGame game;

    private Stack<GameState> gameStateStack;

    public GameStateManager(GladiatorGame game) {
        this.game = game;
        gameStateStack = new Stack<GameState>();
        pushState(GameStates.MENU);
    }

    public GladiatorGame game() {
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
            case MENU:
                return new MenuState(this);
            case PLAY:
                return new PlayState(this);
            case LEADERBOARD:
                return new LeaderboardState(this);
            default:
                return null;
        }
    }
}
