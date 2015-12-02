package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.object.Damage;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class DamageSystem extends EntityProcessingSystem {

	public DamageSystem() {
		super(Aspect.all(Damage.class));
	}

	@Override
	protected void process(Entity entity) {
		// TODO
	}

}
