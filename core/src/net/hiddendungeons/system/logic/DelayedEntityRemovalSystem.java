package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.logic.Delay;
import net.hiddendungeons.component.logic.Removable;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedEntityProcessingSystem;

@Wire
public class DelayedEntityRemovalSystem extends DelayedEntityProcessingSystem  {
	ComponentMapper<Delay> dm;
	ComponentMapper<Removable> rm;
	
	public DelayedEntityRemovalSystem() {
		super(Aspect.all(Delay.class, Removable.class));
	}

	@Override
	protected float getRemainingDelay(Entity e) {
		Delay delay = dm.get(e);
       	return delay.delay;
	}

	@Override
	protected void processDelta(Entity e, float accumulatedDelta) {
		dm.get(e).delay -= accumulatedDelta;
	}

	@Override
	protected void processExpired(Entity e) {
		switch (rm.get(e).type) {
			case Renderable.DECAL:
				world.getSystem(RenderSystem.class).unregisterToDecalRenderer(e);
				break;
			case Renderable.SPRITE:
				world.getSystem(RenderSystem.class).unregisterToSpriteRenderer(e);
				break;
			case Renderable.MODEL:
				world.getSystem(RenderSystem.class).unregisterToModelRenderer(e);
				break;
		}
		e.deleteFromWorld();
	}

}
