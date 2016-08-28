package com.dimensionalwave.soda.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class ContentManager {

    private HashMap<String, String> textures;
    private AssetManager manager = new AssetManager();

    public ContentManager() {
        textures = new HashMap<String, String>();
    }

    public void update() {
        manager.update();
    }

    public Boolean isLoaded() {
        return (manager.getProgress() >= 1f);
    }

    public void waitForLoad() {
        manager.finishLoading();
    }

    public void loadTexture(String path, String key) {
        manager.load(path, Texture.class);
        textures.put(key, path);
    }

    public Texture getTexture(String key) {
        if(textures.containsKey(key)) {
            return manager.get(textures.get(key), Texture.class);
        }

        return null;
    }

    public void disposeTexture(String key) {
        if(textures.containsKey(key)) {
            manager.unload(textures.remove(key));
        }
    }

}
