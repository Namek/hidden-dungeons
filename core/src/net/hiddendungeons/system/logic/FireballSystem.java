
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
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.EntityFactorySystem;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
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
	ComponentMapper<Velocity> mVelocity;

	PerspectiveCamera camera;
	
	boolean shouldThrow = false;
	final Vector3 velocity = new Vector3();
	final Vector3 tmp = new Vector3();
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
		IntBag actives = subscription.getEntities();
		int[] array = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			process(array[i]);
		}
	}
	
	protected final void process(int entityId) {
		updateFireball(entityId);
		Decal fireballDecal = mDecal.get(entityId).decal;
		setDecalRadius(fireballDecal, mFireball.get(entityId).radius);
	}
	
	void updateFireball(int entityId) {
		Fireball fireball = mFireball.get(entityId);
		switch (fireball.state) {
			case pulsing_up:
				setPositionBasedOnPlayer(entityId);
				setStateToThrowIfCan(fireball);
				pulseUpFireball(fireball);
				break;
			case pulsing_down:
				setPositionBasedOnPlayer(entityId);
				setStateToThrowIfCan(fireball);
				pulseDownFireball(fireball);
				break;
			case throwing:
				velocity.set(mTransform.get(entityId).currentPos);
				throwFireball(entityId, velocity.sub(camera.position).scl(20f), fireball.radius, Constants.Fireball.DisappearTime);
				break;
			case throwed:
				break;
			case nothing:
			default:
				fireball.state = FireballState.pulsing_up;
				break;
		}
	}
	
	void setPositionBasedOnPlayer(int entityId) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Player player = playerEntity.getComponent(Player.class);
		Transform transform = mTransform.get(entityId);
		
		transform.desiredPos.set(playerTransform.desiredPos)
			.add(playerTransform.displacement)
			.add(0, player.eyeAltitude, 0)
			.add(tmp.set(playerTransform.direction).limit(0.4f))
			.add(tmp.crs(playerTransform.up).limit(0.15f))
			.add(tmp.set(playerTransform.up).scl(-1f).limit(0.1f));
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
	
	void throwFireball(int entityId, Vector3 speed, float radius, float delay) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		
		EntityEdit edit = world.getEntity(entityId).edit();
		edit.create(Delay.class).delay = delay;
		edit.create(Removable.class).type = Renderable.DECAL;
		edit.create(Collider.class).groups = CollisionGroups.FIREBALL;
		edit.create(Velocity.class);
		edit.create(Dimensions.class).set(radius * 2f, radius * 2f, radius * 2f);
		
		Entity viewFinderEntity = tagManager.getEntity(Tags.VIEW_FINDER);
		tmp.set(viewFinderEntity.getComponent(Transform.class).desiredPos).add(tmp.set(playerTransform.direction).scl(20f));
		Velocity vel = mVelocity.get(entityId);
		vel.velocity.set(tmp);
		vel.setup(Constants.Fireball.MaxSpeed);
		
		mFireball.get(entityId).state = FireballState.throwed;
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
