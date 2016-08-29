package com.dimensionalwave.gladiator.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {

    private Vector2 healthSize = new Vector2(100, 14);
    private Vector2 healthPosition = new Vector2(10, 10);

    private Texture healthBgTexture;
    private Texture healthRedBgTexture;
    private Texture healthTexture;
    private int healthBarFill;

    public Vector2 getHealthPosition() {
        return healthPosition;
    }

    public void setHealthPosition(Vector2 healthPosition) {
        this.healthPosition = healthPosition;
    }

    public HealthBar(Vector2 position, Vector2 size) {
        healthSize = size;
        healthPosition = position;

        Pixmap healthBgPixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        healthBgPixel.setColor(Color.BLACK);
        healthBgPixel.fillRectangle(0, 0, 1, 1);

        Pixmap healthRedPixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        healthRedPixel.setColor(Color.RED);
        healthRedPixel.fillRectangle(0, 0, 1, 1);

        Pixmap healthPixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        healthPixel.setColor(Color.GREEN);
        healthPixel.fillRectangle(0, 0, 1, 1);

        healthBgTexture = new Texture(healthBgPixel, Pixmap.Format.RGB888, false);
        healthRedBgTexture = new Texture(healthRedPixel, Pixmap.Format.RGB888, false);
        healthTexture = new Texture(healthPixel, Pixmap.Format.RGB888, false);
    }

    public void update(float currentHealth, float totalHealth) {
        healthBarFill = (int)(currentHealth / totalHealth * healthSize.x);
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(healthBgTexture, healthPosition.x - 1, healthPosition.y - 1, healthSize.x + 2, healthSize.y + 2);
        spriteBatch.draw(healthRedBgTexture, healthPosition.x, healthPosition.y, healthSize.x, healthSize.y);
        spriteBatch.draw(healthTexture, healthPosition.x, healthPosition.y, healthBarFill, healthSize.y);
    }
}
