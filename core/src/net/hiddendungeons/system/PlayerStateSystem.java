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
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;
	
	InputSystem inputSystem;
	RenderSystem renderSystem;
	
	Input input;
	final Vector3 tmp = new Vector3();
	
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


		// Strafe movement
		if (input.isKeyPressed(Keys.A)) {
			tmp.set(transform.rotation).rotate(90, 0, 1,0).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.D)) {
			tmp.set(transform.rotation).rotate(-90, 0, 1,0).setLength(Constants.Player.MaxSpeed);			
		}
		else {
			tmp.setZero();
		}
		velocity.velocity.set(tmp);
		
		// Forward/backward movement
		if (input.isKeyPressed(Keys.W)) {
			tmp.set(transform.rotation).setLength(Constants.Player.MaxSpeed);
		}
		else if (input.isKeyPressed(Keys.S)) {
			tmp.set(transform.rotation).setLength(Constants.Player.MaxSpeed).scl(-1);
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
	}

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity e = world.getEntity(otherEntityId);
		
		if (e.getComponent(Enemy.class) != null) {
			killEnemy(e);
		}
	}
	
	void killEnemy(Entity e) {
		world.getSystem(RenderSystem.class).unregisterToDecalRenderer(e);
		e.deleteFromWorld();
	}
}
