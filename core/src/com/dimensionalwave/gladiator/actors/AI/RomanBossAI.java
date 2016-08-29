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

enum AIBossAnimation {
    IDLE,
    WALK,
    ATTACK,
    DEATH
}

public class RomanBossAI extends AI {

    private HashMap<AIBossAnimation, ActionAnimation> animations = new HashMap<AIBossAnimation, ActionAnimation>();

    private AIBossAnimation activeAnimationType;
    private ActionAnimation activeAnimation;
    private ActorDirection actorDirection;

    HealthBar healthBar;

    private float stateTime;

    private void setAnimation(AIBossAnimation newAnimation) {
        if(activeAnimationType != null && activeAnimationType.equals(newAnimation) || !animations.containsKey(newAnimation)) {
            return;
        }

        stateTime = 0f;
        activeAnimationType = newAnimation;
        activeAnimation = animations.get(newAnimation);
        activeAnimation.update(stateTime, getPosition());
    }

    public RomanBossAI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/characters/caesar/idle.png", "char_ai_boss_idle");
        contentManager.loadTexture("textures/characters/caesar/walk.png", "char_ai_boss_walk");
        contentManager.loadTexture("textures/characters/caesar/attack.png", "char_ai_boss_attack");
        contentManager.loadTexture("textures/characters/caesar/death.png", "char_ai_boss_death");

        contentManager.waitForLoad();

        animations.put(AIBossAnimation.IDLE, new ActionAnimation(contentManager.getTexture("char_ai_boss_idle"), new Vector2(0, 0), 4, 0.20f, true));
        animations.put(AIBossAnimation.WALK, new ActionAnimation(contentManager.getTexture("char_ai_boss_walk"), new Vector2(0, 0), 5, 0.10f, true));
        animations.put(AIBossAnimation.ATTACK, new ActionAnimation(contentManager.getTexture("char_ai_boss_attack"), new Vector2(0, 0), 2, 0.10f, false));
        animations.put(AIBossAnimation.DEATH, new ActionAnimation(contentManager.getTexture("char_ai_boss_death"), new Vector2(0, 0), 15, 0.18f, false));

        stateTime = 0f;
        actorDirection = ActorDirection.RIGHT;
        setAnimation(AIBossAnimation.IDLE);

        Vector2 textureSize = new Vector2(43, 76);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
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
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_AI_BOSS;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_AI_BOSS;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef);

        physicsBody.setUserData(this);

        // Set up other stuff
        healthBar = new HealthBar(new Vector2(0, 0), new Vector2(100.0f, 6.0f));

        // Initialize properties
        setProperty("ACTION", CharacterAction.NONE);
        setProperty("HEALTH", 1000.0f);
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
        activeAnimation.update(stateTime, getPosition());
        healthBar.update((Float)getProperty("HEALTH"), 1000.0f);
        healthBar.setHealthPosition(new Vector2(getScaledPosition().x - 43, getScaledPosition().y + 76));

        if(isDead()) {
            setAnimation(AIBossAnimation.DEATH);
        }

        if(isDead() && activeAnimation.isAnimationFinished()) {
            isRemovable = true;
        }

        if(isDead()) {
            return;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        activeAnimation.render(spriteBatch, actorDirection);
        healthBar.render(spriteBatch);
    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("char_ai_boss_idle");
        contentManager.disposeTexture("char_ai_boss_walk");
        contentManager.disposeTexture("char_ai_boss_attack");
        contentManager.disposeTexture("char_ai_boss_death");
        world.destroyBody(physicsBody);
    }
}
