package net.hiddendungeons.system.logic;

import static com.badlogic.gdx.math.MathUtils.cos;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Interpolation;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.object.EnergyBall;
import net.hiddendungeons.component.object.Growable;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;


/**
 * Animates size of energy balls.
 */
public class EnergyBallStateSystem extends EntityProcessingSystem {
	M<EnergyBall> mEnergyBall;
	M<DecalComponent> mDecal;
	M<Transform> mTransform;
	M<Velocity> mVelocity;
	M<Growable> mGrowable;

	TagManager tagManager;
	RenderSystem renderSystem;

	PerspectiveCamera camera;


	public EnergyBallStateSystem() {
		super(Aspect.all(
			EnergyBall.class,
			DecalComponent.class,
			Transform.class,
			Growable.class
		));
	}

	@Override
	protected void initialize() {
		camera = renderSystem.camera;
	}

	@Override
	protected void process(Entity e) {
		EnergyBall ball = mEnergyBall.get(e);
		Decal decal = mDecal.get(e).decal;
		Growable grow = mGrowable.get(e);

		// pulse
		float factor = cos(ball.pulse.update(world.delta));

		ball.radius = Constants.Fireball.PulseMinRadius
			+ Constants.Fireball.PulseRadiusDiff * factor;

		ball.radius *= Interpolation.sineOut.apply(grow.percent);

		decal.setWidth(ball.radius * 2);
		decal.setHeight(ball.radius * 2);
	}

	public void throwBall(Entity e) {
		EnergyBall ball = mEnergyBall.get(e);
		EntityEdit edit = e.edit();

		float size = ball.radius*2;
		edit.create(Collider.class).groups = CollisionGroups.ENERGY_BALL;
		edit.create(Dimensions.class).set(size, size, size);

		Entity playerEntity = tagManager.getEntity(Tags.Player);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Velocity vel = edit.create(Velocity.class);
		playerTransform.toDirection(vel.velocity);
		vel.setSpeed(Constants.Fireball.MaxSpeed);
	}
}
