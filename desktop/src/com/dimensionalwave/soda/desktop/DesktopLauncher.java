package com.dimensionalwave.soda.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dimensionalwave.soda.Constants;
import com.dimensionalwave.soda.SodaGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Constants.TITLE;
		config.width = Constants.V_WIDTH * Constants.SCALE;
		config.height = Constants.V_HEIGHT * Constants.SCALE;
		new LwjglApplication(new SodaGame(), config);
	}
}
