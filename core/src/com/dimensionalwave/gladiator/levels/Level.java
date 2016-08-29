package com.dimensionalwave.gladiator.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.actors.Powerups.ArmorPowerup;
import com.dimensionalwave.gladiator.actors.Powerups.HealthPowerup;
import com.dimensionalwave.gladiator.actors.Powerups.Powerup;
import com.dimensionalwave.gladiator.actors.Powerups.WeaponPowerup;
import com.dimensionalwave.gladiator.handlers.ContactListenerImpl;
import com.dimensionalwave.gladiator.handlers.GameStateManager;

public abstract class Level {

    protected GameStateManager gameStateManager;
    protected Player player;
    protected OrthographicCamera gameCamera;
    protected World world;

    protected boolean isNextLevel = false;
    protected int levelIndex = 0;

    protected Array<Powerup> powerups = new Array<Powerup>();
    protected Vector2 playerStart = new Vector2(0, 0);
    protected Vector2 bossStart = new Vector2(0, 0);

    private TiledMap map;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;
    private MapProperties mapProperties;
    private ContactListenerImpl contactListener;

    private int tileWidth = 0;
    private int tileHeight = 0;
    private int mapHeight = 0;
    private int mapWidth = 0;

    public Level(GameStateManager gameStateManager, ContactListener contactListener, World world, Player player) {
        this.gameStateManager = gameStateManager;
        this.world = world;
        this.gameCamera = gameStateManager.game().getCamera();
        this.player = player;
        this.contactListener = (ContactListenerImpl)contactListener;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public boolean isNextLevel() {
        return isNextLevel;
    }

    public void setIsNextLevel(boolean nextLevel) {
        isNextLevel = nextLevel;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public TiledMap getMap() {
        return map;
    }

    public MapProperties getMapProperties() {
        return mapProperties;
    }

    public void load() {
        map = new TmxMapLoader().load("maps/level" + levelIndex + ".tmx");
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(map);

        mapProperties = map.getProperties();
        TiledMapTileLayer groundLayer = (TiledMapTileLayer)map.getLayers().get("layer_ground");

        tileWidth = mapProperties.get("tilewidth", Integer.class);
        tileHeight = mapProperties.get("tileheight", Integer.class);
        mapWidth = (mapProperties.get("width", Integer.class) * tileWidth);
        mapHeight = (mapProperties.get("height", Integer.class) * tileHeight);

        ChainShape chainShape = new ChainShape();
        Vector2[] chainVectors = new Vector2[3];
        chainVectors[0] = new Vector2(
                -(tileWidth) / 2 / Box2DConstants.PPM, -(tileHeight) / 2 / Box2DConstants.PPM);
        chainVectors[1] = new Vector2(
                -(tileWidth) / 2 / Box2DConstants.PPM, (tileHeight) / 2 / Box2DConstants.PPM);
        chainVectors[2] = new Vector2(
                (tileWidth) / 2 / Box2DConstants.PPM, (tileHeight) / 2 / Box2DConstants.PPM);
        chainShape.createChain(chainVectors);

        for(Integer row = 0; row < groundLayer.getHeight(); row++) {
            for(Integer column = 0; column < groundLayer.getWidth(); column++) {

                TiledMapTileLayer.Cell cell =  groundLayer.getCell(column, row);

                if(cell == null || cell.getTile() == null) continue;

                BodyDef tileBodyDef = new BodyDef();
                tileBodyDef.type = BodyDef.BodyType.StaticBody;
                tileBodyDef.position.set(
                        (column + 0.5f) * (tileWidth) / Box2DConstants.PPM,
                        (row + 0.5f) * (tileHeight) / Box2DConstants.PPM
                );

                FixtureDef tileFixtureDef = new FixtureDef();
                tileFixtureDef.friction = 0.5f;
                tileFixtureDef.shape = chainShape;
                tileFixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_SCENERY;
                tileFixtureDef.filter.maskBits = Box2DConstants.MASK_SCENERY;

                world.createBody(tileBodyDef).createFixture(tileFixtureDef);
            }
        }

        chainShape.dispose();
        findWalls();
        findWarps();
        findPowerups();
    }

    public void update(float deltaTime) {
        for(Powerup powerup : powerups) {
            powerup.update(deltaTime);
        }

        for(Powerup powerup : contactListener.getPowerupsToCollect()) {
            if(!powerups.contains(powerup, true)) {
                continue;
            }

            if(powerup instanceof ArmorPowerup) {
                player.setProperty("ARMOR_STRENGTH", player.getArmorStrength() + 10.0f);
            } else if(powerup instanceof WeaponPowerup) {
                player.setProperty("WEAPON_STRENGTH", player.getWeaponStrength() + 10.0f);
            } else if(powerup instanceof HealthPowerup) {
                if(player.getPlayerHealth() + 10.0f >= 100.0f) {
                    player.setProperty("HEALTH", 100.0f);
                } else {
                    player.setProperty("HEALTH", player.getPlayerHealth() + 10.0f);
                }
            }

            powerup.dispose();
            powerups.removeValue(powerup, true);
        }
        contactListener.getPowerupsToCollect().clear();

    }

    public void render(SpriteBatch spriteBatch) {
        for(Powerup powerup : powerups) {
            powerup.render(spriteBatch);
        }
    }

    public void renderLevel() {
        tiledMapRenderer.setView(gameCamera);
        tiledMapRenderer.render();
    }

    public void dispose() {
        map.dispose();
    }

    private void findPowerups() {
        MapLayer layer = getMap().getLayers().get("layer_powerup");

        if(layer == null) {
            return;
        }

        for(MapObject mapObject : layer.getObjects()) {

            float posX = (Float) mapObject.getProperties().get("x");
            float posY = (Float) mapObject.getProperties().get("y");

            if(mapObject.getName().equals("weapon")) {
                Powerup weaponPowerup = new WeaponPowerup(gameStateManager.game().getContentManager(),
                        world,
                        new Vector2(0, 0),
                        "POWERUP_WEAPON");
                weaponPowerup.setPosition(new Vector2(posX, posY));
                powerups.add(weaponPowerup);
            } else if(mapObject.getName().equals("health")) {
                Powerup healthPowerup = new HealthPowerup(gameStateManager.game().getContentManager(),
                        world,
                        new Vector2(0, 0),
                        "POWERUP_HEALTH");
                healthPowerup.setPosition(new Vector2(posX, posY));
                powerups.add(healthPowerup);
            } else if(mapObject.getName().equals("armor")) {
                Powerup armorPowerup = new ArmorPowerup(gameStateManager.game().getContentManager(),
                        world,
                        new Vector2(0, 0),
                        "POWERUP_ARMOR");
                armorPowerup.setPosition(new Vector2(posX, posY));
                powerups.add(armorPowerup);
            }
        }
    }

    private void findWarps() {
        MapLayer layer = getMap().getLayers().get("layer_warp");

        if(layer == null) {
            return;
        }

        for(MapObject mapObject : layer.getObjects()) {
            float posX = (Float) mapObject.getProperties().get("x");
            float posY = (Float) mapObject.getProperties().get("y");
            float width = (Float) mapObject.getProperties().get("width");
            float height = (Float) mapObject.getProperties().get("height");

            if(mapObject.getName().equals("player_start")) {
                playerStart = new Vector2(posX, posY);
                continue;
            }

            if(mapObject.getName().equals("boss_start")) {
                bossStart = new Vector2(posX, posY);
                continue;
            }

            // Must be normal warp
            BodyDef warpBody = new BodyDef();
            warpBody.type = BodyDef.BodyType.StaticBody;
            warpBody.position.set(
                    (posX) / Box2DConstants.PPM,
                    (posY) / Box2DConstants.PPM
            );

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(
                    (width) / Box2DConstants.PPM,
                    (height) / Box2DConstants.PPM
            );

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.isSensor = true;
            fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_WARP;
            fixtureDef.filter.maskBits = Box2DConstants.MASK_WARP;

            Body body = world.createBody(warpBody);
            body.createFixture(fixtureDef).setUserData("warp_level_next");
            body.setUserData(this);

            polygonShape.dispose();
        }
    }

    private void findWalls() {
        MapLayer layer = getMap().getLayers().get("layer_wall");

        if(layer == null) {
            return;
        }

        for(MapObject mapObject : layer.getObjects()) {

            float posX = (Float) mapObject.getProperties().get("x");
            float posY = (Float) mapObject.getProperties().get("y");
            float width = (Float) mapObject.getProperties().get("width");
            float height = (Float) mapObject.getProperties().get("height");

            BodyDef wallBody = new BodyDef();
            wallBody.type = BodyDef.BodyType.StaticBody;
            wallBody.position.set(
                    (posX) / Box2DConstants.PPM,
                    (posY) / Box2DConstants.PPM
            );

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(
                    (width) / Box2DConstants.PPM,
                    (height) / Box2DConstants.PPM
            );

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_SCENERY;
            fixtureDef.filter.maskBits = Box2DConstants.MASK_SCENERY;

            Body body = world.createBody(wallBody);

            if(mapObject.getName() != null && mapObject.getName().equals("wall_death")) {
                fixtureDef.isSensor = true;
                body.createFixture(fixtureDef).setUserData(mapObject.getName());
            } else {
                body.createFixture(fixtureDef);
            }



            polygonShape.dispose();
        }
    }

}
