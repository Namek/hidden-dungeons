package net.hiddendungeons.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.MagicHand;
import net.hiddendungeons.component.object.WeaponHand;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.logic.SwordFightSystem;
import net.hiddendungeons.system.view.render.RenderSystem;

@Wire
public class PlayerStateSystem extends EntityProcessingSystem implements CollisionEnterListener {
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;

	EntityFactorySystem entityFactorySystem;
	InputSystem inputSystem;
	RenderSystem renderSystem;
	TagManager tags;

	Input input;
	final Vector3 tmp = new Vector3();
	final Vector3 look = new Vector3();
	final Vector3 forward = new Vector3();
	final Vector3 up = new Vector3();
	final Quaternion rotation = new Quaternion();

	// rotation
	float yaw, pitch;

	// head bobbing
	private float headDepth, headDir = 1;


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

		transform.toDirection(look);
		transform.toUpDir(up);


		// Mouse movement
		int mouseDeltaX = input.getDeltaX();
		int mouseDeltaY = input.getDeltaY();
		final float sensitivity = Constants.Player.MouseSensitivity;
		yaw -= mouseDeltaX != 0 ? mouseDeltaX * sensitivity : 0;
		pitch -= mouseDeltaY != 0 ? mouseDeltaY * sensitivity : 0;
		while (yaw >= 360) yaw -= 360;
		while (yaw < 0) yaw += 360;
		while (pitch >= 360) pitch -= 360;
		while (pitch < 0) pitch += 360;
		transform.orientation.setEulerAngles(yaw, pitch, 0);



		transform.toForward(forward);


		// Strafe movement
		if (input.isKeyPressed(Keys.A)) {
			tmp.set(forward).rotate(90, 0, 1, 0).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.D)) {
			tmp.set(forward).rotate(-90, 0, 1,0).setLength(Constants.Player.MaxSpeed);
		}
		else {
			tmp.setZero();
		}
		velocity.velocity.set(tmp);

		// Forward/backward movement
		if (input.isKeyPressed(Keys.W)) {
			tmp.set(forward).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.S)) {
			tmp.set(forward).setLength(Constants.Player.MaxSpeed).scl(-1);
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
			tags.getEntity(Tags.MagicHand).getComponent(MagicHand.class).wishToAttack = true;
		}

		if (input.isButtonPressed(Input.Buttons.LEFT) || input.isKeyPressed(Keys.K)) {
			tags.getEntity(Tags.WeaponHand).getComponent(WeaponHand.class).wishToAttack = true;
		}
	}

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);

		if (otherEntity.getComponent(Damage.class) != null) {
			dmgPlayer(entity, otherEntity);
		}
	}

	private void dmgPlayer(Entity entity, Entity otherEntity) {
		Player component = mPlayer.get(entity);
		Vector3 position = mTransform.get(entity).currentPos;
		Damage damageComponent = otherEntity.getComponent(Damage.class);
		component.hp -= damageComponent.dmg;

		if (component.hp < 0.0f) {
			entity.deleteFromWorld();
		}
		else {
			Vector3 enemyPosition = otherEntity.getComponent(Transform.class).currentPos;
			animateHarm(entity, tmp.set(enemyPosition).sub(position));
		}
	}

	private void animateHarm(Entity e, Vector3 velocity) {
		Velocity vel = mVelocity.get(e);
		vel.velocity.set(velocity);
		vel.setup(Constants.Enemy.MaxSpeed, Constants.Enemy.Friction);
	}
}
