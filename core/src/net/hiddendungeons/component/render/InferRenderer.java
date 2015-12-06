package net.hiddendungeons.component.render;

import net.hiddendungeons.system.view.render.RenderBatchingSystem;
import net.hiddendungeons.system.view.render.DeferredRendererSetSystem;

import com.artemis.PooledComponent;

/**
 * Tags entities having {@link Renderable#type} set to {@link Renderable#NONE}
 * by the time of creation.
 *
 * @see DeferredRendererSetSystem
 * @see RenderBatchingSystem
 *
 * @author Namek
 */
public class InferRenderer extends PooledComponent {

	@Override
	protected void reset() {
	}

}
