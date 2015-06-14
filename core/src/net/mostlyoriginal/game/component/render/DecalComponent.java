package net.mostlyoriginal.game.component.render;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

/**
 * Sticker = Decal
 *
 */
public class DecalComponent extends PooledComponent {
	public final Decal decal = new Decal();
	
	@Override
	protected void reset() {
		decal.setTextureRegion(null);
	}
	
}
