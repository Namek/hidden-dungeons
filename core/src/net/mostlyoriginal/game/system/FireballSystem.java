package net.mostlyoriginal.game.system;

import net.mostlyoriginal.game.component.Fireball;
import net.mostlyoriginal.game.component.FireballState;
import net.mostlyoriginal.game.component.logic.Position;
import net.mostlyoriginal.game.component.render.DecalComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

@Wire
public class FireballSystem extends EntitySystem {
	ComponentMapper<DecalComponent> sm;
	ComponentMapper<Fireball> fm;
	ComponentMapper<Position> pm;

	DecalBatch decalBatch;
	CameraGroupStrategy groupStrategy;
	Camera cam;
	
	public FireballSystem() {
		super(Aspect.all(Fireball.class, DecalComponent.class, Position.class));
		cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = 1;
		cam.far = 300;
		cam.position.set(0.0f, 0.0f, 5.0f);
		cam.update();
		decalBatch = new DecalBatch(new CameraGroupStrategy(cam));
	}
	
	@Override
	protected void initialize() {
		
	}
	
	@Override
	protected final void processSystem() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		
		int[] array = actives.getData();
		Entity e = flyweight;
		for (int i = 0, s = actives.size(); s > i; i++) {
			e.id = array[i];
			process(e);
		}
		
		decalBatch.flush();
	}
	
	protected final void process(Entity e) {
		Fireball fireball = fm.get(e);
		updateByState(fireball);
		
		Decal fireballDecal = sm.get(e).decal;
		fireballDecal.setWidth(fireball.radius * 2.0f);
		fireballDecal.setHeight(fireball.radius * 2.0f);
		
		Position position = pm.get(e);
		fireballDecal.setPosition(position.pos);
		fireballDecal.lookAt(cam.position, cam.up);
		decalBatch.add(fireballDecal);
	}
	
	private void updateByState(Fireball fireball) {
		switch (fireball.state) {
			case pulsing_up:
				animatePulseUp(fireball);
				break;
			case pulsing_down:
				animatePulseDown(fireball);
				break;
			case nothing:
			default:
				fireball.state = FireballState.pulsing_up;
				break;
		}
	}
	
	// todo move consts to content
	private void animatePulseDown(Fireball fireball) {
		float minRadius = fireball.minRadius;
		float pulseOffset = fireball.tickIncrement;
		fireball.radius = Math.max(fireball.radius - pulseOffset, minRadius);
		if (fireball.radius == minRadius) {
			fireball.state = FireballState.pulsing_up;
		}
	}
	
	private void animatePulseUp(Fireball fireball) {
		float maxRadius = fireball.maxRadius;
		float pulseOffset = fireball.tickIncrement;
		fireball.radius = Math.min(fireball.radius + pulseOffset, maxRadius);
		if (fireball.radius == maxRadius) {
			fireball.state = FireballState.pulsing_down;
		}
		
	}
}
