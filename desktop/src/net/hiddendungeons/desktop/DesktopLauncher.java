package net.hiddendungeons.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.hiddendungeons.GdxArtemisGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.x = 0;
		config.y = 300;
		new LwjglApplication(new GdxArtemisGame(), config);
	}
}
