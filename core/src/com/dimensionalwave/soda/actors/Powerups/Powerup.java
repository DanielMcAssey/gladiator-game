package com.dimensionalwave.soda.actors.Powerups;

import com.badlogic.gdx.math.Vector2;
import com.dimensionalwave.soda.actors.Actor;

public abstract class Powerup extends Actor {

    public Powerup(Vector2 startPosition, String newName) {
        super(startPosition, newName);
    }

}
