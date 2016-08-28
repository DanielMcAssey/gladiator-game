package com.dimensionalwave.soda.actors.AI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.soda.Box2DConstants;
import com.dimensionalwave.soda.Constants;
import com.dimensionalwave.soda.handlers.ContentManager;

public class RomanAI extends AI {

    private static final int IDLE_FRAMES = 1;
    private static final float IDLE_ANIM_SPEED = 0.20f;
    private static final boolean IDLE_ANIM_LOOP = true;

    private Animation idleAnimation;
    private Texture idleTexture;

    private Vector2 textureSize = new Vector2(0, 0);

    private float stateTime;

    public RomanAI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/characters/roman/idle.png", "char_ai_idle");

        contentManager.waitForLoad();
        idleTexture = contentManager.getTexture("char_ai_idle");
        textureSize = new Vector2(idleTexture.getWidth() / IDLE_FRAMES, idleTexture.getHeight());

        TextureRegion[] idleFrames = TextureRegion.split(idleTexture, (int)textureSize.x, (int)textureSize.y)[0];
        idleAnimation = new Animation(IDLE_ANIM_SPEED, idleFrames);

        stateTime = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (currentPosition.x + 0.5f) * (textureSize.x * Constants.MAP_SCALE) / Box2DConstants.PPM,
                (currentPosition.y + 0.5f) * (textureSize.y * Constants.MAP_SCALE) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x - 3 * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                (textureSize.y * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_AI;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_AI;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef);

        physicsBody.setUserData(this);

        // Initialize properties
        setProperty("ACTION", "NONE");
    }

    @Override
    public void update(float dT) {
        stateTime += dT;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(idleAnimation.getKeyFrame(stateTime, IDLE_ANIM_LOOP),
                (physicsBody.getPosition().x * Box2DConstants.PPM) - (textureSize.x / 2),
                (physicsBody.getPosition().y * Box2DConstants.PPM) - (textureSize.y / 2) - 2,
                textureSize.x / 2.0f,
                textureSize.y / 2.0f,
                textureSize.x,
                textureSize.y,
                Constants.MAP_SCALE,
                Constants.MAP_SCALE,
                0.0f);
    }

    @Override
    public void dispose() {
        contentManager.disposeTexture("char_ai_idle");
    }
}
