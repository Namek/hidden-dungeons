package net.hiddendungeons.operation;

import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.ComponentMapper;
import com.artemis.World;

import se.feomedia.orion.Executor;
import se.feomedia.orion.OperationTree;
import se.feomedia.orion.operation.SingleUseOperation;

public class DestroyOperation extends SingleUseOperation {

	@Override
	public Class<? extends Executor> executorType() {
		return DestroyExecutor.class;
	}


	public static class DestroyExecutor extends SingleUseExecutor<DestroyOperation> {
		World world;
		ComponentMapper<Renderable> mRenderable;
		RenderSystem renderer;


		@Override
		public void initialize(World world) {
			super.initialize(world);
			this.world = world;
			mRenderable = world.getMapper(Renderable.class);
			renderer = world.getSystem(RenderSystem.class);
		}

		@Override
		protected void act(DestroyOperation op, OperationTree node) {
			if (mRenderable.has(op.entityId)) {
				renderer.unregister(world.getEntity(op.entityId));
			}

			world.delete(op.entityId);
		}
	}

};