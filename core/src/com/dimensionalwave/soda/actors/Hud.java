package com.dimensionalwave.soda.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.handlers.ContentManager;

public class Hud extends Actor {

    TextureRegion romanHeadTexture;
    TextureRegion shieldTexture;
    TextureRegion weaponTexture;

    public Hud(ContentManager newContentManager, World newWorld, Vector2 startPosition, String newName) {
        super(newContentManager, newWorld, startPosition, newName);
        setProperty("HUD_HEALTH", 100.0f);
        setProperty("HUD_WEAPON_STRENGTH", 1);
        setProperty("HUD_SHIELD_STRENGTH", 1);
        setProperty("HUD_KILL_COUNT", 0);

        contentManager.waitForLoad();

    }

    @Override
    public void update(float dT) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose() {

    }
}
