package net.hiddendungeons.component.base;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Namek
 * @see PositionSystem
 */
public class Transform extends PooledComponent {
	
	/**
	 * Position set before collision detection.
	 */
	public final Vector3 desiredPos = new Vector3();
	
	/**
	 * Finally accepted position, result of collision checks and physical forces.
	 */
	public final Vector3 currentPos = new Vector3();
	
	/**
	 * Rotation (axes: x, y, z) around <code>origin</code>. 
	 */
	public final Vector3 rotation = new Vector3();
	
	/**
	 * Origin of rotation.
	 */
	public final Vector3 origin = new Vector3();
	
	
	/**
	 * Sets both desired and current position.
	 */
	public Transform xyz(float x, float y, float z) {
		desiredPos.set(x, y, z);
		currentPos.set(x, y, z);
		return this;
	}
	
	/**
	 * Sets both desired and current position.
	 */
	public Transform xyz(Vector3 pos) {
		desiredPos.set(pos);
		currentPos.set(pos);
		return this;
	}


	@Override
	protected void reset() {
		currentPos.set(0, 0, 0);
		desiredPos.set(0, 0, 0);
		rotation.set(0, 0, 0);
	}
}
