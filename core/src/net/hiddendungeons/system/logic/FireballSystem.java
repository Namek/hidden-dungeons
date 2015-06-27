
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
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.system.EntityFactorySystem;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

@Wire
public class FireballSystem extends EntitySystem {
	RenderSystem renderSystem;
	ComponentMapper<DecalComponent> sm;
	ComponentMapper<Fireball> fm;
	ComponentMapper<Transform> tm;
	
	PerspectiveCamera camera;
	
	boolean shouldThrow = false;
	Vector3 velocity = new Vector3();
	int fireballsInHand = 0;
	
	public FireballSystem() {
		super(Aspect.all(Fireball.class, DecalComponent.class, Transform.class));
	}
	
	@Override
	protected void initialize() {
		camera = renderSystem.camera;
	}
	
	@Override
	protected final void processSystem() {
		fireballsInHand = 0;
		int[] array = actives.getData();
		Entity e = flyweight;
		for (int i = 0, s = actives.size(); s > i; i++) {
			e.id = array[i];
			process(e);
		}
	}
	
	protected final void process(Entity e) {
		updateFireball(e);
		Decal fireballDecal = sm.get(e).decal;
		setDecalRadius(fireballDecal, fm.get(e).radius);
		setDecalPosition(fireballDecal, tm.get(e).currentPos);
	}
	
	void updateFireball(Entity e) {
		Fireball fireball = fm.get(e);
		switch (fireball.state) {
			case pulsing_up:
				tm.get(e).desiredPos.set(camera.position.x, camera.position.y, camera.position.z).mulAdd(camera.direction, 0.2f);
				setStateToThrowIfCan(fireball);
				pulseUpFireball(fireball);
				fireballsInHand++;
				break;
			case pulsing_down:
				tm.get(e).desiredPos.set(camera.position.x, camera.position.y, camera.position.z).mulAdd(camera.direction, 0.2f);
				setStateToThrowIfCan(fireball);
				pulseDownFireball(fireball);
				
				break;
			case throwing:
				velocity.set(tm.get(e).currentPos);
				throwFireball(e, velocity.sub(camera.position).scl(10f), 4f);
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
	
	void setStateToThrowIfCan(Fireball fireball) {
		if (shouldThrow) {
			fireball.state = FireballState.throwing;
			shouldThrow = false;
		}
		else {
			fireballsInHand++;
		}
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
			fireball.state = FireballState.pulsing_down;
		}
	}
	
	void throwFireball(Entity e, Vector3 speed, float delay) {
		EntityEdit edit = e.edit();
		edit.create(Delay.class).delay = delay;
		edit.create(Removable.class).type = Renderable.DECAL;
		edit.create(Collider.class).groups = CollisionGroups.PLAYER_MONSTERS;
		edit.create(Velocity.class);
		edit.create(Dimensions.class).set(10,  10,  1);
		
		Velocity vel = e.getComponent(Velocity.class);
		vel.velocity.set(speed);
		vel.acceleration.set(0f, 0f, 0f);
		vel.setup(20f);
		
		fm.get(e).state = FireballState.throwed;
	}
	
	void createFireball(Entity e) {
		world.getSystem(EntityFactorySystem.class).createFireball(tm.get(e).currentPos);
	}

	public void throwFireball() {
		shouldThrow = true;
	}
	
	public boolean canSpawnFireball() {
		return fireballsInHand == 0;
	}
}
