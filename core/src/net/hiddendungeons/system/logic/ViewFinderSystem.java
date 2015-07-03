package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.ViewFinder;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;

@Wire
public class ViewFinderSystem extends EntityProcessingSystem {
	TagManager tagManager;
	ComponentMapper<Transform> mTransform;
	
	final Vector3 tmp = new Vector3();
	
	public ViewFinderSystem() {
		super(Aspect.all(ViewFinder.class, Transform.class));
	}

	@Override
	protected void process(Entity e) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Player player = playerEntity.getComponent(Player.class);
		Transform transform = mTransform.get(e);
		
		transform.desiredPos.set(playerTransform.desiredPos)
			.add(playerTransform.displacement)
			.add(0, player.eyeAltitude, 0)
			.add(tmp.set(playerTransform.direction).limit(0.2f));
	}
	
}
