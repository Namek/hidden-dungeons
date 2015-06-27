
package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Delay;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.logic.Removable;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.Fireball.FireballState;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;
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
	TagManager tagManager;
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<Fireball> mFireball;
	ComponentMapper<Transform> mTransform;
	
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
		Decal fireballDecal = mDecal.get(e).decal;
		setDecalRadius(fireballDecal, mFireball.get(e).radius);
	}
	
	void updateFireball(Entity e) {
		Fireball fireball = mFireball.get(e);
		switch (fireball.state) {
			case pulsing_up:
				setPositionBasedOnPlayer(e);
				setStateToThrowIfCan(fireball);
				pulseUpFireball(fireball);
				break;
			case pulsing_down:
				setPositionBasedOnPlayer(e);
				setStateToThrowIfCan(fireball);
				pulseDownFireball(fireball);
				break;
			case throwing:
				velocity.set(mTransform.get(e).currentPos);
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
	
	void setPositionBasedOnPlayer(Entity e) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Player player = playerEntity.getComponent(Player.class);
		Transform transform = mTransform.get(e);
		
		transform.desiredPos.set(playerTransform.desiredPos)
			.add(playerTransform.displacement)
			.add(0, player.eyeAltitude, 0)
			.mulAdd(playerTransform.orientation, 0.2f);
	}

	void setDecalRadius(Decal fireballDecal, float radius) {
		fireballDecal.setWidth(radius * 2f);
		fireballDecal.setHeight(radius * 2f);
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
		Decal decal = e.getComponent(DecalComponent.class).decal;
		edit.create(Dimensions.class).set(decal.getHeight(), decal.getWidth(), 0.01f);
		Velocity vel = e.getComponent(Velocity.class);
		vel.velocity.set(speed);
		vel.acceleration.set(0f, 0f, 0f);
		vel.setup(20f);
		
		mFireball.get(e).state = FireballState.throwed;
	}
	
	void createFireball(Entity e) {
		world.getSystem(EntityFactorySystem.class).createFireball(mTransform.get(e).currentPos);
	}

	public void throwFireball() {
		shouldThrow = true;
	}
	
	public boolean canSpawnFireball() {
		return fireballsInHand == 0;
	}
}
