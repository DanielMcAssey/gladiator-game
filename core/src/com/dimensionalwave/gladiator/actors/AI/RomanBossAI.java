package com.dimensionalwave.gladiator.actors.AI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.actors.ActorDirection;
import com.dimensionalwave.gladiator.enums.AIBossAnimation;
import com.dimensionalwave.gladiator.handlers.ActionAnimation;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.helpers.CharacterAction;
import com.dimensionalwave.gladiator.helpers.HealthBar;

import java.util.HashMap;

public class RomanBossAI extends AI {

    private HashMap<AIBossAnimation, ActionAnimation> animations = new HashMap<AIBossAnimation, ActionAnimation>();

    private AIBossAnimation activeAnimationType;
    private ActionAnimation activeAnimation;
    private ActorDirection actorDirection;
    private ActionAnimation lightningAttack;

    private HealthBar healthBar;

    private Boolean isMoving = false;
    private Boolean isAttacking = false;

    private float stateTime;
    private float lightningStateTime;

    private float attackTimeout = 0.0f;

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
        contentManager.loadTexture("textures/characters/caesar/weapon/lightning.png", "char_ai_boss_weapon_lightning");

        contentManager.waitForLoad();

        animations.put(AIBossAnimation.IDLE, new ActionAnimation(contentManager.getTexture("char_ai_boss_idle"), new Vector2(0, 0), 4, 0.20f, true));
        animations.put(AIBossAnimation.WALK, new ActionAnimation(contentManager.getTexture("char_ai_boss_walk"), new Vector2(0, 0), 5, 0.10f, true));
        animations.put(AIBossAnimation.ATTACK, new ActionAnimation(contentManager.getTexture("char_ai_boss_attack"), new Vector2(0, 0), 2, 0.10f, false));
        animations.put(AIBossAnimation.DEATH, new ActionAnimation(contentManager.getTexture("char_ai_boss_death"), new Vector2(0, 0), 15, 0.18f, false));
        lightningAttack = new ActionAnimation(contentManager.getTexture("char_ai_boss_weapon_lightning"), new Vector2(0, 0), 6, 0.28f, false);

        stateTime = 0f;
        lightningStateTime = 0f;
        actorDirection = ActorDirection.RIGHT;
        setAnimation(AIBossAnimation.IDLE);

        Vector2 textureSize = new Vector2(43, 76);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (getPosition().x + 0.5f) * (textureSize.x) / Box2DConstants.PPM,
                (getPosition().y + 0.5f) * (textureSize.y) / Box2DConstants.PPM
        );

        physicsBody = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x) / 2 / Box2DConstants.PPM,
                (textureSize.y) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_AI_BOSS;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_AI_BOSS;
        physicsBody.createFixture(fixtureDef).setUserData("ai_body");

        // WEAPON
        PolygonShape weaponShape = new PolygonShape();
        FixtureDef weaponFixture = new FixtureDef();
        weaponShape.setAsBox(
                (textureSize.x + 1600f) / 2 / Box2DConstants.PPM,
                (textureSize.y) / 2 / Box2DConstants.PPM,
                new Vector2(0, -5 / 2 / Box2DConstants.PPM), 0
        );

        weaponFixture.shape = weaponShape;
        weaponFixture.filter.categoryBits = Box2DConstants.CATEGORY_WEAPON;
        weaponFixture.filter.maskBits = Box2DConstants.MASK_AI_WEAPON;
        weaponFixture.isSensor = true;
        physicsBody.createFixture(weaponFixture).setUserData("ai_weapon");

        weaponShape.dispose();
        polygonShape.dispose();

        physicsBody.setUserData(this);

        // Set up other stuff
        healthBar = new HealthBar(new Vector2(0, 0), new Vector2(100.0f, 6.0f));

        // Initialize properties
        setProperty("ACTION", CharacterAction.NONE);
        setProperty("HEALTH", 250.0f);
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
        lightningStateTime += dT;
        activeAnimation.update(stateTime, getPosition());

        float lightningOffsetX =  -(40 / Box2DConstants.PPM);
        float lightningOffsetY =  -(14 / Box2DConstants.PPM);

        if(actorDirection.equals(ActorDirection.RIGHT)) {
            lightningOffsetX = -lightningOffsetX;
        }

        healthBar.update((Float)getProperty("HEALTH"), 250.0f);
        healthBar.setHealthPosition(new Vector2(getScaledPosition().x - 43, getScaledPosition().y + 76));

        if(isDead()) {
            setAnimation(AIBossAnimation.DEATH);
        }

        if(isDead() && activeAnimation.isAnimationFinished()) {
            isRemovable = true;
        }

        if(isHit) {
            hitAmount += dT * 10;
        }

        if(hitAmount >= 1.0f) {
            hitAmount = 0.0f;
            isHit = false;
        }

        if(isDead()) {
            return;
        }

        // CAN SEE YOU
        if(target == null) {
            return;
        }

        if(target.getPosition().x <= getPosition().x) {
            actorDirection = ActorDirection.LEFT;
        } else {
            actorDirection = ActorDirection.RIGHT;
        }

        float distance = Math.abs(target.getPosition().x - getPosition().x);
        if(distance > (260 / Box2DConstants.PPM)) {
            setAnimation(AIBossAnimation.WALK);
            isAttacking = false;
            isMoving = true;
            physicsBody.setLinearVelocity(Math.signum(target.getPosition().x - getPosition().x) * 0.2f, 0);
        } else if(!isAttacking && isMoving) {
            physicsBody.setLinearVelocity(0, 0);
            isMoving = false;
            setAnimation(AIBossAnimation.IDLE);
        }

        // FIGHTING YOU
        if(inContact == null) {
            return;
        }

        attackTimeout += dT;

        if(attackTimeout >= 4.15f) {
            setAnimation(AIBossAnimation.ATTACK);
            setProperty("ACTION", CharacterAction.ATTACK);

            if(!isAttacking) {
                lightningStateTime = 0f;
            }

            isAttacking = true;

            if(lightningAttack.isAnimationFinished() && attackTimeout >= 6.0f) {
                attackTimeout = 0.0f;

                target.doDamage(10.0f, CharacterAction.JUMP);
            }
        } else {
            if(!isMoving && isAttacking && activeAnimation.isAnimationFinished()) {
                setAnimation(AIBossAnimation.IDLE);
                setProperty("ACTION", CharacterAction.NONE);
                isAttacking = false;
            }
        }

        lightningAttack.update(stateTime, new Vector2(getPosition().x + lightningOffsetX, getPosition().y + lightningOffsetY));
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

        if(isHit) {
            spriteBatch.setColor(1.0f, hitAmount, 0.0f, 1.0f);
        } else {
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        activeAnimation.render(spriteBatch, actorDirection);

        if(isAttacking && !isDead()) {
            lightningAttack.render(spriteBatch, actorDirection);
        }

        if(isHit) {
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

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
