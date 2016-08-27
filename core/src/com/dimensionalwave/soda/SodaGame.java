package com.dimensionalwave.soda;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dimensionalwave.soda.handlers.GameStateManager;
import com.dimensionalwave.soda.input.InputHandler;
import com.dimensionalwave.soda.input.InputManager;

public class SodaGame extends ApplicationAdapter {

	private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private GameStateManager gameStateManager;

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    @Override
	public void create () {
        Gdx.input.setInputProcessor(new InputHandler());

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.V_WIDTH, Constants.V_HEIGHT);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constants.V_WIDTH, Constants.V_HEIGHT);

        gameStateManager = new GameStateManager(this);
	}

	@Override
	public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();

        gameStateManager.update(deltaTime);
        gameStateManager.render();
        InputManager.update();
	}
	
	@Override
	public void dispose () {

	}
}
