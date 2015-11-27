package net.hiddendungeons.screen;

import com.artemis.WorldConfigurationBuilder;

import net.hiddendungeons.system.EntityFactorySystem;
import net.hiddendungeons.system.InputSystem;
import net.hiddendungeons.system.PlayerStateSystem;
import net.hiddendungeons.system.WorldInitSystem;
import net.hiddendungeons.system.base.PositionSystem;
import net.hiddendungeons.system.base.TimeSystem;
import net.hiddendungeons.system.base.collision.CollisionDetectionSystem;
import net.hiddendungeons.system.base.events.EventSystem;
import net.hiddendungeons.system.logic.DelayedEntityRemovalSystem;
import net.hiddendungeons.system.logic.EnemySystem;
import net.hiddendungeons.system.logic.FireballSystem;
import net.hiddendungeons.system.logic.MotionSystem;
import net.hiddendungeons.system.logic.SwordFightSystem;
import net.hiddendungeons.system.logic.ViewFinderSystem;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.hiddendungeons.system.view.render.SwordRenderSystem;
import net.hiddendungeons.system.view.render.debug.TopDownEntityDebugSystem;
import net.mostlyoriginal.api.screen.core.WorldScreen;

import com.artemis.World;
import com.artemis.managers.TagManager;

public class GameScreen extends WorldScreen {
	@Override
	protected World createWorld() {
		return new World(new WorldConfigurationBuilder()
			.with(
				new EntityFactorySystem(),
				new WorldInitSystem(),
				new InputSystem(),
				new EventSystem(),
				new PlayerStateSystem(),
				new EnemySystem(),
				new TimeSystem(),
				new PositionSystem(),
				new ViewFinderSystem(),
				new FireballSystem(),
				new SwordFightSystem(),
				new CollisionDetectionSystem(),
				new SwordRenderSystem(),
				new MotionSystem(),
				new RenderSystem(),
				new TopDownEntityDebugSystem(),
				new DelayedEntityRemovalSystem()
			)
			.with(
				 new TagManager()
			).build());
	}
}
