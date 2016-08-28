package com.dimensionalwave.soda.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.dimensionalwave.soda.Box2DConstants;
import com.dimensionalwave.soda.Constants;
import com.dimensionalwave.soda.handlers.ContentManager;
import com.dimensionalwave.soda.input.InputAction;
import com.dimensionalwave.soda.input.InputManager;

enum PlayerAnimation {
    IDLE,
    WALK,
    ATTACK,
    BLOCK,
    BLOCK_RESET,
    JUMP_START,
    JUMP_END,
    DEATH
}

public class Player extends Actor {

    private static final int IDLE_FRAMES = 4;
    private static final float IDLE_ANIM_SPEED = 0.20f;
    private static final boolean IDLE_ANIM_LOOP = true;

    private static final int WALK_FRAMES = 11;
    private static final float WALK_ANIM_SPEED = 0.10f;
    private static final boolean WALK_ANIM_LOOP = true;

    private static final int ATTACK_FRAMES = 6;
    private static final float ATTACK_ANIM_SPEED = 0.05f;
    private static final boolean ATTACK_ANIM_LOOP = false;

    private static final int BLOCK_FRAMES = 3;
    private static final float BLOCK_ANIM_SPEED = 0.10f;
    private static final boolean BLOCK_ANIM_LOOP = false;

    private static final int BLOCK_RESET_FRAMES = 3;
    private static final float BLOCK_RESET_ANIM_SPEED = 0.10f;
    private static final boolean BLOCK_RESET_ANIM_LOOP = false;

    private static final int JUMP_START_FRAMES = 4;
    private static final float JUMP_START_ANIM_SPEED = 0.10f;
    private static final boolean JUMP_START_ANIM_LOOP = false;

    private static final int JUMP_END_FRAMES = 2;
    private static final float JUMP_END_ANIM_SPEED = 0.10f;
    private static final boolean JUMP_END_ANIM_LOOP = false;

    private static final int DEATH_FRAMES = 9;
    private static final float DEATH_ANIM_SPEED = 0.18f;
    private static final boolean DEATH_ANIM_LOOP = false;

    private PlayerAnimation activeAnimationType;
    private Animation activeAnimation;
    private Boolean activeAnimationLoop;

    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation attackAnimation;
    private Animation blockAnimation;
    private Animation blockResetAnimation;
    private Animation jumpStartAnimation;
    private Animation jumpEndAnimation;
    private Animation deathAnimation;

    private Texture idleTexture;
    private Texture walkTexture;
    private Texture attackTexture;
    private Texture blockTexture;
    private Texture blockResetTexture;
    private Texture jumpStartTexture;
    private Texture jumpEndTexture;
    private Texture deathTexture;

    private Vector2 textureSize = new Vector2(0, 0);
    private Vector2 textureOrigin = new Vector2(0, 0);

    private ActorDirection actorDirection;
    private ActorDirection previousActorDirection;
    private Boolean isMoving = false;
    private Boolean isJumping = false;
    private Boolean isAttacking = false;
    private Boolean isBlocking = false;

    private Body weaponBody;

    private float stateTime;

    private float playerHealth = 100.0f;

    public float getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(float playerHealth) {
        this.playerHealth = playerHealth;
    }

    public boolean isDead() {
        return (playerHealth <= 0.0f);
    }

