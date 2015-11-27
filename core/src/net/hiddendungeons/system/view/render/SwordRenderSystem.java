package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.LeftHand.SwordState;
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
	ComponentMapper<LeftHand> mLeftHand;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Player> mPlayer;

	TagManager tags;

	private final Vector3 tmpVect3 = new Vector3();
	private final Vector3 right = new Vector3();


	public SwordRenderSystem() {
		super(Aspect.all(LeftHand.class, Transform.class));
	}

	@Override
	protected void process(Entity entity) {
		LeftHand hand = mLeftHand.get(entity);
		Transform transform = mTransform.get(entity);

		Entity playerEntity = tags.getEntity(Tags.Player);
		Player player = mPlayer.get(playerEntity);
		Transform playerTransform = mTransform.get(playerEntity);

		// Position
		transform.desiredPos
			.set(playerTransform.desiredPos)
			.add(playerTransform.displacement) //totally reduce head bobbing effect on hand
			.add(0, player.eyeAltitude, 0)
			.add(tmpVect3.set(playerTransform.direction).limit(Constants.LeftHand.DistanceFromEye));

		// Rotate when attacking
		right.set(playerTransform.direction).crs(playerTransform.up);
		float rotation = Constants.LeftHand.RotationMin;

		if (hand.state == SwordState.Attack) {
			boolean isForward = hand.attack.getCurrentActionIndex() == 0;
			float progress = hand.attack.getCurrentActionProgress();
			if (!isForward) {
				progress = 1f - progress;
			}
			rotation = MathUtils.lerp(Constants.LeftHand.RotationMin, Constants.LeftHand.RotationMax, progress);
		}

		Vector3 dir = tmpVect3.set(playerTransform.direction).rotate(right, rotation)
			.scl(-1f); //invert because it's decal, needs to look into the camera
		transform.look(dir);
	}
}
