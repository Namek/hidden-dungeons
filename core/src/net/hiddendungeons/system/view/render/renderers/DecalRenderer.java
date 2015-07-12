package net.hiddendungeons.system.view.render.renderers;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.RenderBatchingSystem.EntityProcessAgent;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public class DecalRenderer implements EntityProcessAgent  {
	private DecalBatch batch;
	private RenderSystem renderSystem;
	private ComponentMapper<DecalComponent> mDecal;
	private ComponentMapper<Transform> mTransform;

	public DecalRenderer(World world, DecalBatch batch) {
		this.batch = batch;
		mDecal = world.getMapper(DecalComponent.class);
		mTransform = world.getMapper(Transform.class);
		renderSystem = world.getSystem(RenderSystem.class);
	}

	@Override
	public void begin() {
	}

	@Override
	public void process(Entity e) {
		Camera camera = renderSystem.camera;
		DecalComponent decalComponent = mDecal.get(e);
		Decal decal = decalComponent.decal;
		Transform transform = mTransform.get(e);
		
		decal.setPosition(transform.currentPos);
		
		if (decalComponent.lookAtCamera) {
			decal.lookAt(camera.position, camera.up);
		}
		else {
			decal.lookAt(transform.direction.add(camera.position), transform.up);
			//throw new UnsupportedOperationException("Todo: set up vector based on orientation");
		}
		batch.add(decal);
	}

	@Override
	public void end() {
		batch.flush();
	}

	@Override
	public int getType() {
		return Renderable.DECAL;
	}

}
