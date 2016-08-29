package com.dimensionalwave.gladiator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.input.InputHandler;
import com.dimensionalwave.gladiator.input.InputManager;

public class GladiatorGame extends ApplicationAdapter {

	private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private GameStateManager gameStateManager;
    private ContentManager contentManager;

    public ContentManager getContentManager() {
        return contentManager;
    }

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

        contentManager = new ContentManager();

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.V_WIDTH / Constants.CAM_SCALE, Constants.V_HEIGHT / Constants.CAM_SCALE);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constants.V_WIDTH, Constants.V_HEIGHT);

        gameStateManager = new GameStateManager(this);
	}

	@Override
	public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(!contentManager.isLoaded()) {
            contentManager.update();
        }

        gameStateManager.update(deltaTime);
        gameStateManager.render();
        InputManager.update();

        spriteBatch.setProjectionMatrix(hudCamera.combined);
	}
	
	@Override
	public void dispose () {

	}
}
