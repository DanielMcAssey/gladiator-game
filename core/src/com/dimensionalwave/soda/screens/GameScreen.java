package com.dimensionalwave.soda.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.dimensionalwave.soda.actors.AI.AI;
import com.dimensionalwave.soda.actors.Player;

public class GameScreen implements Screen {

    private SpriteBatch spriteBatch;

    private Array<AI> enemyAI = new Array<AI>();
    private Player player;

    private TiledMap map;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        updateLevel(delta);
        drawLevel(spriteBatch);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void updateLevel(float delta) {
        // Update
        player.update(delta);

        for(AI ai : enemyAI) {
            ai.update(delta);
        }
    }

    private void drawLevel(SpriteBatch spriteBatch) {
        // Update
        player.render(spriteBatch);

        for(AI ai : enemyAI) {
            ai.render(spriteBatch);
        }

    }
}
