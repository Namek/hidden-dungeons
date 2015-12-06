package net.hiddendungeons.system.logic;

import java.util.Set;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Enemy.EnemyState;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.base.collision.messaging.CollisionExitListener;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

@Wire
public class EnemySystem extends EntityProcessingSystem implements CollisionEnterListener, CollisionExitListener {
	RenderSystem renderSystem;
	TagManager tagManager;
	Camera camera;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;
	ComponentMapper<Enemy> mEnemy;

	@Override
	protected void initialize() {
		camera = renderSystem.camera;
	}

	public EnemySystem() {
		super(Aspect.all(Enemy.class, DecalComponent.class, Transform.class));
	}

	@Override
	protected void process(Entity e) {
		checkCollisions(e);
		Entity playerEntity = tagManager.getEntity(Tags.Player);
		Vector3 playerPosition = playerEntity.getComponent(Transform.class).currentPos;
		Enemy enemy = mEnemy.get(e);

		Vector3 position = mTransform.get(e).currentPos;
		Velocity velocity = mVelocity.get(e);
		Vector3 vel = velocity.velocity;

		if (enemy.state == EnemyState.normal) {
			if (isPlayerInRadius(position, playerPosition, Constants.Enemy.DetectionRadius)) {
				goToPlayerOrAttackIfInRadius(e, position, playerPosition, velocity);
			}
			else {
				vel.set(0, 0, 0);
			}
		}
		else if (enemy.state == EnemyState.aggressive) {
			goToPlayerOrAttackIfInRadius(e, position, playerPosition, velocity);
		}
		else if (vel.isZero() && enemy.state == EnemyState.hurt) {
			enemy.state = EnemyState.aggressive;
		}
	}

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);

		Fireball fireball = otherEntity.getComponent(Fireball.class);
		if (fireball != null) {
			dmgEnemy(entity, otherEntity);
		}

		LeftHand leftHand = otherEntity.getComponent(LeftHand.class);
		if (leftHand != null) {
			mEnemy.get(entityId).colliders.add(otherEntityId);
		}
	}

	@Override
	public void onCollisionExit(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);

		LeftHand leftHand = otherEntity.getComponent(LeftHand.class);
		if (leftHand != null) {
			mEnemy.get(entityId).colliders.remove(otherEntityId);
		}
	}

	void checkCollisions(Entity e) {
		Enemy enemy = mEnemy.get(e);
		Set<Integer> set = enemy.colliders;

		for (Integer i : set) {
		    Entity colide = world.getEntity(i);

		    if (colide == null) {
		    	set.remove(i);
		    }
		    else {
		    	LeftHand hand = colide.getComponent(LeftHand.class);
		    	if (hand != null && hand.state == LeftHand.SwordState.Attack) {
		    		dmgEnemy(e, colide);
		    	}
		    }
		}
	}

	void dmgEnemy(Entity entity, Entity otherEntity) {
		Enemy enemy = mEnemy.get(entity);
		enemy.state = EnemyState.hurt;
		enemy.hp -= otherEntity.getComponent(Damage.class).dmg;
		Velocity velocityComponent = otherEntity.getComponent(Velocity.class);

		if (velocityComponent != null) {
			Vector3 velocity = velocityComponent.velocity;
			animateHit(entity, velocity);
			destroyEntity(otherEntity);
		}

		if (enemy.hp < 0.0f) {
			destroyEntity(entity);
		}
	}

	void destroyEntity(Entity entity) {
		entity.deleteFromWorld();
	}

	void animateHit(Entity e, Vector3 velocity) {
		Velocity vel = e.getComponent(Velocity.class);
		vel.velocity.set(velocity.limit(Constants.Enemy.MaxSpeed));
		vel.setup(Constants.Enemy.MaxSpeed, Constants.Enemy.Friction);
	}

	void goToPlayerOrAttackIfInRadius(Entity e, Vector3 position, Vector3 playerPosition, Velocity velocity) {
		if (isPlayerInRadius(position, playerPosition, Constants.Enemy.AttackRadius)) {
			animateAttack(e);
			velocity.velocity.set(0, 0, 0);
		}
		else {
			velocity.setup(Constants.Enemy.MaxSpeed);
			velocity.velocity.set(playerPosition).sub(position);
			velocity.velocity.y = 0f;
		}
	}

	void animateAttack(Entity e) {
		// todo
	}

	boolean isPlayerInRadius(Vector3 position, Vector3 playerPosition, float radius) {
		return position.dst(playerPosition) < radius;
	}
}
