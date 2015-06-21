package net.hiddendungeons.component.logic;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

public class Position extends PooledComponent {
	public final Vector3 pos = new Vector3();

	@Override
	protected void reset() {
		pos.x = 0.0f;
		pos.y = 0.0f;
		pos.z = 0.0f;
	}
}
