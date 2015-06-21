package net.hiddendungeons.screen;

import net.hiddendungeons.system.InputSystem;
import net.hiddendungeons.system.WorldInitSystem;
import net.hiddendungeons.system.base.PositionSystem;
import net.hiddendungeons.system.base.TimeSystem;
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
				new TimeSystem(),
				new PositionSystem(),
				new RenderSystem()
			)
			.initialize();
	}
}
