package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Camera;

@Wire
public class EnemySystem extends EntityProcessingSystem implements CollisionEnterListener {
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

	@Override
	public void onCollisionEnter(int entityId, int otherEntityId) {
		Entity entity = world.getEntity(entityId);
		Entity otherEntity = world.getEntity(otherEntityId);
		
		if (otherEntity.getComponent(Fireball.class) != null) {
			dmgEnemy(entity, otherEntity);
		}
	}
	
	void dmgEnemy(Entity entity, Entity otherEntity) {
		Enemy component = entity.getComponent(Enemy.class);
		component.hp -= otherEntity.getComponent(Enemy.class).dmg;
		
		if (component.hp < 0.0f) {
			world.getSystem(RenderSystem.class).unregisterToDecalRenderer(entity);
			entity.deleteFromWorld();
		}
	}
	
}
