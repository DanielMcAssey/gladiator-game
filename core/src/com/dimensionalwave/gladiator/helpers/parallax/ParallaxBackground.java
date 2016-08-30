package com.dimensionalwave.gladiator.helpers.parallax;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ParallaxBackground {
    private Array<ParallaxLayer> layers;
    private Vector2 speed = new Vector2();

    /**
     * @param layers  The  background layers
     * @param speed A Vector2 attribute to point out the x and y speed
     */
    public ParallaxBackground(Array<ParallaxLayer> layers, Vector2 speed){
        this.layers = layers;
        this.speed.set(speed);
    }

    public void render(Camera camera, SpriteBatch spriteBatch){
        for(ParallaxLayer layer : layers){
            spriteBatch.setProjectionMatrix(camera.projection);
            spriteBatch.begin();
            float currentX = - camera.position.x*layer.parallaxRatio.x % ( layer.texture.getHeight() + layer.padding.x) ;

            if( speed.x < 0 )currentX += -( layer.texture.getWidth() + layer.padding.x);
            do{
                float currentY = - camera.position.y * layer.parallaxRatio.y % ( layer.texture.getHeight() + layer.padding.y) ;
                if( speed.y < 0 )currentY += - (layer.texture.getHeight() + layer.padding.y);
                do{
                    spriteBatch.draw(layer.texture,
                            -camera.viewportWidth/2 + currentX + layer.startPosition.x ,
                            -camera.viewportHeight/2 + currentY +layer.startPosition.y);
                    currentY += ( layer.texture.getHeight() + layer.padding.y );
                }while( currentY < camera.viewportHeight);
                currentX += ( layer.texture.getWidth() + layer.padding.x);
            } while(currentX < camera.viewportWidth);
            spriteBatch.end();
        }
    }
}
