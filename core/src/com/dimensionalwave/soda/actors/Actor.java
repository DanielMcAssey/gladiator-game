package com.dimensionalwave.soda.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Actor {

    private String name;
    private Vector2 currentPosition;

    public Actor(Vector2 startPosition, String newName) {
        name = newName;
        currentPosition = startPosition;
    }

    public Vector2 getPosition() {
        return currentPosition;
    }

    public void setPosition(Vector2 newPosition) {
        currentPosition = newPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void move(Vector2 moveDirection) {
        currentPosition.add(moveDirection);
    }

    public abstract void update(float dT);
    public abstract void render(SpriteBatch spriteBatch, float PPM);
    public abstract void dispose();
}
