package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.logic.Position;
import net.hiddendungeons.component.logic.Speed;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class MotionSystem extends EntityProcessingSystem {
	ComponentMapper<Position> pm;
	ComponentMapper<Speed> sm;
	
	public MotionSystem() {
		super(Aspect.all(Position.class, Speed.class));
	}
	
	@Override
	protected void process(Entity e) {
		updatePosition(e);
	}

	void updatePosition(Entity e) {
		Position pos = pm.get(e);
		Speed speed = sm.get(e);
		
		pos.pos.add(speed.speed);
	}
}
