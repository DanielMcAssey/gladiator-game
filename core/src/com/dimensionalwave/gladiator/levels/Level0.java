package com.dimensionalwave.gladiator.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.actors.AI.RomanAI;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.handlers.GameStateManager;

public class Level0 extends Level {

    private Array<RomanAI> romanAIs;
    private Queue<RomanAI> romanAIsToRemove;

    public Level0(GameStateManager gameStateManager, ContactListener contactListener, World world, Player player) {
        super(gameStateManager, contactListener, world, player);
        levelIndex = 0;
    }

    @Override
    public void load() {
        super.load();

        player.setPosition(playerStart);
        romanAIs = new Array<RomanAI>();
        romanAIsToRemove = new Queue<RomanAI>();
        createAI();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for(RomanAI ai : romanAIs) {
            if(ai.isDead() && ai.isRemovable()) {
                ai.dispose();
                romanAIsToRemove.addFirst(ai);
                continue;
            }

            if(ai.getTarget() == null) {
                ai.setTarget(player);
            }

            ai.update(deltaTime);
        }

        while(romanAIsToRemove.size > 0) {
            romanAIs.removeValue(romanAIsToRemove.removeFirst(),  true);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);

        for(RomanAI ai : romanAIs) {
            ai.render(spriteBatch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for(RomanAI ai : romanAIs) {
            ai.dispose();
        }

        romanAIs.clear();

        for(Body body : miscBodies) {
            world.destroyBody(body);
        }

        miscBodies.clear();
    }

    private void createAI() {
        MapLayer layer = getMap().getLayers().get("layer_ai");

        if(layer == null) {
            return;
        }

        for(MapObject mapObject : layer.getObjects()) {
            float posX = (Float) mapObject.getProperties().get("x");
            float posY = (Float) mapObject.getProperties().get("y");

            RomanAI ai = new RomanAI(gameStateManager.game().getContentManager(), world, new Vector2(0, 0), "ROMAN");
            ai.setPosition(new Vector2(posX, posY));
            romanAIs.add(ai);
        }
    }
}
