package net.hiddendungeons.builders;

import static net.hiddendungeons.builders.MapBuilder.FAR;
import static net.hiddendungeons.builders.MapBuilder.LEFT;
import static net.hiddendungeons.builders.MapBuilder.NEAR;
import static net.hiddendungeons.builders.MapBuilder.RIGHT;
import net.hiddendungeons.component.render.ModelSetComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.enums.RenderLayers;
import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;

public class MapBuilder {
	public final static int NONE = 0, NEAR = 1, FAR = 2, LEFT = 4, RIGHT = 8, FLOOR = 16, CEIL = 32;

	private World world;
	private RenderSystem renderSystem;

	Texture wallTexture, floorTexture, ceilingTexture;
	final short[] wallMeshIndices = new short[] { 0, 1, 2, 3 };

	final Vector3 rightDir = new Vector3(), leftDir = new Vector3();
	final Vector3 nearLeft = new Vector3();
	final Vector3 nearRight = new Vector3();
	final Vector3 backwardLeft = new Vector3();
	final Vector3 forwardDir = new Vector3();
	final Vector3 backwardDir = new Vector3();
	final Vector3 farLeft = new Vector3();
	final Vector3 farRight = new Vector3();
	final Vector3 forwardRight = new Vector3();

	final Vector3 tmp1 = new Vector3(), tmp2 = new Vector3();

	public final Vector3 bboxMin = new Vector3();
	public final Vector3 bboxMax = new Vector3();


	public MapBuilder(World world, RenderSystem renderSystem) {
		this.world = world;
		this.renderSystem = renderSystem;
		init();
	}

	private void init() {
		wallTexture = new Texture("graphics/wall.jpg");
		floorTexture = new Texture("graphics/floor.jpg");
		ceilingTexture = new Texture("graphics/ceiling.jpg");

		Texture[] textures = { wallTexture, floorTexture, ceilingTexture };

		for (Texture t : textures) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			t.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		}

