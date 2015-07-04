package net.hiddendungeons.enums;

import net.hiddendungeons.component.render.Renderable;

/**
 * Higher value is in front, lower is behind.
 *
 * @see Renderable#layer
 */
public interface RenderLayers {
	public static final int WORLD = 0;
	public static final int ENTITIES = 1;
	public static final int HUD = 2;
}
