package com.dimensionalwave.gladiator.actors.Powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.actors.Actor;
import com.dimensionalwave.gladiator.handlers.ContentManager;

public abstract class Powerup extends Actor {

    public Powerup(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
    }

}
