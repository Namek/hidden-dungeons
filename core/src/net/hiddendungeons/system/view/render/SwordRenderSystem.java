package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.WeaponHand;
import net.hiddendungeons.component.object.WeaponHand.SwordState;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class SwordRenderSystem extends EntityProcessingSystem {
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<WeaponHand> mLeftHand;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Player> mPlayer;

	TagManager tags;

	private final Vector3 tmpVect3 = new Vector3();
	private final Vector3 right = new Vector3();


	public SwordRenderSystem() {
		super(Aspect.all(WeaponHand.class, Transform.class));
	}

	@Override
	protected void process(Entity entity) {
		WeaponHand hand = mLeftHand.get(entity);
		Transform transform = mTransform.get(entity);

		Entity playerEntity = tags.getEntity(Tags.Player);
		Player player = mPlayer.get(playerEntity);
		Transform playerTransform = mTransform.get(playerEntity);

		// Position
		transform.desiredPos
			.set(playerTransform.desiredPos)
			.add(playerTransform.displacement) //totally reduce head bobbing effect on hand
			.add(0, player.eyeAltitude, 0)
			.add(playerTransform.toDirection(tmpVect3).limit(Constants.WeaponHand.DistanceFromEye));

		// Rotate when attacking
		playerTransform.toRightDir(right);
		float pitch = Constants.WeaponHand.RotationPitchMin;

		if (hand.state == SwordState.Attack) {
			boolean isForward = hand.attack.getCurrentActionIndex() == 0;
			float progress = hand.attack.getCurrentActionProgress();
			if (!isForward) {
				progress = 1f - progress;
			}
			pitch = MathUtils.lerp(Constants.WeaponHand.RotationPitchMin, Constants.WeaponHand.RotationPitchMax, progress);
		}

		Vector3 dir = playerTransform.toDirection(tmpVect3).rotate(right, pitch);
		transform.look(dir);
	}
}
