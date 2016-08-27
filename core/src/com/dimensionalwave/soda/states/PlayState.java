package com.dimensionalwave.soda.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dimensionalwave.soda.Box2DConstants;
import com.dimensionalwave.soda.Constants;
import com.dimensionalwave.soda.handlers.ContactListenerImpl;
import com.dimensionalwave.soda.handlers.GameStateManager;
import com.dimensionalwave.soda.input.InputAction;
import com.dimensionalwave.soda.input.InputManager;

public class PlayState extends GameState {

    private Box2DDebugRenderer debugRenderer;

    private World world;
    private ContactListenerImpl contactListener;

    private OrthographicCamera box2DCamera;

    private TiledMap map;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    public PlayState(GameStateManager manager) {
        super(manager);

        debugRenderer = new Box2DDebugRenderer();

        world = new World(new Vector2(0.0f, -9.80665f), true);
        contactListener = new ContactListenerImpl();
        world.setContactListener(contactListener);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, Constants.V_WIDTH / Box2DConstants.PPM, Constants.V_HEIGHT / Box2DConstants.PPM);

        map = new TmxMapLoader().load("maps/level0.tmx");
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(map, Constants.MAP_SCALE);

        MapProperties mapProperties = map.getProperties();
        TiledMapTileLayer groundLayer = (TiledMapTileLayer)map.getLayers().get("layer_ground");

        int tileWidth = mapProperties.get("tilewidth", Integer.class);
        int tileHeight = mapProperties.get("tileheight", Integer.class);

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

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(
                        (tileWidth * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM,
                        (tileHeight * Constants.MAP_SCALE) / 2 / Box2DConstants.PPM
                );

                FixtureDef tileFixtureDef = new FixtureDef();
                tileFixtureDef.friction = 0;
                tileFixtureDef.shape = polygonShape;
                tileFixtureDef.filter.categoryBits = Box2DConstants.CATEGORY_GROUND;
                tileFixtureDef.filter.maskBits = Box2DConstants.MASK_SCENERY;

                world.createBody(tileBodyDef).createFixture(tileFixtureDef);
            }
        }

    }

    @Override
    public void handleInput() {

        if(InputManager.isPressed(InputAction.ACTION_JUMP) && contactListener.isPlayerOnGround()) {

        }


    }

    @Override
    public void update(float delta) {

        handleInput();

        world.step(delta, Constants.BOX2D_VEL_ITERS, Constants.BOX2D_POS_ITERS);

    }

    @Override
    public void render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if(Constants.IS_DEBUG) {
            debugRenderer.render(world, box2DCamera.combined);
        }
    }

    @Override
    public void dispose() {

    }
}
