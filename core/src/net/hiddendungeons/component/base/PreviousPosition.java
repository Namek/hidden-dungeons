package net.hiddendungeons.component.base;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

public class PreviousPosition extends PooledComponent {
	public final Vector3 position = new Vector3();

	@Override
	protected void reset() {
		position.set(0, 0, 0);
	}
}
