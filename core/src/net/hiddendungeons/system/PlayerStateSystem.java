package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.logic.FireballSystem;
import net.hiddendungeons.system.logic.SwordFightSystem;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

@Wire
public class PlayerStateSystem extends EntityProcessingSystem implements CollisionEnterListener {
	FireballSystem fireballSystem;
	SwordFightSystem swordFightSystem;
	EntityFactorySystem entityFactorySystem;
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;

	InputSystem inputSystem;
	RenderSystem renderSystem;
	TagManager tags;

	Input input;
	final Vector3 tmp = new Vector3();
	final Vector3 direction = new Vector3();
	final Vector3 up = new Vector3();

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

		transform.toDirection(direction);
		transform.toUpDir(up);


		// Left/right rotation
		direction.rotate(up, -input.getDeltaX() * 0.1f * Constants.Player.MouseSensitivity);
		transform.direction(direction);


		// Strafe movement
		if (input.isKeyPressed(Keys.A)) {
			tmp.set(direction).rotate(90, 0, 1, 0).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.D)) {
			tmp.set(direction).rotate(-90, 0, 1,0).setLength(Constants.Player.MaxSpeed);
		}
		else {
			tmp.setZero();
		}
		velocity.velocity.set(tmp);

		// Forward/backward movement
		if (input.isKeyPressed(Keys.W)) {
			tmp.set(direction).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.S)) {
			tmp.set(direction).setLength(Constants.Player.MaxSpeed).scl(-1);
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

		if (input.isButtonPressed(Input.Buttons.RIGHT) || input.isKeyPressed(Keys.L)) {
			fireballSystem.throwFireball();
		}

		if (input.isButtonPressed(Input.Buttons.LEFT) || input.isKeyPressed(Keys.K)) {
			tags.getEntity(Tags.LeftHand).getComponent(LeftHand.class).wishToAttack = true;
		}

		spawnFireballIfCan();
	}

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);

		if (otherEntity.getComponent(Damage.class) != null) {
			dmgPlayer(entity, otherEntity);
		}
	}

	void dmgPlayer(Entity entity, Entity otherEntity) {
		Player component = mPlayer.get(entity);
		Vector3 position = mTransform.get(entity).currentPos;
		Damage damageComponent = otherEntity.getComponent(Damage.class);
		component.hp -= damageComponent.dmg;

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