		bboxMin.set(1, 1, 1).scl(Float.MAX_VALUE);
		bboxMax.set(1, 1, 1).scl(Float.MIN_VALUE);
	}

	/**
	 * <code>start</code> is the nearest bottom corner.
	 */
	public MapBuilder createCorridor(Vector3 start, Vector3 dir, float length, float width, float height) {
		return createCorridor(start, dir, length, width, height, LEFT|RIGHT);
	}

	public MapBuilder createCorridor(Vector3 start, Vector3 dir, float length, float width, float height, int wallBitSet) {
		return createCorridor(start, dir, length, width, height, wallBitSet, FLOOR|CEIL);
	}

	public MapBuilder createCorridor(Vector3 start, Vector3 dir, float length, float width, float height, int wallBitSet, int horizontalsBitSet) {
		nearLeft.set(start);
		forwardDir.set(dir).nor();
		backwardDir.set(forwardDir).scl(-1);
		rightDir.set(dir).rotate(-90, 0, 1, 0).setLength(width);
		leftDir.set(dir).rotate(90, 0, 1, 0).setLength(width);
		nearRight.set(nearLeft).add(rightDir);
		farLeft.set(nearLeft).mulAdd(forwardDir, length);
		farRight.set(nearRight).mulAdd(forwardDir, length);

		cb(nearLeft);
		cb(tmp1.set(nearLeft).add(0, height, 0));
		cb(nearRight);
		cb(tmp1.set(nearRight).add(0, height, 0));
		cb(farLeft);
		cb(tmp1.set(farLeft).add(0, height, 0));
		cb(farRight);
		cb(tmp1.set(farRight).add(0, height, 0));

		float wallUCoordMax = length / height;
		float wallUCoordStart = wallUCoordMax;
		float wallUCoordEnd = -wallUCoordMax;
		float floorUCoordMax = length / width;

		int rectCount =
			+ ((wallBitSet & LEFT) != 0 ? 1 : 0)
			+ ((wallBitSet & RIGHT) != 0 ? 1 : 0)
			+ ((wallBitSet & NEAR) != 0 ? 1 : 0)
			+ ((wallBitSet & FAR) != 0 ? 1 : 0)
			+ ((horizontalsBitSet & FLOOR) != 0 ? 1 : 0)
			+ ((horizontalsBitSet & CEIL) != 0 ? 1 : 0);

		ModelInstance[] instances = new ModelInstance[rectCount];

		ModelBuilder builder = new ModelBuilder();
		int rectIndex = 0;

		// Left wall
		if ((wallBitSet & LEFT) != 0) {
			tmp1.set(nearLeft).add(0, height, 0);
			tmp2.set(farLeft).add(0, height, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(wallTexture))
			).rect(
				new VertexInfo().setPos(nearLeft).setUV(wallUCoordStart, 0).setNor(rightDir),
				new VertexInfo().setPos(farLeft).setUV(wallUCoordEnd, 0).setNor(rightDir),
				new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(rightDir),
				new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(rightDir)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}

		// Right wall
		if ((wallBitSet & RIGHT) != 0) {
			tmp1.set(nearRight).add(0, height, 0);
			tmp2.set(farRight).add(0, height, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(wallTexture))
			).rect(
				new VertexInfo().setPos(nearRight).setUV(wallUCoordStart, 0).setNor(leftDir),
				new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(leftDir),
				new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(leftDir),
				new VertexInfo().setPos(farRight).setUV(wallUCoordEnd, 0).setNor(leftDir)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}

		// Setup before near and far walls
		wallUCoordMax = width / height;
		wallUCoordStart = wallUCoordMax;
		wallUCoordEnd = -wallUCoordMax;

		// Near wall
		if ((wallBitSet & NEAR) != 0) {
			tmp1.set(nearLeft).add(0, height, 0);
			tmp2.set(nearRight).add(0, height, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(wallTexture))
			).rect(
				new VertexInfo().setPos(nearLeft).setUV(wallUCoordStart, 0).setNor(backwardDir),
				new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(backwardDir),
				new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(backwardDir),
				new VertexInfo().setPos(nearRight).setUV(wallUCoordEnd, 0).setNor(backwardDir)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}

		// Far wall
		if ((wallBitSet & FAR) != 0) {
			tmp1.set(farLeft).add(0, height, 0);
			tmp2.set(farRight).add(0, height, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(wallTexture))
			).rect(
				new VertexInfo().setPos(farLeft).setUV(wallUCoordStart, 0).setNor(forwardDir),
				new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(forwardDir),
				new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(backwardDir),
				new VertexInfo().setPos(farRight).setUV(wallUCoordEnd, 0).setNor(forwardDir)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}

		// Floor
		if ((horizontalsBitSet & FLOOR) != 0) {
			tmp1.set(nearLeft).mulAdd(forwardDir, length);
			Vector3 up = tmp2.set(0, 1, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(floorTexture))
			).rect(
				new VertexInfo().setPos(nearLeft).setUV(0, 0).setNor(up),
				new VertexInfo().setPos(nearRight).setUV(0, 1).setNor(up),
				new VertexInfo().setPos(farRight).setUV(floorUCoordMax, 1).setNor(up),
				new VertexInfo().setPos(tmp1).setUV(floorUCoordMax, 0).setNor(up)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}

		// Ceil
		if ((horizontalsBitSet & CEIL) != 0) {
			nearLeft.add(0, height, 0);
			tmp1.add(0, height, 0);
			farRight.add(0, height, 0);
			nearRight.add(0, height, 0);
			Vector3 down = tmp2.set(0, -1, 0);

			builder.begin();
			builder.part("rect", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates,
				new Material(TextureAttribute.createDiffuse(ceilingTexture))
			).rect(
				new VertexInfo().setPos(nearLeft).setUV(0, 0).setNor(down),
				new VertexInfo().setPos(tmp1).setUV(floorUCoordMax, 0).setNor(down),
				new VertexInfo().setPos(farRight).setUV(floorUCoordMax, 1).setNor(down),
				new VertexInfo().setPos(nearRight).setUV(0, 1).setNor(down)
			);
			instances[rectIndex++] = new ModelInstance(builder.end());
		}


		// Create entity
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();

		ModelSetComponent models = edit.create(ModelSetComponent.class);
		models.instances = instances;

		Renderable renderable = edit.create(Renderable.class)
			.renderer(Renderable.MODEL)
			.layer(RenderLayers.WORLD);

		// TODO set layer for renderable?

		return this;
	}

	private void cb(Vector3 point) {
		bboxMin.x = Math.min(bboxMin.x, point.x);
		bboxMin.y = Math.min(bboxMin.y, point.y);
		bboxMin.z = Math.min(bboxMin.z, point.z);
		bboxMax.x = Math.max(bboxMax.x, point.x);
		bboxMax.y = Math.max(bboxMax.y, point.y);
		bboxMax.z = Math.max(bboxMax.z, point.z);
	}
}
