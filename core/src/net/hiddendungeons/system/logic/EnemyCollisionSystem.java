package net.hiddendungeons.system.logic;

import java.util.Set;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Enemy.EnemyState;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.operation.DestroyOperation;
import net.hiddendungeons.operation.animation.EnemyDeathAnimation;
import net.hiddendungeons.operation.animation.EnemyHitAnimation;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.base.collision.messaging.CollisionExitListener;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import static se.feomedia.orion.OperationFactory.*;

public class EnemyCollisionSystem extends EntityProcessingSystem implements CollisionEnterListener, CollisionExitListener {
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

	public EnemyCollisionSystem() {
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

		// AI - needs refactor
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


		// TODO remove those ifs and just delegate the work to another system

		Fireball fireball = otherEntity.getComponent(Fireball.class);
		if (fireball != null) {
			dmgEnemy(entity, otherEntity);
		}

	}

	@Override
	public void onCollisionExit(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);

	}

	void checkCollisions(Entity e) {
		Enemy enemy = mEnemy.get(e);
		Set<Integer> set = enemy.colliders;

		for (Integer i : set) {
		    Entity colide = world.getEntity(i);

		    if (colide == null) {
		    	set.remove(i);
		    }
		}
	}

	void dmgEnemy(Entity enemyEntity, Entity otherEntity) {
		Enemy enemy = mEnemy.get(enemyEntity);
		enemy.state = EnemyState.hurt;
		enemy.hp -= otherEntity.getComponent(Damage.class).dmg;
		Velocity attackerVelocity = otherEntity.getComponent(Velocity.class);

		if (enemy.hp <= 0) {
			sequence(
				operation(EnemyDeathAnimation.class).setup(attackerVelocity.velocity),
				operation(DestroyOperation.class)
			).register(enemyEntity);
		}
		else if (attackerVelocity != null) {
			sequence(
				operation(EnemyHitAnimation.class).setup(attackerVelocity.velocity)
			).register(enemyEntity);
		}
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
