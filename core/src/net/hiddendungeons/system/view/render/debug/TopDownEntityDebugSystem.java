package net.hiddendungeons.system.view.render.debug;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.LeftHand;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Renders top-down map containing all entities that have {@link Transform} component.
 * @author Namek
 *
 */
@Wire
public class TopDownEntityDebugSystem extends EntitySystem {
	ComponentMapper<Dimensions> mDimensions;
	ComponentMapper<Enemy> mEnemy;
	ComponentMapper<Fireball> mFireball;
	ComponentMapper<LeftHand> mLeftHand;
	ComponentMapper<Transform> mTransform;

	Entity flyweight;
	
	final static float PADDING_PERCENT = 0.05f;
	final static float DEFAULT_CIRCLE_RADIUS = 4f;

	// rendering modes
	final static int RENDERING_DISABLED = 1;
	final static int RENDERING_SMALL_CIRCLES = 2;
	final static int RENDERING_SCALED_CIRCLES = 4;
	final static int RENDERING_DIMENSIONS = 8;
	public static int RENDERING_MODE_FIRST = RENDERING_DISABLED;
	public static int RENDERING_MODE_LAST = RENDERING_DIMENSIONS;

	ShapeRenderer shapeRenderer;
	final Vector2 tmpPos2d = new Vector2();
	final Vector2 tmpSize2d = new Vector2();
	final Vector2 projection = new Vector2();
	final Vector2 tmp = new Vector2();

	public final Vector2 min = new Vector2();
	public final Vector2 max = new Vector2();

	int renderingMode = RENDERING_DISABLED;


	public TopDownEntityDebugSystem() {
		super(Aspect.all(Transform.class));
	}

	@Override
	protected void initialize() {
		shapeRenderer = new ShapeRenderer();
		flyweight = createFlyweightEntity();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	protected void processSystem() {
		if (Gdx.input.isKeyJustPressed(Keys.SLASH)) {
			if (renderingMode == RENDERING_MODE_LAST) {
				renderingMode = RENDERING_MODE_FIRST;
			}
			else {
				renderingMode <<= 1;
			}
		}

		if (renderingMode == RENDERING_DISABLED) {
			return;
		}

		IntBag actives = subscription.getEntities();
		if (Gdx.input.isKeyJustPressed(Keys.STAR)) {
			findWorldBoundingBox();
		}

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		float padding = PADDING_PERCENT * Math.min(width, height);
		width -= 2*padding;
		height -= 2*padding;

		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.getColor().a = 0.4f;
		shapeRenderer.rect(padding, padding, width, height);
		shapeRenderer.end();

		projection.set(1f / (max.x - min.x), 1f / (max.y - min.y)).scl(width, height);
		shapeRenderer.begin(ShapeType.Filled);

		for (int i = 0, n = actives.size(); i < n; ++i) {
			int entityId = actives.get(i);

			final Dimensions dims = mDimensions.get(entityId);
			final Transform transform = mTransform.get(entityId);
			final Vector3 pos = transform.currentPos;
			final Vector3 dir = transform.direction;

			tmpPos2d.set(pos.x, pos.z).sub(min).scl(projection).add(padding, padding);

			Color color = Color.WHITE;
			if (mEnemy.has(entityId)) {
				color = Color.RED;
			}
			else if (mFireball.has(entityId)) {
				color = Color.ORANGE;
			}
			else if (mLeftHand.has(flyweight)) {
				color = Color.PINK;
			}

			shapeRenderer.setColor(color);

			if (dims != null) {
				tmpSize2d.set(dims.getWidth(), dims.getDepth()).scl(projection);
			}

			if ((renderingMode & (RENDERING_SMALL_CIRCLES|RENDERING_SCALED_CIRCLES)) != 0 || dims == null) {
				float radius = renderingMode == RENDERING_SCALED_CIRCLES ? (tmpSize2d.x+tmpSize2d.y)/2 : DEFAULT_CIRCLE_RADIUS;

				shapeRenderer.circle(tmpPos2d.x, tmpPos2d.y, radius);
			}
			else {
				float rotation = -MathUtils.atan2(dir.z, dir.x) * MathUtils.radiansToDegrees;

				shapeRenderer.rect(
					tmpPos2d.x - tmpSize2d.x/2, tmpPos2d.y - tmpSize2d.y/2,
					tmpSize2d.x/2, tmpSize2d.y/2, tmpSize2d.x, tmpSize2d.y, 1, 1, rotation
				);
			}
		}

		shapeRenderer.end();
	}

	private void findWorldBoundingBox() {
		final IntBag actives = subscription.getEntities();

		if (actives.size() < 2) {
			return;
		}

		min.set(1, 1).scl(Float.MAX_VALUE);
		max.set(1, 1).scl(Float.MIN_VALUE);

		for (int i = 0, n = actives.size(); i < n; ++i) {
			int entityId = actives.get(i);

			final Dimensions dims = mDimensions.get(entityId);
			final Transform transform = mTransform.get(entityId);
			final Vector3 pos = transform.currentPos;

			if (pos.x < min.x) min.x = pos.x;
			if (pos.z < min.y) min.y = pos.z;
			if (pos.x > max.x) max.x = pos.x;
			if (pos.z > max.y) max.y = pos.z;
		}

		// Convert ratio to screen ratio
		float localWidth = max.x - min.x;
		float localHeight = max.y - min.y;
		float foundRatio = localWidth / localHeight;

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		float wantedRatio = width / height;
		float scale = 1f / foundRatio * wantedRatio;

		if (foundRatio < 1) {
			float newLocalWidth = localWidth * scale;
			min.x = (max.x - min.x)/2 - newLocalWidth/2;
			max.x = (max.x - min.x)/2 + newLocalWidth/2;
		}
		else {
			float newLocalHeight = localHeight * scale;
			min.y = (max.y - min.y)/2 - newLocalHeight/2;
			max.y = (max.y - min.y)/2 + newLocalHeight/2;
		}
	}

}
