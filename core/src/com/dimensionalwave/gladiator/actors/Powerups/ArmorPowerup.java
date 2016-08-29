package com.dimensionalwave.gladiator.actors.Powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.actors.ActorDirection;
import com.dimensionalwave.gladiator.handlers.ActionAnimation;
import com.dimensionalwave.gladiator.handlers.ContentManager;

public class ArmorPowerup extends Powerup {

    private ActionAnimation animation;

    private float stateTime;

    public ArmorPowerup(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/powerups/shield.png", "powerup_armor");

        contentManager.waitForLoad();

        Texture powerupArmor = contentManager.getTexture("powerup_armor");
        animation = new ActionAnimation(powerupArmor, new Vector2(0, 0), 21, 0.05f, true);
        stateTime = 0f;

        Vector2 textureSize = new Vector2(powerupArmor.getWidth() / 21, 16);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (getPosition().x + 0.5f) * (textureSize.x) / Box2DConstants.PPM,
                (getPosition().y + 0.5f) * (textureSize.y) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x) / 2 / Box2DConstants.PPM,
                (textureSize.y) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_POWERUP;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_POWERUP;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef).setUserData("powerup_body");
        physicsBody.setUserData(this);
        polygonShape.dispose();
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
        animation.update(stateTime, getPosition());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        animation.render(spriteBatch, ActorDirection.RIGHT);
    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("powerup_armor");
        world.destroyBody(physicsBody);
    }
}
