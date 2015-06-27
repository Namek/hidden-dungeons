package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

@Wire
public class EnemySystem extends EntityProcessingSystem {
	RenderSystem renderSystem;
	Camera camera;
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<Transform> mTransform;
	
	@Override
	protected void initialize() {
		camera = renderSystem.camera;
	}
	
	public EnemySystem() {
		super(Aspect.all(Enemy.class, DecalComponent.class, Transform.class));
	}
	
	@Override
	protected void process(Entity e) {
		
	}
	
}
