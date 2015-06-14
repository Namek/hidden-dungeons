package net.mostlyoriginal.game.screen;

import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.utils.builder.WorldBuilder;

import com.artemis.BaseSystem;
import com.artemis.World;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

	public static final String BACKGROUND_COLOR_HEX = "969291";

	@Override
	protected World createWorld() {
		return new WorldBuilder()
			.with(
				instanceGameSystems()
			).initialize();
	}

	private BaseSystem[] instanceGameSystems() {
		return new BaseSystem[] {
		};
	}
}
