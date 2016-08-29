package com.dimensionalwave.gladiator.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.helpers.HealthBar;

public class Hud extends Actor {

    private BitmapFont font;

    private TextureRegion heartTexture;
    private TextureRegion romanHeadTexture;
    private TextureRegion shieldTexture;
    private TextureRegion weaponTexture;

    private HealthBar healthBar;

    public Hud(ContentManager newContentManager, World newWorld, Vector2 startPosition, String newName) {
        super(newContentManager, newWorld, startPosition, newName);

        contentManager.loadTexture("textures/hud/hud.png", "sys_hud");

        contentManager.waitForLoad();

        font = new BitmapFont();

        Texture hudTexture = contentManager.getTexture("sys_hud");
        heartTexture = new TextureRegion(hudTexture, 0, 0, 16, 16);
        romanHeadTexture = new TextureRegion(hudTexture, 16, 0, 16, 16);
        shieldTexture = new TextureRegion(hudTexture, 32, 0, 16, 16);
        weaponTexture = new TextureRegion(hudTexture, 48, 0, 16, 16);

        healthBar = new HealthBar(new Vector2(40.0f, Constants.V_HEIGHT - 20.0f), new Vector2(100.0f, 14.0f));

        // Initialize
        setProperty("HUD_HEALTH", 100.0f);
        setProperty("HUD_WEAPON_STRENGTH", 100);
        setProperty("HUD_SHIELD_STRENGTH", 100);
        setProperty("HUD_KILL_COUNT", 0);
    }

    @Override
    public void update(float dT) {
        healthBar.update((Float)getProperty("HUD_HEALTH"), 100.0f);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        healthBar.render(spriteBatch);
        spriteBatch.draw(heartTexture, 15, Constants.V_HEIGHT - 21);

        font.draw(spriteBatch, ((Integer)getProperty("HUD_WEAPON_STRENGTH")).toString() + "%", Constants.V_WIDTH - 65, Constants.V_HEIGHT - 8);
        spriteBatch.draw(weaponTexture, Constants.V_WIDTH - 20, Constants.V_HEIGHT - 21);

        font.draw(spriteBatch, ((Integer)getProperty("HUD_SHIELD_STRENGTH")).toString() + "%", Constants.V_WIDTH - 65, Constants.V_HEIGHT - 28);
        spriteBatch.draw(shieldTexture, Constants.V_WIDTH - 20, Constants.V_HEIGHT - 41);

        font.draw(spriteBatch, ((Integer)getProperty("HUD_KILL_COUNT")).toString(), Constants.V_WIDTH - 65, Constants.V_HEIGHT - 48);
        spriteBatch.draw(romanHeadTexture, Constants.V_WIDTH - 20, Constants.V_HEIGHT - 61);
    }

    @Override
    public void dispose() {

    }
}
