package com.dimensionalwave.gladiator.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.actors.AI.AI;
import com.dimensionalwave.gladiator.enums.PlayerAnimation;
import com.dimensionalwave.gladiator.handlers.ActionAnimation;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.helpers.CharacterAction;
import com.dimensionalwave.gladiator.input.InputAction;
import com.dimensionalwave.gladiator.input.InputManager;

import java.util.HashMap;

public class Player extends Actor {

    private HashMap<PlayerAnimation, ActionAnimation> animations = new HashMap<PlayerAnimation, ActionAnimation>();

    private Array<AI> inContact = new Array<AI>();

    private PlayerAnimation activeAnimationType;
    private ActionAnimation activeAnimation;
    private ActorDirection actorDirection;

    private boolean isHit = false;
    private float hitAmount = 0.0f;

    private Boolean isRemovable = false;
    private Boolean isMoving = false;
    private Boolean isJumping = false;
    private Boolean isAttacking = false;
    private Boolean isBlocking = false;

    private float stateTime;

    public void addContact(AI ai) {
        if(!inContact.contains(ai, true)) {
            inContact.add(ai);
        }
    }

    public void removeContact(AI ai) {
        if(inContact.contains(ai, true)) {
            inContact.removeValue(ai, true);
        }
    }

    public void doDamage(float damageAmount, CharacterAction dodgeAction) {
        if(!((CharacterAction)getProperty("ACTION")).equals(dodgeAction) && !isDead()) {
            float trueDamageAmount = damageAmount / (getArmorStrength() / 100.0f);

            if(getPlayerHealth() - trueDamageAmount <= 0f) {
                setProperty("HEALTH", 0f);
            } else {
                setProperty("HEALTH", getPlayerHealth() - trueDamageAmount);
            }

            isHit = true;
        }
    }

    public Boolean isRemovable() {
        return isRemovable;
    }

    public float getPlayerHealth() {
        return (Float) getProperty("HEALTH");
    }

    public float getArmorStrength() {
        return (Float) getProperty("ARMOR_STRENGTH");
    }

    public float getWeaponStrength() {
        return (Float) getProperty("WEAPON_STRENGTH");
    }

    public int getKills() {
        return (Integer) getProperty("KILLS");
    }

    public boolean isDead() {
        return (getPlayerHealth() <= 0.0f);
    }

    private void setAnimation(PlayerAnimation newAnimation) {
        if(activeAnimationType != null && activeAnimationType.equals(newAnimation) || !animations.containsKey(newAnimation)) {
            return;
        }

        stateTime = 0f;
        activeAnimationType = newAnimation;
        activeAnimation = animations.get(newAnimation);
        activeAnimation.update(stateTime, getPosition());
    }

