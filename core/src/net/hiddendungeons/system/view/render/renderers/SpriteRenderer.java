package net.hiddendungeons.system.view.render.renderers;

import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.SpriteComponent;
import net.hiddendungeons.system.view.render.RenderBatchingSystem.EntityProcessAgent;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteRenderer implements EntityProcessAgent {
	private SpriteBatch batch;
	private ComponentMapper<SpriteComponent> sm;


	public SpriteRenderer(World world, SpriteBatch batch) {
		this.batch = batch;
		sm = world.getMapper(SpriteComponent.class);
	}

	@Override
	public void begin() {
		batch.begin();
	}

	@Override
	public void end() {
		batch.end();
	}

	@Override
	public void process(Entity e) {
		SpriteComponent sprite = sm.get(e);
		sprite.sprite.draw(batch);
	}

	@Override
	public int getType() {
		return Renderable.SPRITE;
	}
}