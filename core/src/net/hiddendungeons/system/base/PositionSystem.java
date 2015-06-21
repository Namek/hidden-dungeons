package net.hiddendungeons.system.base;

import net.hiddendungeons.component.base.PreviousPosition;
import net.hiddendungeons.component.base.TimeUpdate;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;

/**
 * <p>System that calculates desired position of moving entity.</p>
 * <p>This system needs a companion system (processing after this one)
 * which will check and modify/copy {@code desiredPos} position into {@code currentPos}.</p>
 *
 * @see net.namekdev.components2.artemisext.components.Position
 * @author Namek
 */
@Wire
public class PositionSystem extends EntityProcessingSystem {
	ComponentMapper<Transform> pm;
	ComponentMapper<PreviousPosition> ppm;
	ComponentMapper<Velocity> vm;
	ComponentMapper<TimeUpdate> tum;
	
	private final Vector3 tmpVector = new Vector3(); 


	public PositionSystem() {
		super(Aspect.all(Transform.class, Velocity.class));
	}

	@Override
	protected void process(Entity e) {
		Transform position = pm.get(e);
		PreviousPosition previousPosition = ppm.get(e);
		Velocity velocity = vm.get(e);
		TimeUpdate time = tum.get(e);

		if (previousPosition != null) {
			previousPosition.position.set(position.currentPos);
		}

		float deltaTime = world.getDelta();//TODO GlobalTime.getDeltaTime(time != null && time.dependsOnTimeFactor());
		calculateDesiredPosition(position, velocity, deltaTime);
	}

	public void calculateDesiredPosition(Transform positionComponent, Velocity velocityComponent, float deltaTime) {
		Vector3 velocity = velocityComponent.velocity;
		float maxSpeed = velocityComponent.maxSpeed;

		// Calculate velocity
		tmpVector.set(velocityComponent.acceleration).scl(deltaTime).add(velocity).limit(maxSpeed);

		if (velocityComponent.frictionOn) {
			float friction = velocityComponent.friction * deltaTime;
			float speed = tmpVector.len();

			if (friction < speed) {
				// calculate delta velocity with friction
				tmpVector.nor().scl(-friction);
			}
			else {
				tmpVector.set(velocityComponent.velocity).scl(-1);
			}

			// Add delta velocity
			velocity.add(tmpVector);
		}
		else {
			velocity.set(tmpVector);
		}

		// Calculate position
		tmpVector.set(velocity).scl(deltaTime);
		positionComponent.desiredPos.add(tmpVector);
	}
}