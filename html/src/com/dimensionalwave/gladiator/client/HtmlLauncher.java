package com.dimensionalwave.gladiator.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dimensionalwave.gladiator.Constants;
import com.dimensionalwave.gladiator.GladiatorGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Constants.V_WIDTH * Constants.SCALE, Constants.V_HEIGHT * Constants.SCALE);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new GladiatorGame();
        }
}