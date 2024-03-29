package com.dimensionalwave.gladiator.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.levels.LevelManager;

public class PlayState extends GameState {

    LevelManager levelManager;

    public PlayState(GameStateManager manager) {
        super(manager);

        levelManager = new LevelManager(manager);
        levelManager.load();
        levelManager.loadLevel(0);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float delta) {
        handleInput();

        levelManager.update(delta);
    }

    @Override
    public void render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelManager.render(spriteBatch);
    }

    @Override
    public void dispose() {
        levelManager.dispose();
        levelManager = null;
    }
}
