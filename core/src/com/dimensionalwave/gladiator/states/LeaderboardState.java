package com.dimensionalwave.gladiator.states;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.dimensionalwave.gladiator.enums.GameStates;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.input.InputAction;
import com.dimensionalwave.gladiator.input.InputManager;

public class LeaderboardState extends GameState {

    GlyphLayout layout;
    private BitmapFont font = new BitmapFont();

    public LeaderboardState(GameStateManager manager) {
        super(manager);

        layout = new GlyphLayout(font, "Congratulations on defeating Caesar!\n\nSadly we could not finish the leaderboard on time,\nso thats why you are staring at this message, simply press enter to reutn to the menu.");
    }

    @Override
    public void handleInput() {
        if(InputManager.isPressed(InputAction.MENU_ENTER)) {
            gameStateManager.pushState(GameStates.MENU);
        }

    }

    @Override
    public void update(float delta) {
        handleInput();
    }

    @Override
    public void render() {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        font.draw(spriteBatch, layout, (hudCamera.viewportWidth - layout.width) / 2f, (hudCamera.viewportHeight + layout.height) / 2);
        spriteBatch.end();

    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
