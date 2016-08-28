package com.dimensionalwave.soda.actors.AI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.actors.Actor;
import com.dimensionalwave.soda.handlers.ContentManager;

public abstract class AI extends Actor {

    private float aiHealth = 100.0f;
    private boolean isRemovable = false;

    public AI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
    }

    public float getAiHealth() {
        return aiHealth;
    }

    public void setAiHealth(float aiHealth) {
        this.aiHealth = aiHealth;
    }

    public boolean isDead() {
        return (aiHealth <= 0.0f);
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public void remove() {
        world.destroyBody(physicsBody);
    }
}
