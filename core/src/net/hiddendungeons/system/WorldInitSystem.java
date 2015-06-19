package net.hiddendungeons.system;

import net.hiddendungeons.component.render.ModelSetComponent;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.component.graphics.Renderable;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

@Wire
public class WorldInitSystem extends BaseSystem {
	RenderSystem renderSystem;

	Texture wallTexture, floorTexture, ceilingTexture;
	final short[] wallMeshIndices = new short[] { 0, 1, 2, 3 };

	final Vector3 rightDir = new Vector3(), leftDir = new Vector3();
	final Vector3 startLeft = new Vector3();
	final Vector3 startRight = new Vector3();
	final Vector3 backwardLeft = new Vector3();
	final Vector3 forwardDir = new Vector3();
	final Vector3 endLeft = new Vector3();
	final Vector3 endRight = new Vector3();
	final Vector3 forwardRight = new Vector3();
	final Vector3 tmp1 = new Vector3(), tmp2 = new Vector3();

	boolean isInitialized = false;

	void _initialize() {
		wallTexture = new Texture("graphics/wall.jpg");
		floorTexture = new Texture("graphics/floor.jpg");
		ceilingTexture = new Texture("graphics/ceiling.jpg");

		Texture[] textures = { wallTexture, floorTexture, ceilingTexture };

		for (Texture t : textures) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			t.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		}

		Vector3 start = new Vector3();
		Vector3 dir = new Vector3();

		float width = 5f, height = 3.5f;
		Vector3 playerPos = new Vector3(width/2, 1.5f, 0);
		Vector3 playerDir = new Vector3(0, 0, -1);

		PerspectiveCamera camera = renderSystem.camera;
		camera.position.set(playerPos);
		camera.direction.set(playerDir);

		createDungeonPart(start.set(0, 0, 0), dir.set(0, 0, -1), 20f, width, height);
		createDungeonPart(start.set(0, 0, -25), dir.set(0, 0, -1), 100f, width, height);
		createDungeonPart(start.set(0, 0, -20), dir.set(-1, 0, 0), 30f, width, height);
		createDungeonPart(start.set(width, 0, -25), dir.set(1, 0, 0), 40f, width, height);
	}

	/**
	 * <code>start</code> is the nearest bottom corner.
	 */
	void createDungeonPart(Vector3 start, Vector3 dir, float length, float width, float height) {
		startLeft.set(start);
		forwardDir.set(dir).setLength(length);
		rightDir.set(dir).rotate(-90, 0, 1, 0).setLength(width);
		leftDir.set(dir).rotate(90, 0, 1, 0).setLength(width);
		startRight.set(startLeft).add(rightDir);
		endLeft.set(startLeft).add(forwardDir);
		endRight.set(startRight).add(forwardDir);

		float wallUCoordMax = length / height;
		float wallUCoordStart = wallUCoordMax;
		float wallUCoordEnd = -wallUCoordMax;
		float floorUCoordMax = length / width;

		ModelInstance[] instances = new ModelInstance[4];

		ModelBuilder builder = new ModelBuilder();

		// Left wall
		tmp1.set(startLeft).add(0, height, 0);
		tmp2.set(endLeft).add(0, height, 0);

		builder.begin();
		builder.part("rect", GL20.GL_TRIANGLES,
			Usage.Position | Usage.Normal | Usage.TextureCoordinates,
			new Material(TextureAttribute.createDiffuse(wallTexture))
		).rect(
			new VertexInfo().setPos(startLeft).setUV(wallUCoordStart, 0).setNor(rightDir),
			new VertexInfo().setPos(endLeft).setUV(wallUCoordEnd, 0).setNor(rightDir),
			new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(rightDir),
			new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(rightDir)
		);
		instances[0] = new ModelInstance(builder.end());

		// Right wall
		tmp1.set(startRight).add(0, height, 0);
		tmp2.set(endRight).add(0, height, 0);

		builder.begin();
		builder.part("rect", GL20.GL_TRIANGLES,
			Usage.Position | Usage.Normal | Usage.TextureCoordinates,
			new Material(TextureAttribute.createDiffuse(wallTexture))
		).rect(
			new VertexInfo().setPos(startRight).setUV(wallUCoordStart, 0).setNor(leftDir),
			new VertexInfo().setPos(tmp1).setUV(wallUCoordStart, 1).setNor(leftDir),
			new VertexInfo().setPos(tmp2).setUV(wallUCoordEnd, 1).setNor(leftDir),
			new VertexInfo().setPos(endRight).setUV(wallUCoordEnd, 0).setNor(leftDir)
		);
		instances[1] = new ModelInstance(builder.end());

		// Floor
		tmp1.set(startLeft).add(forwardDir);
		Vector3 up = tmp2.set(0, 1, 0);

		builder.begin();
		builder.part("rect", GL20.GL_TRIANGLES,
			Usage.Position | Usage.Normal | Usage.TextureCoordinates,
			new Material(TextureAttribute.createDiffuse(floorTexture))
		).rect(
			new VertexInfo().setPos(startLeft).setUV(0, 0).setNor(up),
			new VertexInfo().setPos(tmp1).setUV(floorUCoordMax, 0).setNor(up),
			new VertexInfo().setPos(endRight).setUV(floorUCoordMax, 1).setNor(up),
			new VertexInfo().setPos(startRight).setUV(0, 1).setNor(up)
		);
		instances[2] = new ModelInstance(builder.end());

		// Ceiling
		startLeft.add(0, height, 0);
		tmp1.add(0, height, 0);
		endRight.add(0, height, 0);
		startRight.add(0, height, 0);
		Vector3 down = tmp2.set(0, -1, 0);

		builder.begin();
		builder.part("rect", GL20.GL_TRIANGLES,
			Usage.Position | Usage.Normal | Usage.TextureCoordinates,
			new Material(TextureAttribute.createDiffuse(ceilingTexture))
		).rect(
			new VertexInfo().setPos(startLeft).setUV(0, 0).setNor(down),
			new VertexInfo().setPos(tmp1).setUV(floorUCoordMax, 0).setNor(down),
			new VertexInfo().setPos(endRight).setUV(floorUCoordMax, 1).setNor(down),
			new VertexInfo().setPos(startRight).setUV(0, 1).setNor(down)
		);
		instances[3] = new ModelInstance(builder.end());


		// Create entity
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();

		ModelSetComponent models = edit.create(ModelSetComponent.class);
		models.instances = instances;

		Renderable renderable = edit.create(Renderable.class);
		renderable = entity.getComponent(Renderable.class);

		// TODO set layer for renderable?

		renderSystem.registerToModelRenderer(entity);
	}

	@Override
	protected void processSystem() {
		if (!isInitialized) {
			_initialize();
			isInitialized = true;
		}
	}
}
