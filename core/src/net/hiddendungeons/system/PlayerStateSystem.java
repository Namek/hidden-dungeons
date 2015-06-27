package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
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

@Wire
public class PlayerStateSystem extends EntityProcessingSystem {
	FireballSystem fireballSystem;
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Velocity> mVelocity;
	
	InputSystem inputSystem;
	RenderSystem renderSystem;
	
	Input input;


	public PlayerStateSystem() {
		super(Aspect.all(Player.class));
	}

	@Override
	protected void initialize() {
		inputSystem.enableDebugCamera = false;
		input = Gdx.input;
	}
	
	

	@Override
	protected void inserted(Entity e) {
		Velocity velocity = mVelocity.get(e);

	}

	@Override
	protected void process(Entity e) {
		Player player = mPlayer.get(e);
		Transform transform = mTransform.get(e);
		Velocity velocity = mVelocity.get(e);
		
		// TODO movement
		// TODO head bobbing
		
		// strafe left
		if (input.isKeyPressed(Keys.A)) {
		}
		// strafe right
		if (input.isKeyPressed(Keys.D)) {
			
		}
		// move forward
		if (input.isKeyPressed(Keys.W)) {
			velocity.acceleration.set(transform.rotation).setLength(10000f);
		}
		
		// move backward
		if (input.isKeyPressed(Keys.S)) {
			velocity.acceleration.set(transform.rotation).setLength(1000f);
		}
		
		if (input.isButtonPressed(Input.Buttons.LEFT)) {
			fireballSystem.throwFireball();
		}
	}
}
