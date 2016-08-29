package com.dimensionalwave.gladiator.handlers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.actors.ActorDirection;

public class ActionAnimation {

    private Vector2 textureSize = new Vector2(0, 0);
    private Vector2 textureOrigin = new Vector2(0, 0);
    private Vector2 animationPosition = new Vector2(0, 0);
    private Animation animation = null;
    private Boolean isLooped = false;

    private float stateTime = 0f;

    public ActionAnimation(Texture texture,
                           Vector2 originOffset,
                           int frameCount,
                           float animationSpeed,
                           boolean isLooped) {
        this.isLooped = isLooped;

        textureSize = new Vector2(texture.getWidth() / frameCount, texture.getHeight());
        textureOrigin = new Vector2(textureSize.x / 2f + originOffset.x, textureSize.y / 2f + originOffset.y);

        TextureRegion[] frames = TextureRegion.split(texture, (int)textureSize.x, (int)textureSize.y)[0];
        animation = new Animation(animationSpeed, frames);
    }

    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(stateTime);
    }

    public void update(float stateTime, Vector2 position) {
        this.stateTime = stateTime;
        this.animationPosition = position;
    }

    public void render(SpriteBatch spriteBatch, ActorDirection direction) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, isLooped);

        if(!currentFrame.isFlipX() && direction.equals(ActorDirection.LEFT)) {
            currentFrame.flip(true, false);
        } else if(currentFrame.isFlipX() && direction.equals(ActorDirection.RIGHT)) {
            currentFrame.flip(true, false);
        }

        spriteBatch.draw(currentFrame,
                (animationPosition.x * Box2DConstants.PPM) - (textureSize.x / 2),
                (animationPosition.y * Box2DConstants.PPM) - (textureSize.y / 2) - 2,
                textureOrigin.x,
                textureOrigin.y,
                textureSize.x,
                textureSize.y,
                1f,
                1f,
                0.0f);
    }

}
