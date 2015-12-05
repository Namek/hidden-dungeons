package net.hiddendungeons.component.base;

import net.hiddendungeons.system.base.PositionSystem;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Defines a transformation of entity:
 * <ol>
 *   <li>desired and current positions</li>
 *   <li>orientation</li>
 *   <li>(optional) graphical {@link #displacement} of position</li>
 * </ol>
 *
 * <p>{@link #desiredPos} defines position which can be calculated by {@link PositionSystem}
 * and {@link currentPos} is the result of collision check and accepting or modifying
 * {@link #desiredPos}. Simply acccepting desired position as current position can be
 * done by copying value of {@link #desiredPos} into {@link #currentPos}.</p>
 *
 * <p><b>Orientation</b> is defined by {@link #direction} and {@link #up} vectors.</p>
 *
 * @author Namek
 * @see PositionSystem
 */
public class Transform extends PooledComponent {
	/** Position set before collision detection. */
	public final Vector3 desiredPos = new Vector3();

	/** Finally accepted position, result of collision checks and physical forces. */
	public final Vector3 currentPos = new Vector3();

	/** Additional displacement to position. Usually used for graphics puroses, like head bobbing. */
	public final Vector3 displacement = new Vector3();

	/** Defines direction for orientation purposes. */
	public final Vector3 direction = new Vector3(0, 0, -1);

	/** Defines rotation around {@link #direction}, it's perpendicular to it. */
	public final Vector3 up = new Vector3(0, 1, 0);


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

	public Transform direction(Vector3 dir) {
		direction.set(dir);
		return this;
	}

	public Transform direction(float x, float y, float z) {
		direction.set(x, y, z);
		return this;
	}

	/**
	 * Sets {@link #up} vector to {@code (0, 1, 0)} and then rotates into given direction.
	 */
	public void look(float dirX, float dirY, float dirZ) {
		direction.set(dirX, dirY, dirZ).nor();

		// right = cross(direction, 0,1,0)
		// up = cross(right, direction)
		up.set(direction).crs(0, 1, 0).crs(direction);
	}

	/**
	 *
	 * @see #direction(float, float, float)
	 */
	public void look(Vector3 dir) {
		look(dir.x, dir.y, dir.z);
	}

	/**
	 * Gets {@link #currentPos}, {@link #direction} and {@link #up} into given Matrix.
	 */
	public Matrix4 toMatrix4(Matrix4 mat) {
		mat.setToLookAt(direction, up).inv();
		mat.trn(currentPos);
		return mat;
	}

	@Override
	protected void reset() {
		currentPos.setZero();
		desiredPos.setZero();
		displacement.setZero();
		direction.set(0, 0, -1);
		up.set(0, 1, 0);
	}
}