    private void setAnimation(PlayerAnimation newAnimation) {
        if(activeAnimationType != null && activeAnimationType.equals(newAnimation)) {
            return;
        }

        stateTime = 0f;
        activeAnimationType = newAnimation;
        switch (newAnimation) {
            case IDLE:
                textureSize = new Vector2(idleTexture.getWidth() / IDLE_FRAMES, idleTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = idleAnimation;
                activeAnimationLoop = IDLE_ANIM_LOOP;
                break;
            case WALK:
                textureSize = new Vector2(walkTexture.getWidth() / WALK_FRAMES, walkTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = walkAnimation;
                activeAnimationLoop = WALK_ANIM_LOOP;
                break;
            case ATTACK:
                textureSize = new Vector2(attackTexture.getWidth() / ATTACK_FRAMES, attackTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f - 14, textureSize.y / 2f);
                activeAnimation = attackAnimation;
                activeAnimationLoop = ATTACK_ANIM_LOOP;
                break;
            case BLOCK:
                textureSize = new Vector2(blockTexture.getWidth() / BLOCK_FRAMES, blockTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = blockAnimation;
                activeAnimationLoop = BLOCK_ANIM_LOOP;
                break;
            case BLOCK_RESET:
                textureSize = new Vector2(blockResetTexture.getWidth() / BLOCK_RESET_FRAMES, blockResetTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = blockResetAnimation;
                activeAnimationLoop = BLOCK_RESET_ANIM_LOOP;
                break;
            case JUMP_START:
                textureSize = new Vector2(jumpStartTexture.getWidth() / JUMP_START_FRAMES, jumpStartTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = jumpStartAnimation;
                activeAnimationLoop = JUMP_START_ANIM_LOOP;
                break;
            case JUMP_END:
                textureSize = new Vector2(jumpEndTexture.getWidth() / JUMP_END_FRAMES, jumpEndTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = jumpEndAnimation;
                activeAnimationLoop = JUMP_END_ANIM_LOOP;
                break;
            case DEATH:
                textureSize = new Vector2(deathTexture.getWidth() / DEATH_FRAMES, deathTexture.getHeight());
                textureOrigin = new Vector2(textureSize.x / 2f, textureSize.y / 2f);
                activeAnimation = deathAnimation;
                activeAnimationLoop = DEATH_ANIM_LOOP;
                break;
        }
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
        idleTexture = contentManager.getTexture("char_player_idle");
        walkTexture = contentManager.getTexture("char_player_walk");
        attackTexture = contentManager.getTexture("char_player_attack");
        blockTexture = contentManager.getTexture("char_player_block");
        blockResetTexture = contentManager.getTexture("char_player_block_reset");
        jumpStartTexture = contentManager.getTexture("char_player_jump_start");
        jumpEndTexture = contentManager.getTexture("char_player_jump_end");
        deathTexture = contentManager.getTexture("char_player_death");

        previousActorDirection = ActorDirection.RIGHT;
        actorDirection = ActorDirection.RIGHT;

        setAnimation(PlayerAnimation.IDLE);
        TextureRegion[] idleFrames = TextureRegion.split(idleTexture, (int)textureSize.x, (int)textureSize.y)[0];
        idleAnimation = new Animation(IDLE_ANIM_SPEED, idleFrames);

        setAnimation(PlayerAnimation.WALK);
        TextureRegion[] walkFrames = TextureRegion.split(walkTexture, (int)textureSize.x, (int)textureSize.y)[0];
        walkAnimation = new Animation(WALK_ANIM_SPEED, walkFrames);

        setAnimation(PlayerAnimation.ATTACK);
        TextureRegion[] attackFrames = TextureRegion.split(attackTexture, (int)textureSize.x, (int)textureSize.y)[0];
        attackAnimation = new Animation(ATTACK_ANIM_SPEED, attackFrames);

        setAnimation(PlayerAnimation.BLOCK);
        TextureRegion[] blockFrames = TextureRegion.split(blockTexture, (int)textureSize.x, (int)textureSize.y)[0];
        blockAnimation = new Animation(BLOCK_ANIM_SPEED, blockFrames);

        setAnimation(PlayerAnimation.BLOCK_RESET);
        TextureRegion[] blockResetFrames = TextureRegion.split(blockResetTexture, (int)textureSize.x, (int)textureSize.y)[0];
        blockResetAnimation = new Animation(BLOCK_RESET_ANIM_SPEED, blockResetFrames);

        setAnimation(PlayerAnimation.JUMP_START);
        TextureRegion[] jumpStartFrames = TextureRegion.split(jumpStartTexture, (int)textureSize.x, (int)textureSize.y)[0];
        jumpStartAnimation = new Animation(JUMP_START_ANIM_SPEED, jumpStartFrames);

        setAnimation(PlayerAnimation.JUMP_END);
        TextureRegion[] jumpEndFrames = TextureRegion.split(jumpEndTexture, (int)textureSize.x, (int)textureSize.y)[0];
        jumpEndAnimation = new Animation(JUMP_END_ANIM_SPEED, jumpEndFrames);

        setAnimation(PlayerAnimation.DEATH);
        TextureRegion[] deathFrames = TextureRegion.split(deathTexture, (int)textureSize.x, (int)textureSize.y)[0];
        deathAnimation = new Animation(DEATH_ANIM_SPEED, deathFrames);

        setAnimation(PlayerAnimation.IDLE);

        stateTime = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (currentPosition.x + 0.5f) * (textureSize.x * Constants.MAP_SCALE) / Box2DConstants.PPM,
                (currentPosition.y + 0.5f) * (textureSize.y * Constants.MAP_SCALE) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x - 5 * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                (textureSize.y * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_PLAYER;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef);
        polygonShape.dispose();

        PolygonShape footSensorShape = new PolygonShape();
        footSensorShape.setAsBox((textureSize.x - 5 * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                2 / Box2DConstants.PPM,
                new Vector2(0, -(textureSize.y * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM), 0);

        FixtureDef footSensor = new FixtureDef();
        footSensor.shape = footSensorShape;
        footSensor.filter.categoryBits = Box2DConstants.CATEGORY_PLAYER;
        footSensor.filter.maskBits = Box2DConstants.MASK_PLAYER;
        footSensor.isSensor = true;

        physicsBody.createFixture(footSensor).setUserData("player_foot");
        footSensorShape.dispose();

        PolygonShape weaponShape = new PolygonShape();
        weaponShape.setAsBox(
                (10f * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                (4f * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM
        );

        BodyDef weaponBodyDef = new BodyDef();
        weaponBodyDef.type = BodyDef.BodyType.DynamicBody;
        weaponBodyDef.position.set(
                (currentPosition.x + 0.5f) * (textureSize.x * Constants.MAP_SCALE) / Box2DConstants.PPM,
                (currentPosition.y + 0.5f) * (textureSize.y * Constants.MAP_SCALE) / Box2DConstants.PPM
        );

        FixtureDef weaponFixture = new FixtureDef();
        weaponFixture.shape = weaponShape;
        weaponFixture.filter.categoryBits = Box2DConstants.CATEGORY_WEAPON;
        weaponFixture.filter.maskBits = Box2DConstants.MASK_PLAYER_WEAPON;
        weaponFixture.isSensor = true;

        weaponBody = world.createBody(weaponBodyDef);
        weaponBody.createFixture(weaponFixture).setUserData("player_weapon");

        weaponShape.dispose();

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.type = JointDef.JointType.RevoluteJoint;
        jointDef.bodyA = physicsBody;
        jointDef.bodyB = weaponBody;
        jointDef.collideConnected = false;
        jointDef.localAnchorA.set(0.5f / Box2DConstants.PPM, 0.5f / Box2DConstants.PPM);
        jointDef.localAnchorB.set(10f / Box2DConstants.PPM, 10f / Box2DConstants.PPM);

        jointDef.enableMotor = true;
        jointDef.maxMotorTorque = 5;
        jointDef.motorSpeed = (float)((Math.PI / 180) * 90);

        //Joint test = world.createJoint(jointDef);

        physicsBody.setUserData(this);

        // Initialize properties
        setProperty("CAN_JUMP", true);
        setProperty("ACTION", "NONE");
    }

    @Override
    public void update(float dT) {
        stateTime += dT;

        if(InputManager.isPressed(InputAction.ACTION_JUMP) && (Boolean)getProperty("CAN_JUMP")) {
            isJumping = true;
            setAnimation(PlayerAnimation.JUMP_START);
            physicsBody.applyForceToCenter(0, 120 * Constants.MAP_SCALE, true);
        }

        if(InputManager.isDown(InputAction.MOVE_RIGHT) && !isAttacking && !isBlocking) {
            isMoving = true;
            previousActorDirection = actorDirection;
            actorDirection = ActorDirection.RIGHT;
            if(!isJumping && !activeAnimationType.equals(PlayerAnimation.JUMP_END)) {
                setAnimation(PlayerAnimation.WALK);
            }
            if(!isBlocking) {
                physicsBody.setLinearVelocity(0.6f * Constants.MAP_SCALE, physicsBody.getLinearVelocity().y);
            }
        } else if(InputManager.isDown(InputAction.MOVE_LEFT) && !isAttacking && !isBlocking) {
            isMoving = true;
            previousActorDirection = actorDirection;
            actorDirection = ActorDirection.LEFT;
            if(!isJumping && !activeAnimationType.equals(PlayerAnimation.JUMP_END)) {
                setAnimation(PlayerAnimation.WALK);
            }
            if(!isBlocking) {
                physicsBody.setLinearVelocity(-0.6f * Constants.MAP_SCALE, physicsBody.getLinearVelocity().y);
            }
        } else if(InputManager.isPressed(InputAction.ACTION_ATTACK) && !isAttacking && !isBlocking && !isMoving && !isJumping) {
            setProperty("ACTION", "ATTACK");
            isAttacking = true;
            setAnimation(PlayerAnimation.ATTACK);
        }else if(InputManager.isDown(InputAction.ACTION_BLOCK) && !isBlocking && !isAttacking && !isMoving && !isJumping) {
            setProperty("ACTION", "BLOCK");
            isBlocking = true;
            setAnimation(PlayerAnimation.BLOCK);
        }

        if(!InputManager.isDown(InputAction.MOVE_RIGHT) && !InputManager.isDown(InputAction.MOVE_LEFT) && isMoving && !isJumping) {
            isMoving = false;
            setProperty("ACTION", "NONE");
            setAnimation(PlayerAnimation.IDLE);
            physicsBody.setLinearVelocity(0, physicsBody.getLinearVelocity().y);
        }

        if(!InputManager.isDown(InputAction.ACTION_BLOCK) && isBlocking && !isAttacking && !isMoving && !activeAnimationType.equals(PlayerAnimation.BLOCK_RESET) && activeAnimation.isAnimationFinished(stateTime)) {
            setProperty("ACTION", "NONE");
            setAnimation(PlayerAnimation.BLOCK_RESET);
        }

        if(isJumping && (Boolean)getProperty("CAN_JUMP") && activeAnimationType.equals(PlayerAnimation.JUMP_START) && activeAnimation.isAnimationFinished(stateTime)) {
            isJumping = false;
            setAnimation(PlayerAnimation.JUMP_END);
        }

        if (!isJumping && activeAnimationType.equals(PlayerAnimation.JUMP_END) && activeAnimation.isAnimationFinished(stateTime)) {
            setAnimation(PlayerAnimation.IDLE);
        }

        if(isAttacking && activeAnimationType.equals(PlayerAnimation.ATTACK) && activeAnimation.isAnimationFinished(stateTime)) {
            isAttacking = false;
        }

        if(isBlocking && activeAnimationType.equals(PlayerAnimation.BLOCK_RESET) && activeAnimation.isAnimationFinished(stateTime)) {
            isBlocking = false;
        }

        if(!isMoving && !isBlocking && !isJumping && activeAnimation.isAnimationFinished(stateTime)) {
            setAnimation(PlayerAnimation.IDLE);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = activeAnimation.getKeyFrame(stateTime, activeAnimationLoop);

        if(!currentFrame.isFlipX() && actorDirection.equals(ActorDirection.LEFT)) {
            currentFrame.flip(true, false);
        } else if(currentFrame.isFlipX() && actorDirection.equals(ActorDirection.RIGHT)) {
            currentFrame.flip(true, false);
        }

        spriteBatch.draw(currentFrame,
                (physicsBody.getPosition().x * Box2DConstants.PPM) - (textureSize.x / 2),
                (physicsBody.getPosition().y * Box2DConstants.PPM) - (textureSize.y / 2) - 2,
                textureOrigin.x,
                textureOrigin.y,
                textureSize.x,
                textureSize.y,
                Constants.MAP_SCALE,
                Constants.MAP_SCALE,
                0.0f);

    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("char_player_idle");
        contentManager.disposeTexture("char_player_block");
        contentManager.disposeTexture("char_player_block_reset");
    }
}
