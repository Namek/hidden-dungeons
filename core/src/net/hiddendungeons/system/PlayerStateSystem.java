package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;

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
	ComponentMapper<Player> mPlayer;
	ComponentMapper<Velocity> mVelocity;
	
	InputSystem inputSystem;
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
	protected void process(Entity e) {
		Player player = mPlayer.get(e);
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
//			velocity.acceleration.set(velocity.velocity).limit();
		}
		
		// move backward
		if (input.isKeyPressed(Keys.S)) {
			
		}
	}
}
