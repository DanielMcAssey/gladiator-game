package com.dimensionalwave.gladiator.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.dimensionalwave.gladiator.actors.ActorDirection;
import com.dimensionalwave.gladiator.enums.GameStates;
import com.dimensionalwave.gladiator.handlers.ActionAnimation;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.input.InputAction;
import com.dimensionalwave.gladiator.input.InputManager;

public class MenuState extends GameState {

    private int menuIndex = 0;
    private final int maxMenuIndex = 1;

    private ActionAnimation menuAnimation;

    private BitmapFont font = new BitmapFont();
    private Texture skyBg;
    private Texture paperBg;
    private Texture controls;
    private Texture logo;

    private GlyphLayout layout = new GlyphLayout();

    private float stateTime;

    public MenuState(GameStateManager manager) {
        super(manager);

        ContentManager contentManager = manager.game().getContentManager();
        contentManager.loadTexture("textures/backgrounds/sky.png", "bg_sky");
        contentManager.loadTexture("textures/backgrounds/paper.png", "bg_paper");
        contentManager.loadTexture("textures/hud/menu_selector.png", "menu_sword");
        contentManager.loadTexture("textures/hud/menu_controls.png", "menu_controls");
        contentManager.loadTexture("textures/hud/menu_logo.png", "menu_logo");

        contentManager.waitForLoad();

        skyBg = contentManager.getTexture("bg_sky");
        paperBg = contentManager.getTexture("bg_paper");
        controls = contentManager.getTexture("menu_controls");
        logo = contentManager.getTexture("menu_logo");
        menuAnimation = new ActionAnimation(contentManager.getTexture("menu_sword"), new Vector2(0, 0), 10, 0.05f, true, false);
        stateTime = 0f;
    }

    @Override
    public void handleInput() {
        if(InputManager.isPressed(InputAction.MENU_ENTER)) {
            if(menuIndex == 0) {
                gameStateManager.pushState(GameStates.PLAY);
            } else if(menuIndex == 1) {
                Gdx.app.exit();
            }
        }

        if(InputManager.isPressed(InputAction.MENU_UP)) {
            if(menuIndex - 1 >= 0) {
                menuIndex -= 1;
            } else {
                menuIndex = maxMenuIndex;
            }
        }

        if(InputManager.isPressed(InputAction.MENU_DOWN)) {
            if(menuIndex + 1 <= maxMenuIndex) {
                menuIndex += 1;
            } else {
                menuIndex = 0;
            }
        }
    }

    @Override
    public void update(float delta) {
        handleInput();
        stateTime += delta;
        menuAnimation.update(stateTime, new Vector2(((hudCamera.viewportWidth) / 2f) - 60, ((hudCamera.viewportHeight) / 2f) - (menuIndex * 30) + 2));
    }

    @Override
    public void render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(hudCamera.combined);

        spriteBatch.draw(skyBg, 0, 0);
        spriteBatch.draw(paperBg, (skyBg.getWidth() - paperBg.getWidth()) / 2f, (skyBg.getHeight() - paperBg.getHeight()) / 2f);

        float logoWidth = logo.getWidth() * 0.3f;
        float logoHeight = logo.getHeight() * 0.3f;
        spriteBatch.draw(logo,
                (hudCamera.viewportWidth / 2f) - (logoWidth / 2f),
                (hudCamera.viewportHeight / 2f) - (logoHeight / 2f) + 70,
                logoWidth,
                logoHeight);

        float controlWidth = controls.getWidth();
        float controlHeight = controls.getHeight();
        spriteBatch.draw(controls,
                (hudCamera.viewportWidth / 2f) - (controlWidth / 2f),
                (hudCamera.viewportHeight / 2f) - (controlHeight / 2f) - 90,
                controlWidth,
                controlHeight);

        layout.setText(font, "Start Game");
        font.draw(spriteBatch, layout, (hudCamera.viewportWidth - layout.width) / 2f, (hudCamera.viewportHeight + layout.height) / 2f);

        layout.setText(font, "Exit");
        font.draw(spriteBatch, layout, (hudCamera.viewportWidth - layout.width) / 2f, (hudCamera.viewportHeight + layout.height) / 2f - 30f);

        layout.setText(font, "Credits - Programmer: Daniel McAssey / Pixel Art: Elizabeth McAssey");
        font.draw(spriteBatch, layout, (hudCamera.viewportWidth - layout.width) / 2f, (10 + layout.height));

        menuAnimation.render(spriteBatch, ActorDirection.RIGHT);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        gameStateManager.game().getContentManager().disposeTexture("bg_sky");
        gameStateManager.game().getContentManager().disposeTexture("menu_sword");
    }
}
