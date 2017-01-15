package net.hiddendungeons.component.base;

import net.hiddendungeons.system.base.PositionSystem;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

/**
 * Usage: set {@link #acceleration} and let {@link PositionSystem} calculate velocity.
 *
 * <p>Set value of {@link #friction} to configure how entity slows down when {@link #frictionOn} {@code = true}.</p>
 *
 * @author Namek
 * @see PositionSystem
 */
public class Velocity extends PooledComponent {
	public final Vector3 velocity = new Vector3();
	public final Vector3 acceleration = new Vector3();

	/**
	 * Maximum speed. Length of {@link #velocity} will be limited by this value.
	 */
	public float maxSpeed;

	/**
	 * Describes how much of speed ({@link #velocity}) to decrease during one second.
	 *
	 * <p>
	 * When friction is greater than current speed then object stops moving.
	 * When friction is lower than current speed then object just moves slower.
	 * </p>
	 *
	 * <p>To enable friction, set {@code frictionOn = true}</p>
	 *
	 * @see #maxSpeed
	 */
	public float friction;

	/**
	 * @see #friction
	 */
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

	public void setSpeed(float speed) {
		this.maxSpeed = speed;
		this.velocity.setLength(speed);
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