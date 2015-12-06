package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.render.InferRenderer;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.RenderBatchingSystem.EntityProcessAgent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class DeferredRendererSetSystem extends EntityProcessingSystem {
	ComponentMapper<InferRenderer> mInferRenderer;
	ComponentMapper<Renderable> mRenderable;

	RenderBatchingSystem renderSystem;


	public DeferredRendererSetSystem() {
		super(Aspect.all(InferRenderer.class, Renderable.class));
	}

	@Override
	protected void process(Entity entity) {
		Renderable renderable = mRenderable.getSafe(entity);

		boolean removeInferTag = renderable == null || renderable.type != Renderable.NONE;
		boolean inferRenderer = renderable != null && renderable.type != Renderable.NONE;

		if (inferRenderer) {
			EntityProcessAgent renderer = renderSystem.getRendererByType(renderable.type);
			renderSystem.registerAgent(entity, renderer);
		}

		if (removeInferTag) {
			mInferRenderer.remove(entity);
		}
	}

}
