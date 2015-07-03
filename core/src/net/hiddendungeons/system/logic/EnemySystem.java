package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Enemy.EnemyState;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

@Wire
public class EnemySystem extends EntityProcessingSystem implements CollisionEnterListener {
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
		Entity playerEntity = tagManager.getEntity(Tags.PLAYER);
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
		if (otherEntity.getComponent(Fireball.class) != null) {
			dmgEnemy(entity, otherEntity);
		}
	}
	
	void dmgEnemy(Entity entity, Entity otherEntity) {
		Enemy enemy = mEnemy.get(entity);
		enemy.state = EnemyState.hurt;
		enemy.hp -= otherEntity.getComponent(Fireball.class).dmg;
		Vector3 velocity = otherEntity.getComponent(Velocity.class).velocity;
		animateHit(entity, velocity);
		
		destroyEntity(otherEntity);
		if (enemy.hp < 0.0f) {
			destroyEntity(entity);
		}
	}
	
	void destroyEntity(Entity entity) {
		world.getSystem(RenderSystem.class).unregisterToDecalRenderer(entity);
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
