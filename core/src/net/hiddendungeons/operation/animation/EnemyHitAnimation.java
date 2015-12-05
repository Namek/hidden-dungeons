package net.hiddendungeons.operation.animation;

import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.enums.Constants;
import se.feomedia.orion.Executor;
import se.feomedia.orion.OperationTree;
import se.feomedia.orion.operation.TemporalOperation;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector3;

public class EnemyHitAnimation extends TemporalOperation {
	public final Vector3 hitDirection = new Vector3();

	public EnemyHitAnimation setup(Vector3 hitDirection) {
		this.hitDirection.set(hitDirection).nor();
		this.duration = 0.5f;
		return this;
	}

	@Override
	public Class<? extends Executor<?>> executorType() {
		return HitAnimationExecutor.class;
	}

	@Wire
	public static class HitAnimationExecutor extends TemporalOperation.TemporalExecutor<EnemyHitAnimation> {
		ComponentMapper<Velocity> mVelocity;

		@Override
		protected void act(float delta, float percent, EnemyHitAnimation anim, OperationTree node) {
			Velocity vel = mVelocity.get(anim.entityId);
			vel.velocity.set(anim.hitDirection).limit(Constants.Enemy.MaxSpeed);
			vel.setup(Constants.Enemy.MaxSpeed, Constants.Enemy.Friction);
		}
	}
}