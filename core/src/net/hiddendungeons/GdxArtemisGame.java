package net.hiddendungeons;

import net.hiddendungeons.screen.GameScreen;

import com.badlogic.gdx.Game;

public class GdxArtemisGame extends Game {

	private static GdxArtemisGame instance;

	@Override
	public void create() {
		instance = this;
		restart();
	}

	public void restart() {
		setScreen(new GameScreen());
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}
}
