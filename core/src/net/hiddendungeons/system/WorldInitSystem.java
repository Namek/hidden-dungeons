package net.hiddendungeons.system;

import static net.hiddendungeons.builders.MapBuilder.*;
import net.hiddendungeons.builders.MapBuilder;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.system.base.collision.CollisionDetectionSystem;
import net.hiddendungeons.system.base.collision.CollisionGroupsRelations;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.hiddendungeons.system.view.render.debug.TopDownEntityDebugSystem;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector3;

@Wire
public class WorldInitSystem extends BaseSystem {
	EntityFactorySystem factorySystem;
	RenderSystem renderSystem;
	TopDownEntityDebugSystem topViewDebugSystem;

	boolean isInitialized = false;


	void _initialize() {
		Vector3 start = new Vector3();
		Vector3 dir = new Vector3();

		setCollisionRelations();

		float width = 5f, height = 3.5f;
		Vector3 playerPos = new Vector3(width/2, 0, 0);
		Vector3 playerDir = new Vector3(0, 0, -1);

		factorySystem.createBasicEnemy(start.set(width / 2, 1, -15));
		factorySystem.createPlayer(playerPos, playerDir);

		MapBuilder mapBuilder = new MapBuilder(world, renderSystem)
			.createCorridor(start.set(0, 0, 0), dir.set(0, 0, -1), 20f, width, height, LEFT|RIGHT|NEAR)
			.createCorridor(start.set(0, 0, -25), dir.set(0, 0, -1), 100f, width, height, LEFT|RIGHT|FAR)
			.createCorridor(start.set(0, 0, -20), dir.set(-1, 0, 0), 30f, width, height)
			.createCorridor(start.set(width, 0, -25), dir.set(1, 0, 0), 40f, width, height)
			.createCorridor(start.set(0, 0, -20), dir.set(0, 0, -1), 5f, width, height, NONE);

		topViewDebugSystem.min.set(mapBuilder.bboxMin.x, Math.max(-20, mapBuilder.bboxMin.z));
		topViewDebugSystem.max.set(mapBuilder.bboxMax.x, Math.min(0, mapBuilder.bboxMax.z));
	}

	void setCollisionRelations() {
		CollisionGroupsRelations relations = world.getSystem(CollisionDetectionSystem.class).relations;
		relations.connectGroups(CollisionGroups.PLAYER, CollisionGroups.ENEMY);
		relations.connectGroups(CollisionGroups.ENEMY, CollisionGroups.ENERGY_BALL);
		relations.connectGroups(CollisionGroups.ENEMY, CollisionGroups.SWORD);
	}

	@Override
	protected void processSystem() {
		if (!isInitialized) {
			_initialize();
			isInitialized = true;
		}
	}
}
