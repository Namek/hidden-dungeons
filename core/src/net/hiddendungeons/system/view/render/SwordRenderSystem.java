package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.PlayerSword;
import net.hiddendungeons.component.object.PlayerSword.SwordState;
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

/**
 * Positions and rotates the sword depending on it's state.
 *
 * @author Namek
 */
public class SwordRenderSystem extends EntityProcessingSystem {
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<PlayerSword> mPlayerSword;
	ComponentMapper<Transform> mTransform;
	ComponentMapper<Player> mPlayer;

	TagManager tags;

	private final Vector3 tmpVect3 = new Vector3();
	private final Vector3 right = new Vector3();


	public SwordRenderSystem() {
		super(Aspect.all(PlayerSword.class, Transform.class));
	}

	@Override
	protected void process(Entity entity) {
		PlayerSword sword = mPlayerSword.get(entity);
		Transform transform = mTransform.get(entity);

		Entity playerEntity = tags.getEntity(Tags.Player);
		Player player = mPlayer.get(playerEntity);
		Transform playerTransform = mTransform.get(playerEntity);

		// Position
		transform.desiredPos
			.set(playerTransform.desiredPos)
			.add(playerTransform.displacement) //totally reduce head bobbing effect on hand
			.add(/*-Constants.Sword.HorzDistanceFromCenter*/0, player.eyeAltitude, 0)
			.add(tmpVect3.set(playerTransform.direction).limit(Constants.Sword.DistanceFromEye));

		// Rotate when attacking
		right.set(playerTransform.direction).crs(playerTransform.up);
		float pitch = Constants.Sword.RotationPitchMin;

		if (sword.state == SwordState.Attack) {
			boolean isForward = sword.attack.getCurrentActionIndex() == 0;
			float progress = sword.attack.getCurrentActionProgress();
			if (!isForward) {
				progress = 1f - progress;
			}
			pitch = MathUtils.lerp(Constants.Sword.RotationPitchMin, Constants.Sword.RotationPitchMax, progress);
		}

		Vector3 dir = tmpVect3.set(playerTransform.direction).rotate(right, pitch)
			.scl(-1f); //invert because it's decal, needs to look into the camera
		transform.look(dir);
	}
}
