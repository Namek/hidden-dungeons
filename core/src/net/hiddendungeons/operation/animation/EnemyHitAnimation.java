package net.hiddendungeons.operation.animation;

import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.enums.Constants;
import se.feomedia.orion.Executor;
import se.feomedia.orion.OperationTree;
import se.feomedia.orion.operation.TemporalOperation;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;

public class EnemyHitAnimation extends TemporalOperation {
	public final Vector3 hitDirection = new Vector3();

	public EnemyHitAnimation setup(Vector3 hitDirection) {
		this.hitDirection.set(hitDirection).nor();
		this.duration = 0.5f;
		return this;
	}

	@Override
	public Class<? extends Executor> executorType() {
		return HitAnimationExecutor.class;
	}

	public static class HitAnimationExecutor extends TemporalOperation.TemporalExecutor<EnemyHitAnimation> {
		World world;

		@Override
		public void initialize(World world) {
			super.initialize(world);
			this.world = world;
		}

		@Override
		protected void act(float delta, float percent, EnemyHitAnimation anim, OperationTree node) {
			Entity entity = world.getEntity(anim.entityId);
			Velocity vel = entity.getComponent(Velocity.class);
			vel.velocity.set(anim.hitDirection).limit(Constants.Enemy.MaxSpeed);
			vel.setup(Constants.Enemy.MaxSpeed, Constants.Enemy.Friction);
		}
	}
}