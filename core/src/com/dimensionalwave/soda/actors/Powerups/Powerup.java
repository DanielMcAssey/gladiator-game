package com.dimensionalwave.soda.actors.Powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.actors.Actor;
import com.dimensionalwave.soda.handlers.ContentManager;

public abstract class Powerup extends Actor {

    public Powerup(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
    }

}
