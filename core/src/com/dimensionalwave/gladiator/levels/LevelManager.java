package com.dimensionalwave.gladiator.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dimensionalwave.gladiator.Box2DConstants;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.actors.Hud;
import com.dimensionalwave.gladiator.actors.Player;
import com.dimensionalwave.gladiator.enums.GameStates;
import com.dimensionalwave.gladiator.handlers.ContactListenerImpl;
import com.dimensionalwave.gladiator.handlers.ContentManager;
import com.dimensionalwave.gladiator.handlers.GameStateManager;
import com.dimensionalwave.gladiator.helpers.parallax.ParallaxBackground;
import com.dimensionalwave.gladiator.helpers.parallax.ParallaxLayer;
import com.dimensionalwave.gladiator.input.InputAction;
import com.dimensionalwave.gladiator.input.InputManager;

import java.util.HashMap;

public class LevelManager {

    private Box2DDebugRenderer debugRenderer;

    private World world;
    private ContactListenerImpl contactListener;

    private GameStateManager gameStateManager;

    private OrthographicCamera gameCamera;
    private OrthographicCamera hudCamera;
    private OrthographicCamera box2DCamera;
    private ParallaxBackground parallaxBackground;

    private Hud hud;
    private Player player;

    private BitmapFont font = new BitmapFont();

    private boolean isLost = false;
    private boolean isWin = false;

    private HashMap<Integer, Level> levels = new HashMap<Integer, Level>();
    private Level activeLevel;

    public LevelManager(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.gameCamera = gameStateManager.game().getCamera();
        this.hudCamera = gameStateManager.game().getHudCamera();
    }

    public void load() {

        debugRenderer = new Box2DDebugRenderer();

        contactListener = new ContactListenerImpl();

        world = new World(new Vector2(0.0f, Constants.BOX2D_GRAVITY), true);
        world.setContactListener(contactListener);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, (Constants.V_WIDTH / Constants.CAM_SCALE) / Box2DConstants.PPM, (Constants.V_HEIGHT / Constants.CAM_SCALE) / Box2DConstants.PPM);

        hud = new Hud(gameStateManager.game().getContentManager(), world, new Vector2(0, 0), "HUD");
        player = new Player(gameStateManager.game().getContentManager(), world, new Vector2(1, 1), "GLADIUS");

        levels.put(0, new Level0(gameStateManager, contactListener, world, player));
        levels.put(1, new Level1(gameStateManager, contactListener, world, player));

        ContentManager contentManager = gameStateManager.game().getContentManager();
        contentManager.loadTexture("textures/backgrounds/sky.png", "bg_sky");
        contentManager.loadTexture("textures/backgrounds/trees.png", "bg_trees");
        contentManager.loadTexture("textures/backgrounds/town.png", "bg_town");

        contentManager.waitForLoad();

        ParallaxLayer skyLayer = new ParallaxLayer(contentManager.getTexture("bg_sky"), new Vector2(), new Vector2(0, 0));
        ParallaxLayer treesLayer = new ParallaxLayer(contentManager.getTexture("bg_trees"), new Vector2(), new Vector2(0, 0));
        ParallaxLayer townLayer = new ParallaxLayer(contentManager.getTexture("bg_town"), new Vector2(), new Vector2(0, 0));
        Array<ParallaxLayer> layers = new Array<ParallaxLayer>();
        layers.add(skyLayer);
        layers.add(treesLayer);
        layers.add(townLayer);
        parallaxBackground = new ParallaxBackground(layers, new Vector2(100, 0));
    }

    public void loadLevel(int levelIndex) {
        if(activeLevel != null) {
            activeLevel.dispose();
            activeLevel = null;
        }

        activeLevel = levels.get(levelIndex);
        activeLevel.load();
    }

    public void update(float deltaTime) {
        if(activeLevel == null) {
            return;
        }

        if(player.isDead() && player.isRemovable()) {
            isLost = true;
        }

        if(activeLevel.getLevelIndex() == 1 && activeLevel.isNextLevel()) {
            isWin = true;
        }

        if(isLost || isWin) {
            if(InputManager.isPressed(InputAction.MENU_ENTER)) {
                if(isLost) {
                    gameStateManager.pushState(GameStates.MENU);
                } else if(isWin) {
                    gameStateManager.pushState(GameStates.LEADERBOARD);
                }
            }
            return;
        }

        if(activeLevel.isNextLevel()) {
            loadLevel(activeLevel.getLevelIndex() + 1);
        }

        world.step(deltaTime, Constants.BOX2D_VEL_ITERS, Constants.BOX2D_POS_ITERS);

        player.setProperty("CAN_JUMP", contactListener.isPlayerOnGround());

        activeLevel.update(deltaTime);

        hud.setProperty("HUD_HEALTH", player.getPlayerHealth());
        hud.setProperty("HUD_WEAPON_STRENGTH", (int)player.getWeaponStrength());
        hud.setProperty("HUD_SHIELD_STRENGTH", (int)player.getArmorStrength());
        hud.setProperty("HUD_KILL_COUNT", player.getKills());
        player.update(deltaTime);
        hud.update(deltaTime);
    }

    public void render(SpriteBatch spriteBatch) {
        if(activeLevel == null) {
            return;
        }

        float newCameraXPosition = player.getPosition().x * Box2DConstants.PPM + gameCamera.viewportWidth / 3;
        if(newCameraXPosition - (gameCamera.viewportWidth / 2) >= 0 &&
                newCameraXPosition + (gameCamera.viewportWidth / 2) <= (activeLevel.getMapWidth())) {
            gameCamera.position.set(
                    newCameraXPosition,
                    gameCamera.viewportHeight / 2,
                    0
            );
            gameCamera.update();

            if(Constants.IS_DEBUG) {
                box2DCamera.position.set(newCameraXPosition / Box2DConstants.PPM,
                        box2DCamera.position.y,
                        0);
                box2DCamera.update();
            }
        }

        parallaxBackground.render(gameCamera, spriteBatch);
        activeLevel.renderLevel();

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(gameCamera.combined);
        activeLevel.render(spriteBatch);
        player.render(spriteBatch);

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        hud.render(spriteBatch);

        if(isLost || isWin) {
            if(isLost) {

            }

        }

        spriteBatch.end();

        if(Constants.IS_DEBUG) {
            debugRenderer.render(world, box2DCamera.combined);
        }

    }

    public void dispose() {
        player.dispose();

        if(activeLevel != null) {
            activeLevel.dispose();
            activeLevel = null;
        }
    }

}
