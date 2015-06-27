package net.hiddendungeons.screen;

import net.hiddendungeons.system.InputSystem;
import net.hiddendungeons.system.PlayerStateSystem;
import net.hiddendungeons.system.WorldInitSystem;
import net.hiddendungeons.system.base.EntityFactorySystem;
import net.hiddendungeons.system.base.PositionSystem;
import net.hiddendungeons.system.base.TimeSystem;
import net.hiddendungeons.system.base.collision.CollisionDetectionSystem;
import net.hiddendungeons.system.base.events.EventSystem;
import net.hiddendungeons.system.logic.DelayedEntityRemovalSystem;
import net.hiddendungeons.system.logic.FireballSystem;
import net.hiddendungeons.system.logic.MotionSystem;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.utils.builder.WorldBuilder;

import com.artemis.World;

public class GameScreen extends WorldScreen {
	@Override
	protected World createWorld() {
		return new WorldBuilder()
			.with(
				new EntityFactorySystem(),
				new WorldInitSystem(),
				new InputSystem(),
				new EventSystem(),
				new PlayerStateSystem(),
				new FireballSystem(),
				new TimeSystem(),
				new PositionSystem(),
				new CollisionDetectionSystem(),
				new MotionSystem(),
				new RenderSystem(),
				new DelayedEntityRemovalSystem()
			)
			.initialize();
	}
}
