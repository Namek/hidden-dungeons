package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class MotionSystem extends EntityProcessingSystem {
	ComponentMapper<Transform> tm;
	
	public MotionSystem() {
		super(Aspect.all(Transform.class));
	}
	
	@Override
	protected void process(Entity e) {
		updatePosition(e);
	}

	void updatePosition(Entity e) {
		Transform transform = tm.get(e);
		
		transform.currentPos.set(transform.desiredPos);
	}
}
