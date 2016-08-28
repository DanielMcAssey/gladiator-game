package com.dimensionalwave.soda.actors.Powerups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.handlers.ContentManager;

public class ArmorPowerup extends Powerup {

    public ArmorPowerup(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
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
