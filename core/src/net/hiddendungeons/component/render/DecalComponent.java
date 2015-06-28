package net.hiddendungeons.component.render;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class DecalComponent extends PooledComponent {
	public Decal decal;
	public boolean lookAtCamera = true;
	
	public final Vector3 lookAtTarget = new Vector3();
	
	@Override
	protected void reset() {
		decal = null;
		lookAtCamera = true;
		lookAtTarget.setZero();
	}
}
