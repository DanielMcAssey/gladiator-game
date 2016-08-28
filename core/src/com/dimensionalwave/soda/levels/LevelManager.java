package com.dimensionalwave.soda.levels;

import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.utils.Queue;
import com.dimensionalwave.soda.Box2DConstants;
import com.dimensionalwave.soda.Constants;
import com.dimensionalwave.soda.actors.AI.RomanAI;
import com.dimensionalwave.soda.actors.AI.RomanBossAI;
import com.dimensionalwave.soda.actors.Hud;
import com.dimensionalwave.soda.actors.Player;
import com.dimensionalwave.soda.handlers.ContactListenerImpl;
import com.dimensionalwave.soda.handlers.GameStateManager;

public class LevelManager {

    private Box2DDebugRenderer debugRenderer;

    private World world;
    private ContactListenerImpl contactListener;

    private GameStateManager gameStateManager;

    private OrthographicCamera gameCamera;
    private OrthographicCamera hudCamera;
    private OrthographicCamera box2DCamera;

    private TiledMap map;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    private Hud hud;
    private Player player;
    private RomanBossAI bossAI;
    private Array<RomanAI> romanAIs;
    private Queue<RomanAI> romanAIsToRemove;

    private int tileWidth = 0;
    private int tileHeight = 0;
    private int mapHeight = 0;
    private int mapWidth = 0;

    public LevelManager(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.gameCamera = gameStateManager.game().getCamera();
        this.hudCamera = gameStateManager.game().getHudCamera();
    }

    public void load(int levelIndex) {

        debugRenderer = new Box2DDebugRenderer();

        hud = new Hud(gameStateManager.game().getContentManager(), world, new Vector2(0, 0), "HUD");

        contactListener = new ContactListenerImpl();

        world = new World(new Vector2(0.0f, -9.80665f), true);
        world.setContactListener(contactListener);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, Constants.V_WIDTH / Box2DConstants.PPM, Constants.V_HEIGHT / Box2DConstants.PPM);

        map = new TmxMapLoader().load("maps/level" + levelIndex + ".tmx");
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(map, Constants.MAP_SCALE);

        MapProperties mapProperties = map.getProperties();
        TiledMapTileLayer groundLayer = (TiledMapTileLayer)map.getLayers().get("layer_ground");

        tileWidth = mapProperties.get("tilewidth", Integer.class);
        tileHeight = mapProperties.get("tileheight", Integer.class);
        mapWidth = (mapProperties.get("width", Integer.class) * tileWidth);
        mapHeight = (mapProperties.get("height", Integer.class) * tileHeight);

        ChainShape chainShape = new ChainShape();
        Vector2[] chainVectors = new Vector2[3];
        chainVectors[0] = new Vector2(
                -(tileWidth * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM, -(tileHeight * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM);
        chainVectors[1] = new Vector2(
                -(tileWidth * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM, (tileHeight * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM);
        chainVectors[2] = new Vector2(
                (tileWidth * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM, (tileHeight * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM);
        chainShape.createChain(chainVectors);

        for(Integer row = 0; row < groundLayer.getHeight(); row++) {
            for(Integer column = 0; column < groundLayer.getWidth(); column++) {

                TiledMapTileLayer.Cell cell =  groundLayer.getCell(column, row);

                if(cell == null || cell.getTile() == null) continue;

                BodyDef tileBodyDef = new BodyDef();
                tileBodyDef.type = BodyDef.BodyType.StaticBody;
                tileBodyDef.position.set(
                        (column + 0.5f) * (tileWidth * Constants.MAP_SCALE) / Box2DConstants.PPM,
                        (row + 0.5f) * (tileHeight * Constants.MAP_SCALE) / Box2DConstants.PPM
                );

                FixtureDef tileFixtureDef = new FixtureDef();
                tileFixtureDef.friction = 0.5f;
                tileFixtureDef.shape = chainShape;
                tileFixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_GROUND;
                tileFixtureDef.filter.maskBits = Box2DConstants.MASK_SCENERY;

                world.createBody(tileBodyDef).createFixture(tileFixtureDef);
            }
        }

        chainShape.dispose();

        player = new Player(gameStateManager.game().getContentManager(), world, new Vector2(1, 1), "GLADIUS");
        //bossAI = new RomanBossAI(gameStateManager.game().getContentManager(), world, new Vector2(6, 1.0f), "CAESAR");

        romanAIs = new Array<RomanAI>();
        romanAIsToRemove = new Queue<RomanAI>();
        //createAI();
    }

    public void update(float deltaTime) {
        world.step(deltaTime, Constants.BOX2D_VEL_ITERS, Constants.BOX2D_POS_ITERS);

        player.setProperty("CAN_JUMP", contactListener.isPlayerOnGround());

        if(bossAI != null) {
            bossAI.update(deltaTime);
        }

        for(RomanAI ai : romanAIs) {
            if(ai.isDead() && ai.isRemovable()) {
                ai.remove();
                ai.dispose();
                romanAIsToRemove.addFirst(ai);
                continue;
            }
            ai.update(deltaTime);
        }

        while(romanAIsToRemove.size > 0) {
            romanAIs.removeValue(romanAIsToRemove.removeFirst(),  true);
        }

        player.update(deltaTime);
    }

    public void render(SpriteBatch spriteBatch) {
        float newCameraXPosition = player.getPosition().x * Box2DConstants.PPM + Constants.V_WIDTH / 3;
        if(newCameraXPosition - (gameCamera.viewportWidth / 2) >= 0 &&
                newCameraXPosition + (gameCamera.viewportWidth / 2) <= (mapWidth * Constants.MAP_SCALE)) {
            gameCamera.position.set(
                    newCameraXPosition,
                    Constants.V_HEIGHT / 2,
                    0
            );
            gameCamera.update();
        }

        tiledMapRenderer.setView(gameCamera);
        tiledMapRenderer.render();

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(gameCamera.combined);

        if(bossAI != null) {
            bossAI.render(spriteBatch);
        }

        for(RomanAI ai : romanAIs) {
            ai.render(spriteBatch);
        }

        player.render(spriteBatch);

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        hud.render(spriteBatch);

        spriteBatch.end();

        if(Constants.IS_DEBUG) {
            debugRenderer.render(world, box2DCamera.combined);
        }

    }

    public void dispose() {

    }

    private void createAI() {
        MapLayer layer = map.getLayers().get("layer_ai");

        if(layer == null) {
            return;
        }

        for(MapObject mapObject : layer.getObjects()) {
            romanAIs.add(new RomanAI(gameStateManager.game().getContentManager(),
                    world,
                    new Vector2(((Float) mapObject.getProperties().get("x") * Constants.MAP_SCALE) / Box2DConstants.PPM,
                            ((Float) mapObject.getProperties().get("y") * Constants.MAP_SCALE) / Box2DConstants.PPM),
                    "ROMAN")
            );
        }
    }

}
