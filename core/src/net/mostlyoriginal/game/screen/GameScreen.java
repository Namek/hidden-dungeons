package net.mostlyoriginal.game.screen;

import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.utils.builder.WorldBuilder;
import net.mostlyoriginal.game.system.WorldInitSystem;
import net.mostlyoriginal.game.system.view.render.RenderSystem;

import com.artemis.World;

/**
 *
 */
public class GameScreen extends WorldScreen {
	@Override
	protected World createWorld() {
		return new WorldBuilder()
			.with(
				new WorldInitSystem(),
				new RenderSystem()
			)
			.initialize();
	}
}
