package com.dimensionalwave.gladiator.actors.AI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.actors.ActorDirection;
import com.dimensionalwave.gladiator.handlers.ActionAnimation;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.helpers.CharacterAction;
import com.dimensionalwave.gladiator.helpers.HealthBar;

import java.util.HashMap;

enum AIAnimation {
    IDLE,
    WALK,
    ATTACK,
    BLOCK,
    BLOCK_RESET,
    DEATH
}

public class RomanAI extends AI {

    private HashMap<AIAnimation, ActionAnimation> animations = new HashMap<AIAnimation, ActionAnimation>();

    private AIAnimation activeAnimationType;
    private ActionAnimation activeAnimation;
    private ActorDirection actorDirection;

    private HealthBar healthBar;

    private float stateTime;

    private void setAnimation(AIAnimation newAnimation) {
        if(activeAnimationType != null && activeAnimationType.equals(newAnimation) || !animations.containsKey(newAnimation)) {
            return;
        }

        stateTime = 0f;
        activeAnimationType = newAnimation;
        activeAnimation = animations.get(newAnimation);
        activeAnimation.update(stateTime, getPosition());
    }

    public RomanAI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/characters/roman/idle.png", "char_ai_idle");
        contentManager.loadTexture("textures/characters/roman/walk.png", "char_ai_walk");
        contentManager.loadTexture("textures/characters/roman/attack.png", "char_ai_attack");
        contentManager.loadTexture("textures/characters/roman/block.png", "char_ai_block");
        contentManager.loadTexture("textures/characters/roman/block_reset.png", "char_ai_block_reset");
        contentManager.loadTexture("textures/characters/roman/death.png", "char_ai_death");

        contentManager.waitForLoad();

        animations.put(AIAnimation.IDLE, new ActionAnimation(contentManager.getTexture("char_ai_idle"), new Vector2(0, 0), 4, 0.20f, true));
        animations.put(AIAnimation.WALK, new ActionAnimation(contentManager.getTexture("char_ai_walk"), new Vector2(0, 0), 11, 0.10f, true));
        animations.put(AIAnimation.ATTACK, new ActionAnimation(contentManager.getTexture("char_ai_attack"), new Vector2(-14f, 0), 6, 0.05f, false));
        animations.put(AIAnimation.BLOCK, new ActionAnimation(contentManager.getTexture("char_ai_block"), new Vector2(0, 0), 3, 0.10f, false));
        animations.put(AIAnimation.BLOCK_RESET, new ActionAnimation(contentManager.getTexture("char_ai_block_reset"), new Vector2(0, 0), 3, 0.10f, false));
        animations.put(AIAnimation.DEATH, new ActionAnimation(contentManager.getTexture("char_ai_death"), new Vector2(0, 0), 12, 0.12f, false));

        stateTime = 0f;
        actorDirection = ActorDirection.RIGHT;
        setAnimation(AIAnimation.IDLE);

        Vector2 textureSize = new Vector2(45, 49);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (getPosition().x + 0.5f) * (textureSize.x) / Box2DConstants.PPM,
                (getPosition().y + 0.5f) * (textureSize.y) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x - 24) / 2 / Box2DConstants.PPM,
                (textureSize.y) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_AI;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_AI;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef).setUserData("ai_body");
        polygonShape.dispose();

        PolygonShape weaponShape = new PolygonShape();
        weaponShape.setAsBox(
                (textureSize.x) / 2 / Box2DConstants.PPM,
                (15f) / 2 / Box2DConstants.PPM,
                new Vector2(0, (10) / 2 / Box2DConstants.PPM), 0
        );

        FixtureDef weaponFixture = new FixtureDef();
        weaponFixture.shape = weaponShape;
        weaponFixture.filter.categoryBits = Box2DConstants.CATEGORY_WEAPON;
        weaponFixture.filter.maskBits = Box2DConstants.MASK_AI_WEAPON;
        weaponFixture.isSensor = true;
        physicsBody.createFixture(weaponFixture).setUserData("ai_weapon");

        physicsBody.setUserData(this);

        // Set up other stuff
        healthBar = new HealthBar(new Vector2(0, 0), new Vector2(30.0f, 4.0f));

        // Initialize properties
        setProperty("ACTION", CharacterAction.NONE);
        setProperty("HEALTH", 100.0f);
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
        activeAnimation.update(stateTime, getPosition());
        healthBar.update((Float)getProperty("HEALTH"), 100.0f);
        healthBar.setHealthPosition(new Vector2(getScaledPosition().x - 15, getScaledPosition().y + 30));

        if(isDead()) {
            setAnimation(AIAnimation.DEATH);
        }

        if(isDead() && activeAnimation.isAnimationFinished()) {
            isRemovable = true;
        }

        if(isDead()) {
            return;
        }

        // TODO: AI Logic
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        activeAnimation.render(spriteBatch, actorDirection);
        healthBar.render(spriteBatch);
    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("char_ai_idle");
        contentManager.disposeTexture("char_ai_walk");
        contentManager.disposeTexture("char_ai_attack");
        contentManager.disposeTexture("char_ai_block");
        contentManager.disposeTexture("char_ai_block_reset");
        contentManager.disposeTexture("char_ai_death");
        world.destroyBody(physicsBody);
    }
}
