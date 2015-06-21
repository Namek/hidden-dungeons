package net.hiddendungeons.component.base;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

/**
 * 
 * @author Namek
 * @see PositionSystem
 */
public class Velocity extends PooledComponent {
	public final Vector3 velocity = new Vector3();
	public final Vector3 acceleration = new Vector3();
	public float maxSpeed;
	public float friction;
	public boolean frictionOn;
	
	
	public Velocity setup(float maxSpeed) {
		this.maxSpeed = maxSpeed;
		this.friction = 0;
		this.frictionOn = false;
		return this;
	}
	
	public Velocity setup(float maxSpeed, float friction) {
		this.maxSpeed = maxSpeed;
		this.friction = friction;
		this.frictionOn = true;
		return this;
	}
	
	public float getCurrentSpeed() {
		return velocity.len();
	}

	@Override
	protected void reset() {
		velocity.set(0, 0, 0);
		acceleration.set(0, 0, 0);
		maxSpeed = 0;
		friction = 0;
		this.frictionOn = false;
	}
}