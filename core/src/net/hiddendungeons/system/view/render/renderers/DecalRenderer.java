package net.hiddendungeons.system.view.render.renderers;

import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.RenderBatchingSystem.EntityProcessAgent;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public class DecalRenderer implements EntityProcessAgent  {
	private DecalBatch batch;
	private ComponentMapper<DecalComponent> dm;

	public DecalRenderer(World world, DecalBatch batch) {
		this.batch = batch;
		dm = world.getMapper(DecalComponent.class);
	}

	@Override
	public void begin() {
	}

	@Override
	public void process(Entity e) {
		Decal decal = dm.get(e).decal;
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
