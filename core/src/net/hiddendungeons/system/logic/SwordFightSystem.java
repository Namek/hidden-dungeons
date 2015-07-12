package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.LeftHand.SwordState;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;

@Wire
public class SwordFightSystem extends EntityProcessingSystem {
	TagManager tagManager;
	RenderSystem renderSystem;
	
	ComponentMapper<Transform> mTransform;
	ComponentMapper<LeftHand> mLeftHand;
	ComponentMapper<DecalComponent> mDecalComponent;
	
	final Vector3 tmp = new Vector3();
	boolean shouldAttack = false;
	int animationCount = 0;
	
	public SwordFightSystem() {
		super(Aspect.all(LeftHand.class, Transform.class));
	}
	
	public void attack() {
		shouldAttack = true;
	}
	
	@Override
	protected void process(Entity e) {
		SwordState state = mLeftHand.get(e).state;
		
		switch (state) {
			case hitting:
				mDecalComponent.get(e).lookAtCamera = false;
				shouldAttack = false;
				animateHit(e);
				break;
			case nothing:
			default:
				if (canAttack(e)) {
					mLeftHand.get(e).state = SwordState.hitting;
				}
				setPosition(e);
				mDecalComponent.get(e).lookAtCamera = true;
				break;
		}
		
	}
	
	boolean canAttack(Entity e) {
		return shouldAttack && mLeftHand.get(e).state != SwordState.hitting;
	}
	
	void animateHit(Entity e) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		final Vector3 direction = mTransform.get(e).direction;
		
		LeftHand leftHand = mLeftHand.get(e);
		
		if (leftHand.rotation >= 0.3f) {
			leftHand.dir = -1;
		}
		else if (leftHand.rotation < 0f) {
			animationCount++;
			leftHand.dir = 1;
			if (isHitFinished()) {
				mLeftHand.get(e).state = SwordState.nothing;
				animationCount = 0;
				direction.set(0f, 0f, -1f);
			}
		}
		
		leftHand.rotation += leftHand.dir * world.getDelta() / 2f;
		leftHand.distance += leftHand.dir * world.getDelta() / 5f;
		
		direction.set(tmp.set(playerTransform.up).limit(leftHand.rotation));
		setPosition(e);
	}
	
	boolean isHitFinished() {
		return animationCount == 1;
	}

	void setPosition(Entity e) {
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Player player = playerEntity.getComponent(Player.class);
		
		Transform transform = mTransform.get(e);
		LeftHand leftHand = mLeftHand.get(e);
		transform.desiredPos.set(playerTransform.desiredPos)
			.add(playerTransform.displacement)
			.add(0, player.eyeAltitude, 0)
			.add(tmp.set(playerTransform.direction).limit(leftHand.distance))
			.add(tmp.crs(playerTransform.up).scl(-1f).limit(0.03f));
	}

}
