package com.dimensionalwave.gladiator.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.dimensionalwave.gladiator.actors.AI.RomanBossAI;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.handlers.GameStateManager;

public class Level1 extends Level {

    private RomanBossAI bossAI;

    public Level1(GameStateManager gameStateManager, ContactListener contactListener, World world, Player player) {
        super(gameStateManager, contactListener, world, player);
        levelIndex = 1;
    }

    @Override
    public void load() {
        super.load();

        bossAI = new RomanBossAI(gameStateManager.game().getContentManager(), world, bossStart, "CAESAR");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        bossAI.update(deltaTime);

        if(bossAI.isDead() && bossAI.isRemovable()) {
            isNextLevel = true;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);

        bossAI.render(spriteBatch);
    }
}
