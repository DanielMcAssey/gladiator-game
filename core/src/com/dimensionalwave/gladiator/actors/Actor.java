package com.dimensionalwave.gladiator.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.handlers.ContentManager;

import java.util.HashMap;

public abstract class Actor {

    protected ContentManager contentManager;
    protected World world;
    protected HashMap<String, Object> actorProperties;
    protected Body physicsBody;
    protected String name;
    private Vector2 currentPosition;

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

    public Vector2 getScaledPosition() {
        if(physicsBody != null) {
            return new Vector2(physicsBody.getPosition().x * Box2DConstants.PPM, physicsBody.getPosition().y * Box2DConstants.PPM);
        }

        return new Vector2(currentPosition.x * Box2DConstants.PPM, currentPosition.y * Box2DConstants.PPM);
    }

    public void setPosition(Vector2 newPosition) {
        if(physicsBody != null) {
            physicsBody.setTransform(new Vector2((newPosition.x) / Box2DConstants.PPM, (newPosition.y) / Box2DConstants.PPM), 0);
        }

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