    public Player(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/characters/gladius/idle.png", "char_player_idle");
        contentManager.loadTexture("textures/characters/gladius/walk.png", "char_player_walk");
        contentManager.loadTexture("textures/characters/gladius/attack.png", "char_player_attack");
        contentManager.loadTexture("textures/characters/gladius/block.png", "char_player_block");
        contentManager.loadTexture("textures/characters/gladius/block_reset.png", "char_player_block_reset");
        contentManager.loadTexture("textures/characters/gladius/jump_start.png", "char_player_jump_start");
        contentManager.loadTexture("textures/characters/gladius/jump_end.png", "char_player_jump_end");
        contentManager.loadTexture("textures/characters/gladius/death.png", "char_player_death");

        contentManager.waitForLoad();

        animations.put(PlayerAnimation.IDLE, new ActionAnimation(contentManager.getTexture("char_player_idle"), new Vector2(0, 0), 4, 0.20f, true));
        animations.put(PlayerAnimation.WALK, new ActionAnimation(contentManager.getTexture("char_player_walk"), new Vector2(0, 0), 11, 0.10f, true));
        animations.put(PlayerAnimation.ATTACK, new ActionAnimation(contentManager.getTexture("char_player_attack"), new Vector2(-25f, 0), 6, 0.05f, false));
        animations.put(PlayerAnimation.BLOCK, new ActionAnimation(contentManager.getTexture("char_player_block"), new Vector2(0, 0), 3, 0.10f, false));
        animations.put(PlayerAnimation.BLOCK_RESET, new ActionAnimation(contentManager.getTexture("char_player_block_reset"), new Vector2(0, 0), 3, 0.10f, false));
        animations.put(PlayerAnimation.JUMP_START, new ActionAnimation(contentManager.getTexture("char_player_jump_start"), new Vector2(0, 0), 4, 0.10f, false));
        animations.put(PlayerAnimation.JUMP_END, new ActionAnimation(contentManager.getTexture("char_player_jump_end"), new Vector2(0, 0), 2, 0.10f, false));
        animations.put(PlayerAnimation.DEATH, new ActionAnimation(contentManager.getTexture("char_player_death"), new Vector2(0, 0), 9, 0.18f, false));

        actorDirection = ActorDirection.RIGHT;
        setAnimation(PlayerAnimation.IDLE);
        stateTime = 0f;

        Vector2 textureSize = new Vector2(45, 49);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (getPosition().x + 0.5f) * (textureSize.x) / Box2DConstants.PPM,
                (getPosition().y + 0.5f) * (textureSize.y) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x - 30) / 2 / Box2DConstants.PPM,
                (textureSize.y) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_PLAYER;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef).setUserData("player_body");
        polygonShape.dispose();

        PolygonShape footSensorShape = new PolygonShape();
        footSensorShape.setAsBox(
                (textureSize.x - 30) / 2 / Box2DConstants.PPM,
                2 / Box2DConstants.PPM,
                new Vector2(0, -(textureSize.y) / 2 / Box2DConstants.PPM), 0);

        FixtureDef footSensor = new FixtureDef();
        footSensor.shape = footSensorShape;
        footSensor.filter.categoryBits = Box2DConstants.CATEGORY_PLAYER;
        footSensor.filter.maskBits = Box2DConstants.MASK_PLAYER;
        footSensor.isSensor = true;

        physicsBody.createFixture(footSensor).setUserData("player_foot");
        footSensorShape.dispose();

        PolygonShape weaponShape = new PolygonShape();
        weaponShape.setAsBox(
                (textureSize.x) / 2 / Box2DConstants.PPM,
                (15f) / 2 / Box2DConstants.PPM,
                new Vector2(0, (10) / 2 / Box2DConstants.PPM), 0
        );

        FixtureDef weaponFixture = new FixtureDef();
        weaponFixture.shape = weaponShape;
        weaponFixture.filter.categoryBits = Box2DConstants.CATEGORY_WEAPON;
        weaponFixture.filter.maskBits = Box2DConstants.MASK_PLAYER_WEAPON;
        weaponFixture.isSensor = true;
        physicsBody.createFixture(weaponFixture).setUserData("player_weapon");

        weaponShape.dispose();

        physicsBody.setUserData(this);

        // Initialize properties
        setProperty("CAN_JUMP", true);
        setProperty("ACTION", CharacterAction.NONE);
        setProperty("HEALTH", 100.0f);
        setProperty("ARMOR_STRENGTH", 100.0f);
        setProperty("WEAPON_STRENGTH", 100.0f);
        setProperty("KILLS", 0);
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
        activeAnimation.update(stateTime, getPosition());

        if(isDead()) {
            setAnimation(PlayerAnimation.DEATH);
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

        if(InputManager.isPressed(InputAction.ACTION_JUMP) && (Boolean)getProperty("CAN_JUMP")) {
            isJumping = true;
            setAnimation(PlayerAnimation.JUMP_START);
            setProperty("ACTION", CharacterAction.JUMP);
            physicsBody.applyForceToCenter(0, 140, true);
        }

        if(InputManager.isDown(InputAction.MOVE_RIGHT) && !isAttacking && !isBlocking) {
            isMoving = true;
            actorDirection = ActorDirection.RIGHT;
            if(!isJumping && !activeAnimationType.equals(PlayerAnimation.JUMP_END)) {
                setAnimation(PlayerAnimation.WALK);
            }
            if(!isBlocking) {
                physicsBody.setLinearVelocity(1.1f, physicsBody.getLinearVelocity().y);
            }
        } else if(InputManager.isDown(InputAction.MOVE_LEFT) && !isAttacking && !isBlocking) {
            isMoving = true;
            actorDirection = ActorDirection.LEFT;
            if(!isJumping && !activeAnimationType.equals(PlayerAnimation.JUMP_END)) {
                setAnimation(PlayerAnimation.WALK);
            }
            if(!isBlocking) {
                physicsBody.setLinearVelocity(-1.1f, physicsBody.getLinearVelocity().y);
            }
        } else if(InputManager.isPressed(InputAction.ACTION_ATTACK) && !isAttacking && !isBlocking && !isMoving && !isJumping) {
            setProperty("ACTION", CharacterAction.ATTACK);
            isAttacking = true;
            setAnimation(PlayerAnimation.ATTACK);
        }else if(InputManager.isDown(InputAction.ACTION_BLOCK) && !isBlocking && !isAttacking && !isMoving && !isJumping) {
            setProperty("ACTION", CharacterAction.BLOCK);
            isBlocking = true;
            setAnimation(PlayerAnimation.BLOCK);
        }

        if(!InputManager.isDown(InputAction.MOVE_RIGHT) && !InputManager.isDown(InputAction.MOVE_LEFT) && isMoving && !isJumping) {
            isMoving = false;
            setProperty("ACTION", CharacterAction.NONE);
            setAnimation(PlayerAnimation.IDLE);
            physicsBody.setLinearVelocity(0, physicsBody.getLinearVelocity().y);
        }

        if(!InputManager.isDown(InputAction.ACTION_BLOCK) && isBlocking && !isAttacking && !isMoving && !activeAnimationType.equals(PlayerAnimation.BLOCK_RESET) && activeAnimation.isAnimationFinished()) {
            setProperty("ACTION", CharacterAction.NONE);
            setAnimation(PlayerAnimation.BLOCK_RESET);
        }

        if(isJumping && (Boolean)getProperty("CAN_JUMP") && activeAnimationType.equals(PlayerAnimation.JUMP_START) && activeAnimation.isAnimationFinished()) {
            isJumping = false;
            setAnimation(PlayerAnimation.JUMP_END);
            setProperty("ACTION", CharacterAction.NONE);
        }

        if (!isJumping && activeAnimationType.equals(PlayerAnimation.JUMP_END) && activeAnimation.isAnimationFinished()) {
            setAnimation(PlayerAnimation.IDLE);
        }

        if(isAttacking && activeAnimationType.equals(PlayerAnimation.ATTACK) && activeAnimation.isAnimationFinished()) {
            isAttacking = false;
        }

        if(isBlocking && activeAnimationType.equals(PlayerAnimation.BLOCK_RESET) && activeAnimation.isAnimationFinished()) {
            isBlocking = false;
        }

        if(!isMoving && !isBlocking && !isJumping && activeAnimation.isAnimationFinished()) {
            setAnimation(PlayerAnimation.IDLE);
        }

        if(((CharacterAction)getProperty("ACTION")).equals(CharacterAction.ATTACK)) {
            for(AI ai : inContact) {
                ai.doDamage(10.0f * (getWeaponStrength() / 100.0f));
                if(ai.isDead() && !ai.isKillTaken()) {
                    setProperty("KILLS", getKills() + 1);
                }
            }
            setProperty("ACTION", CharacterAction.NONE);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if(isHit) {
            spriteBatch.setColor(1.0f, hitAmount, 0.0f, 1.0f);
        } else {
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        activeAnimation.render(spriteBatch, actorDirection);

        if(isHit) {
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("char_player_idle");
        contentManager.disposeTexture("char_player_walk");
        contentManager.disposeTexture("char_player_attack");
        contentManager.disposeTexture("char_player_block");
        contentManager.disposeTexture("char_player_block_reset");
        contentManager.disposeTexture("char_player_jump_start");
        contentManager.disposeTexture("char_player_jump_end");
        contentManager.disposeTexture("char_player_death");
        world.destroyBody(physicsBody);
    }
}
