package com.dimensionalwave.soda.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dimensionalwave.soda.SodaGame;
import com.dimensionalwave.soda.handlers.GameStateManager;

public abstract class GameState {

    protected GameStateManager gameStateManager;
    protected SodaGame game;

    protected SpriteBatch spriteBatch;
    protected OrthographicCamera camera;
    protected OrthographicCamera hudCamera;

    protected GameState(GameStateManager manager) {
        gameStateManager = manager;
        game = gameStateManager.game();
        spriteBatch = game.getSpriteBatch();
        camera = game.getCamera();
        hudCamera = game.getHudCamera();
    }

    public abstract void handleInput();
    public abstract void update(float delta);
    public abstract void render();
    public abstract void dispose();

}
