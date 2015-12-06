package net.hiddendungeons.component.base;

import net.hiddendungeons.system.base.PositionSystem;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
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
 * <p><b>Orientation</b> is defined by {@link #orientation} quaternion.</p>
 *
 * @author Namek
 * @see PositionSystem
 */
public class Transform extends PooledComponent {
	/** Default direction when null orientation is given.
	 * Don't modify this, it's rather by OpenGL design. */
	public static final Vector3 DEFAULT_DIRECTION = new Vector3(0, 0, -1);

	/** Default up vector. */
	public static final Vector3 UP_VECTOR = new Vector3(0, 1, 0);

	private static final Vector3 tmpVect = new Vector3();
	private static final Vector3 tmpVect2 = new Vector3();


	/** Position set before collision detection. */
	public final Vector3 desiredPos = new Vector3();

	/** Finally accepted position, result of collision checks and physical forces. */
	public final Vector3 currentPos = new Vector3();

	/** Additional displacement to position. Usually used for graphics puroses, like head bobbing. */
	public final Vector3 displacement = new Vector3();

	/** Defines orientation due to 3D vector {@link #DEFAULT_DIRECTION} = {@code (0, 0, -1)}. */
	public final Quaternion orientation = new Quaternion();


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

	/**
	 * @see {@link #direction(float, float, float)}
	 */
	public Transform direction(Vector3 dir) {
		return direction(dir.x, dir.y, dir.z);
	}

	/** Sets direction by keeping current roll angle. */
	public Transform direction(float x, float y, float z) {
		float roll = orientation.getRollRad();
		look(x, y, z);

		float yaw = orientation.getYawRad();
		float pitch = orientation.getPitchRad();
		orientation.setEulerAnglesRad(yaw, pitch, roll);

		return this;
	}

	/**
	 * Sets orientation by comparing it to {@link #DEFAULT_DIRECTION}.
	 */
	public void look(float dirX, float dirY, float dirZ) {
		orientation.setFromCross(DEFAULT_DIRECTION, tmpVect.set(dirX, dirY, dirZ));
	}

	/**
	 * Sets orientation by comparing it to {@link #DEFAULT_DIRECTION}.
	 * @see #direction(float, float, float)
	 */
	public void look(Vector3 dir) {
		look(dir.x, dir.y, dir.z);
	}

	/**
	 * Gets {@link #currentPos}, {@link #direction} and {@link #up} into given Matrix.
	 * Ignores {@link #displacement}.
	 */
	public Matrix4 toMatrix4(Matrix4 mat) {
		orientation.toMatrix(mat.val);
		mat.trn(currentPos);
		return mat;
	}

	/**
	 * Sets {@link #desiredPos} and {@link #orientation} from given Matrix.
	 * Don't use scaling in given matrix because quaternion is not normalized.
	 */
	public void fromMatrix4(Matrix4 mat) {
		mat.getTranslation(desiredPos);
		mat.getRotation(orientation);
	}

	public Vector3 toDirection(Vector3 outDir) {
		outDir.set(DEFAULT_DIRECTION);
		outDir.mul(orientation);
		return outDir;
	}

	public Vector3 toUpDir(Vector3 outUp) {
		return directionToUp(toDirection(tmpVect), outUp);
	}

	public Vector3 toRightDir(Vector3 outRight) {
		return toDirection(outRight).crs(toUpDir(tmpVect2));
	}

	public Vector3 toLeftDir(Vector3 outLeft) {
		return toRightDir(outLeft).scl(-1f);
	}

	/**
	 * Transforms direction vector to up vector.
	 * Base up vector is {@link #UP_VECTOR} = {@code (0, 1, 0)}.
	 *
	 * @return given {@code outUp} reference
	 */
	public static Vector3 directionToUp(Vector3 dir, Vector3 outUp) {
		// right = cross(direction, 0,1,0)
		// up = cross(right, direction)
		return outUp.set(dir).crs(UP_VECTOR).crs(dir);
	}

	@Override
	protected void reset() {
		currentPos.setZero();
		desiredPos.setZero();
		displacement.setZero();
		orientation.idt();
	}
}
