package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.ModelSetComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.Shaders;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;
import net.hiddendungeons.system.view.render.renderers.DecalRenderer;
import net.hiddendungeons.system.view.render.renderers.SpriteRenderer;
import net.hiddendungeons.util.DefaultShaderWatchableProvider;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Basic renderer supporting layers and different renderers.
 *
 * @author Namek
 * @see Renderable
 */
@Wire(injectInherited=true)
public class RenderSystem extends RenderBatchingSystem {
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<Dimensions> mDimensions;
	ComponentMapper<ModelSetComponent> mModelSet;
	ComponentMapper<Renderable> mRenderable;
	ComponentMapper<Shaders> mShaders;

	ComponentMapper<Transform> mTransform;
	ComponentMapper<Player> mPlayer;

	TagManager tagManager;

	/** Render bounding boxes for collision based on Dimensions and Transform components. */
	public boolean enableDebugBoundingBoxes = false;
	ModelInstance debugBoundingBox;

	DecalBatch decalBatch;
	SpriteBatch spriteBatch;
	ModelBatch modelBatch;
	public PerspectiveCamera camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	DecalRenderer decalRenderer;
	SpriteRenderer spriteRenderer;
	ModelRenderer modelRenderer;
	
	public DefaultShaderWatchableProvider shaderProvider;
	Environment environment;
	DirectionalLight directionalLight;


	@Override
	protected void initialize() {
		super.initialize();

		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		spriteBatch = new SpriteBatch();

		decalRenderer = new DecalRenderer(world, decalBatch);
		spriteRenderer = new SpriteRenderer(world, spriteBatch);
		modelRenderer = new ModelRenderer();

		camera.near = 0.1f;
		camera.far = 300f;

		Config shaderConfig = new Config();
		shaderConfig.defaultCullFace = 0;

		shaderProvider = new DefaultShaderWatchableProvider(
			shaderConfig,
			Gdx.files.internal("shaders/basic.vertex.glsl"),
			Gdx.files.internal("shaders/basic.fragment.glsl")
		);
		modelBatch = new ModelBatch(shaderProvider);

		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0, 0, 0, 1f));
        directionalLight = new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f);
        environment.add(directionalLight);

        createDebugBoundingBox();
	}

	public void registerToDecalRenderer(Entity entity) {
		registerAgent(entity, decalRenderer);
	}

	public void unregisterToDecalRenderer(Entity entity) {
		unregisterAgent(entity, decalRenderer);
	}

	public void registerToSpriteRenderer(Entity entity) {
		registerAgent(entity, spriteRenderer);
	}

	public void unregisterToSpriteRenderer(Entity entity) {
		unregisterAgent(entity, spriteRenderer);
	}

	public void registerToModelRenderer(Entity entity) {
		registerAgent(entity, modelRenderer);
	}

	public void unregisterToModelRenderer(Entity entity) {
		unregisterAgent(entity, modelRenderer);
	}

	@Override
	protected void processSystem() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		Entity entity = tagManager.getEntity(Tags.PLAYER);
		Transform transform = mTransform.get(entity);
		Player player = mPlayer.get(entity);

		camera.position
			.set(transform.currentPos)
			.add(transform.displacement)
			.add(0, player.eyeAltitude, 0);

		camera.direction.set(transform.direction);
		camera.up.set(transform.up);

		camera.update();

		if (Gdx.input.isKeyJustPressed(Keys.B)) {
			enableDebugBoundingBoxes = !enableDebugBoundingBoxes;
		}

		super.processSystem();
		shaderProvider.updateAll(world.getDelta());
	}

	@Override
	protected void processByAgent(EntityProcessAgent agent, Entity entity) {
		super.processByAgent(agent, entity);

		// Draw debug bounding box
		if (enableDebugBoundingBoxes) {
			Transform transform = mTransform.get(entity);
			Dimensions dimensions = mDimensions.get(entity);

			if (dimensions == null || transform == null) {
				return;
			}

			final Matrix4 trans = debugBoundingBox.transform;
			final Vector3 dims = dimensions.dimensions;

			trans.setToLookAt(transform.direction, transform.up);
			trans.translate(transform.currentPos);
			trans.scale(dims.x, dims.y, dims.z);

			modelBatch.render(debugBoundingBox, environment);
		}
	}

	private void createDebugBoundingBox() {
		ModelBuilder builder = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(1, 0, 1, 1));
		Model model = builder.createBox(1, 1, 1, GL20.GL_LINES, material, Usage.Position | Usage.ColorUnpacked);

		debugBoundingBox = new ModelInstance(model);
	}

	private class ModelRenderer implements EntityProcessAgent {
		@Override
		public void begin() {
			modelBatch.begin(camera);
		}

		@Override
		public void process(Entity e) {
			ModelSetComponent models = mModelSet.get(e);

			if (models != null) {
				Shaders shaders = mShaders.get(e);

				for (int i = 0; i < models.instances.length; ++i) {
					RenderableProvider model = models.instances[i];

					if (shaders == null) {
						modelBatch.render(model, environment);
					}
					else {
						if (shaders.useDefaultShader) {
							modelBatch.render(model, environment);
						}
						
						for (int j = 0, n = shaders.shaders.length; j < n; ++j) {
							modelBatch.render(model, environment, shaders.shaders[j]);
						}
					}
				}
			}
		}

		@Override
		public void end() {
			modelBatch.end();
		}

		@Override
		public int getType() {
			return Renderable.MODEL;
		}
	}
}
