package com.dimensionalwave.gladiator.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.enums.GameStates;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.input.InputAction;
import com.dimensionalwave.gladiator.input.InputManager;

public class MenuState extends GameState {

    BitmapFont font = new BitmapFont();

    public MenuState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void handleInput() {
        if(InputManager.isPressed(InputAction.MENU_ENTER)) {
            gameStateManager.pushState(GameStates.PLAY);
        }

        if(InputManager.isPressed(InputAction.MENU_UP)) {

        }

        if(InputManager.isPressed(InputAction.MENU_DOWN)) {

        }
    }

    @Override
    public void update(float delta) {
        handleInput();

    }

    @Override
    public void render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        font.draw(spriteBatch, "PRESS ENTER TO START", Constants.V_WIDTH / 2, Constants.V_HEIGHT / 2);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
