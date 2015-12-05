package net.hiddendungeons.component.render;

import net.hiddendungeons.component.base.Transform;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class DecalComponent extends PooledComponent {
	public Decal decal;

	/** Ignore {@link Transform#orientation} by looking at camera. */
	public boolean lookAtCamera = false;

	@Override
	protected void reset() {
		decal = null;
		lookAtCamera = false;
	}
}
