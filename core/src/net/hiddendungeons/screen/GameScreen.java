package net.hiddendungeons.screen;

import net.hiddendungeons.system.InputSystem;
import net.hiddendungeons.system.WorldInitSystem;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.utils.builder.WorldBuilder;

import com.artemis.World;

public class GameScreen extends WorldScreen {
	@Override
	protected World createWorld() {
		return new WorldBuilder()
			.with(
				new WorldInitSystem(),
				new InputSystem(),
				new RenderSystem()
			)
			.initialize();
	}
}
