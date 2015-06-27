package net.hiddendungeons.enums;

import net.hiddendungeons.component.render.Renderable;

/**
 * Higher value is in front, lower is behind.
 * 
 * @see Renderable#layer
 */
public interface RenderLayers {
	public static final int GAME = 0; 
	public static final int HUD = 1; 
}
