package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Delay;
import net.hiddendungeons.component.logic.Removable;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.Fireball.FireballState;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityTransmuter;
import com.artemis.EntityTransmuterFactory;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

@Wire
public class FireballSystem extends EntityProcessingSystem {
	RenderSystem renderSystem;
	ComponentMapper<DecalComponent> sm;
	ComponentMapper<Fireball> fm;
	ComponentMapper<Transform> tm;
	PerspectiveCamera camera;
	EntityTransmuter transmuter;
	
	public FireballSystem() {
		super(Aspect.all(Fireball.class, DecalComponent.class, Transform.class));
	}
	
	@Override
	protected void initialize() {
		camera = renderSystem.camera;
		transmuter = new EntityTransmuterFactory(world)
	        .add(Delay.class)
	        .add(Removable.class)
	        .add(Collider.class)
	        .add(Velocity.class)
	        .add(Dimensions.class)
	        .build();
	}
	
	@Override
	protected final void process(Entity e) {
		updateFireball(e);
		Decal fireballDecal = sm.get(e).decal;
		setDecalRadius(fireballDecal, fm.get(e).radius);
		setDecalPosition(fireballDecal, tm.get(e).desiredPos);
	}
	
	void updateFireball(Entity e) {
		Fireball fireball = fm.get(e);
		switch (fireball.state) {
			case pulsing_up:
				tm.get(e).desiredPos.set(camera.position.x, camera.position.y, camera.position.z).mulAdd(camera.direction, 0.2f);
				pulseUpFireball(fireball);
				break;
			case pulsing_down:
				tm.get(e).desiredPos.set(camera.position.x, camera.position.y, camera.position.z).mulAdd(camera.direction, 0.2f);
				pulseDownFireball(fireball);
				break;
			case throwing:
				Vector3 speed = new Vector3(tm.get(e).desiredPos);
				throwFireball(e, speed.sub(camera.position).scl(10f), 10f);
				break;
			case throwed:
				break;
			case nothing:
			default:
				fireball.state = FireballState.pulsing_up;
				break;
		}
		
	}
	
	void setDecalRadius(Decal fireballDecal, float radius) {
		fireballDecal.setWidth(radius * 2f);
		fireballDecal.setHeight(radius * 2f);
	}
	
	void setDecalPosition(Decal fireballDecal, Vector3 position) {
		fireballDecal.setPosition(position);
		fireballDecal.lookAt(camera.position, camera.up);
	}
	
	void pulseDownFireball(Fireball fireball) {
		float minRadius = fireball.minRadius;
		float pulseOffset = fireball.tickIncrement;
		fireball.radius = Math.max(fireball.radius - pulseOffset, minRadius);
		if (fireball.radius == minRadius) {
			fireball.state = FireballState.pulsing_up;
		}
	}
	
	void pulseUpFireball(Fireball fireball) {
		float maxRadius = fireball.maxRadius;
		float pulseOffset = fireball.tickIncrement;
		fireball.radius = Math.min(fireball.radius + pulseOffset, maxRadius);
		if (fireball.radius == maxRadius) {
			fireball.state = FireballState.throwing;
		}
	}
	
	void throwFireball(Entity e, Vector3 speed, float delay) {
		transmuter.transmute(e);
		Velocity vel = e.getComponent(Velocity.class);
		vel.velocity.set(speed);
		vel.acceleration.set(0f, 0f, 0f);
		vel.setup(20f);
		e.getComponent(Dimensions.class).set(10,  10,  1);
		e.getComponent(Delay.class).delay = delay;
		e.getComponent(Removable.class).type = Renderable.DECAL;
		fm.get(e).state = FireballState.throwed;
	}
}
