package net.hiddendungeons.component.render;

import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.PooledComponent;

/**
 * Determines type of renderer and target layer.
 *
 * @author Namek
 * @see RenderSystem
 */
public class Renderable extends PooledComponent {
	public static final int NONE = 0;
	public static final int DECAL = 2;
	public static final int SPRITE = 4;
	public static final int MODEL = 8;


    /**
     * Layer: higher is in front, lower is behind.
     */
    public int layer = 0;

    /**
     * Mask for combination of renderer types.
     */
    public int type = 0;


    public Renderable() {
    }

    public Renderable layer(int layer) {
    	this.layer = layer;
    	return this;
    }

    public Renderable renderer(int type) {
    	this.type = type;
    	return this;
    }

	@Override
	protected void reset() {
		type = layer = 0;
	}
}