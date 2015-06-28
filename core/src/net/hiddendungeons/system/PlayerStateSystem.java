package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.logic.FireballSystem;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

@Wire
public class PlayerStateSystem extends EntityProcessingSystem implements CollisionEnterListener {
	FireballSystem fireballSystem;
	EntityFactorySystem entityFactorySystem;
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;
	
	InputSystem inputSystem;
	RenderSystem renderSystem;
	
	Input input;
	final Vector3 tmp = new Vector3();
	
	// head bobbing
	private float headDepth, headDir = 1;

	float fireballRespawn = Constants.Fireball.RespawnTime;
	
	public PlayerStateSystem() {
		super(Aspect.all(Player.class));
	}

	@Override
	protected void initialize() {
		inputSystem.enableDebugCamera = false;
		input = Gdx.input;
	}

	@Override
	protected void process(Entity e) {
		Player player = mPlayer.get(e);
		Transform transform = mTransform.get(e);
		Velocity velocity = mVelocity.get(e);


		// Strafe movement
		if (input.isKeyPressed(Keys.A)) {
			tmp.set(transform.orientation).rotate(90, 0, 1,0).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.D)) {
			tmp.set(transform.orientation).rotate(-90, 0, 1,0).setLength(Constants.Player.MaxSpeed);			
		}
		else {
			tmp.setZero();
		}
		velocity.velocity.set(tmp);
		
		// Forward/backward movement
		if (input.isKeyPressed(Keys.W)) {
			tmp.set(transform.orientation).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.S)) {
			tmp.set(transform.orientation).setLength(Constants.Player.MaxSpeed).scl(-1);
		}
		else {
			tmp.setZero();
		}
		velocity.velocity.add(tmp);

		
		// Head bobbing
		if (velocity.getCurrentSpeed() != 0) {
			headDepth += headDir * world.getDelta()/2;

			if (headDepth >= Constants.Player.MaxHeadBob)
				headDir = -1;
			else if (headDepth <= -Constants.Player.MaxHeadBob)
				headDir = 1;
		}
		else {
			headDepth = 0;
		}

		transform.displacement.y = headDepth;

		if (input.isButtonPressed(Input.Buttons.LEFT)) {
			fireballSystem.throwFireball();
		}
		
		spawnFireballIfCan();
	}

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);
		
		if (otherEntity.getComponent(Enemy.class) != null) {
			dmgPlayer(entity, otherEntity);
		}
	}
	
	void dmgPlayer(Entity entity, Entity otherEntity) {
		Player component = mPlayer.get(entity);
		Vector3 position = mTransform.get(entity).currentPos;
		Enemy enemyComponent = otherEntity.getComponent(Enemy.class);
		component.hp -= enemyComponent.dmg;
		
		if (component.hp < 0.0f) {
			world.getSystem(RenderSystem.class).unregisterToDecalRenderer(entity);
			entity.deleteFromWorld();
		}
		else {
			Vector3 enemyPosition = otherEntity.getComponent(Transform.class).currentPos;
			animateHarm(entity, tmp.set(enemyPosition).sub(position));
		}
	}
	
	void animateHarm(Entity e, Vector3 velocity) {
		Velocity vel = mVelocity.get(e);
		vel.velocity.set(velocity);
		vel.setup(Constants.Enemy.MaxSpeed, Constants.Enemy.Friction);
	}
	
	void spawnFireballIfCan() {
		if (fireballSystem.canSpawnFireball()) {
			if (fireballRespawn < 0f) {
				entityFactorySystem.createFireball(tmp.set(0, 0, -25));
				fireballRespawn = Constants.Fireball.RespawnTime;
			}
			else {
				fireballRespawn -= world.delta;
			}
		}
	}
	
}
