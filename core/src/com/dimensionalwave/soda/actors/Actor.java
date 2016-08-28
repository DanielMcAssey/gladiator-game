package com.dimensionalwave.soda.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.handlers.ContentManager;

import java.util.HashMap;

public abstract class Actor {

    protected ContentManager contentManager;
    protected World world;
    protected HashMap<String, Object> actorProperties;
    protected Body physicsBody;
    protected String name;
    protected Vector2 currentPosition;

    public Actor(ContentManager newContentManager, World newWorld, Vector2 startPosition, String newName) {
        actorProperties = new HashMap<String, Object>();
        contentManager = newContentManager;
        world = newWorld;
        name = newName;
        currentPosition = startPosition;
    }

    public Vector2 getPosition() {
        if(physicsBody != null) {
            return physicsBody.getPosition();
        }

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

    public Object getProperty(String key) {
        if(actorProperties.containsKey(key)) {
            return actorProperties.get(key);
        }

        return null;
    }

    public void setProperty(String key, Object value) {
        actorProperties.put(key, value);
    }

    public void move(Vector2 moveDirection) {
        currentPosition.add(moveDirection);
    }

    public Body getPhysicsBody() { return physicsBody; }

    public abstract void update(float dT);
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void dispose();
}
