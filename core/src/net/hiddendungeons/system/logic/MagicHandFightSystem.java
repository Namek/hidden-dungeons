package net.hiddendungeons.system.logic;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.EnergyBall;
import net.hiddendungeons.component.object.Growable;
import net.hiddendungeons.component.object.MagicHand;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Constants.MagicHand.MagicType;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.EntityFactorySystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

@Wire
public class MagicHandFightSystem extends EntityProcessingSystem {
	M<MagicHand> mHand;
	M<EnergyBall> mFireball;
	M<Growable> mGrowable;
	M<Transform> mTransform;

	TagManager tagManager;
	EntityFactorySystem entityFactory;
	EnergyBallStateSystem energyBallSystem;


	final Vector3 tmp = new Vector3(), up = new Vector3();


	public MagicHandFightSystem() {
		super(Aspect.all(MagicHand.class));
	}

	@Override
	protected void process(Entity e) {
		MagicHand hand = mHand.get(e);

		if (hand.energyBallId == 0) {
			hand.energyBallId = entityFactory.createEnergyBall(hand.magicType).getId();
		}

		Entity energyBall = world.getEntity(hand.energyBallId);
		Growable grow = mGrowable.get(energyBall);

		float growDuration = 0.1f;

		if (hand.magicType == MagicType.Fire) {
			growDuration = Constants.MagicHand.FireGrowDuration;
		}
		else if (hand.magicType == MagicType.Ice) {
			growDuration = Constants.MagicHand.IceGrowDuration;
		}

		if (grow.percent < 1 && grow.up) {
			grow.percent += (world.delta / growDuration);
		}

		// can we fire this thing?
		if (hand.wishToAttack) {
			if (grow.percent < 1) {
				// prevent auto throw after it grows up
				hand.wishToAttack = false;
			}
			else {
				// TODO we should animate throw animation before creating a new ball
				energyBallSystem.throwBall(energyBall);
				hand.energyBallId = 0;

				// now create a new ball!
				energyBall = entityFactory.createEnergyBall(hand.magicType);
				hand.energyBallId = energyBall.getId();
			}
		}

		setPositionInFrontOfPlayer(energyBall);
	}

	private void setPositionInFrontOfPlayer(Entity e) {
		Entity playerEntity = tagManager.getEntity(Tags.Player);
		Transform playerTransform = playerEntity.getComponent(Transform.class);
		Player player = playerEntity.getComponent(Player.class);
		Transform transform = mTransform.get(e);

		transform.toUpDir(up);

		transform.desiredPos.set(playerTransform.desiredPos)
			.add(playerTransform.displacement)
			.add(0, player.eyeAltitude, 0)
			.add(playerTransform.toDirection(tmp).limit(0.4f))
			.add(tmp.crs(up).limit(0.15f))
			.add(tmp.set(up).scl(-1f).limit(0.1f));
	}
}
