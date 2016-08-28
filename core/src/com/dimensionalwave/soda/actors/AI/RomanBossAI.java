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

public class RomanBossAI extends AI {

    private static final int WALK_FRAMES = 5;
    private static final float WALK_ANIM_SPEED = 0.15f;
    private static final boolean WALK_ANIM_LOOP = true;

    private Animation walkAnimation;
    private Texture walkTexture;

    private Vector2 textureSize = new Vector2(0, 0);

    private float stateTime;

    public RomanBossAI(ContentManager contentManager, World newWorld, Vector2 startPosition, String newName) {
        super(contentManager, newWorld, startPosition, newName);
        contentManager.loadTexture("textures/characters/caesar/walking.png", "char_ai_boss_walk");

        contentManager.waitForLoad();
        walkTexture = contentManager.getTexture("char_ai_boss_walk");
        textureSize = new Vector2(walkTexture.getWidth() / WALK_FRAMES, walkTexture.getHeight());

        TextureRegion[] walkFrames = TextureRegion.split(walkTexture, (int)textureSize.x, (int)textureSize.y)[0];
        walkAnimation = new Animation(WALK_ANIM_SPEED, walkFrames);

        stateTime = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (currentPosition.x + 0.5f) * (textureSize.x * Constants.MAP_SCALE) / Box2DConstants.PPM,
                (currentPosition.y + 0.5f) * (textureSize.y * Constants.MAP_SCALE) / Box2DConstants.PPM
        );

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(
                (textureSize.x * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                (textureSize.y * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_AI_BOSS;
        fixtureDef.filter.maskBits = Box2DConstants.MASK_AI_BOSS;

        physicsBody = world.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef);

        physicsBody.setUserData(this);

        // Initialize properties
        setProperty("ACTION", "NONE");
    }

    @Override
    public void update(float dT) {
        stateTime += dT;

        //physicsBody.setLinearVelocity(-0.2f, 0.0f);

    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(walkAnimation.getKeyFrame(stateTime, WALK_ANIM_LOOP),
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
        contentManager.disposeTexture("char_ai_boss_walk");
    }
}
