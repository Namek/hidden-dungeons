package net.hiddendungeons.component.logic;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector3;

public class Speed extends  PooledComponent {
	public final Vector3 speed = new Vector3();

	@Override
	protected void reset() {
		speed.x = 0.0f;
		speed.y = 0.0f;
		speed.z = 0.0f;
	}
}
