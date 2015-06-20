package net.hiddendungeons.system.view.render;

import net.hiddendungeons.component.render.CustomShader;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.ModelSetComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.system.view.render.renderers.DecalRenderer;
import net.hiddendungeons.system.view.render.renderers.SpriteRenderer;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 * Basic renderer supporting layers and different renderers.
 *
 * @author Namek
 * @see Renderable
 */
@Wire(injectInherited=true)
public class RenderSystem extends RenderBatchingSystem {
	ComponentMapper<DecalComponent> mDecal;
	ComponentMapper<ModelSetComponent> mModelSet;
	ComponentMapper<Renderable> mRenderable;
	ComponentMapper<CustomShader> mCustomShader;

	DecalBatch decalBatch;
	SpriteBatch spriteBatch;
	ModelBatch modelBatch;
	public PerspectiveCamera camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	DecalRenderer decalRenderer;
	SpriteRenderer spriteRenderer;
	ModelRenderer modelRenderer;
	Environment environment;
	public DefaultShaderProvider shaderProvider;


	@Override
	protected void initialize() {
		decalRenderer = new DecalRenderer(world, decalBatch);
		spriteRenderer = new SpriteRenderer(world, spriteBatch);
		modelRenderer = new ModelRenderer();

		camera.near = 0.1f;
		camera.far = 300f;

		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		spriteBatch = new SpriteBatch();

		Config shaderConfig = new Config();
		shaderConfig.defaultCullFace = 0;
		shaderProvider = new DefaultShaderProvider(shaderConfig);
		modelBatch = new ModelBatch(shaderProvider);

		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        
	}

	public void registerToDecalRenderer(Entity entity) {
		registerAgent(entity, decalRenderer);
	}

	public void registerToSpriteRenderer(Entity entity) {
		registerAgent(entity, spriteRenderer);
	}

	public void registerToModelRenderer(Entity entity) {
		registerAgent(entity, modelRenderer);
	}

	@Override
	protected void processSystem() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		camera.update();

		super.processSystem();
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
				CustomShader shader = mCustomShader.get(e);

				for (int i = 0; i < models.instances.length; ++i) {
					RenderableProvider model = models.instances[i];

					if (shader == null) {
						modelBatch.render(model, environment);
					}
					else {						
						modelBatch.render(model, environment, shader.shader);
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
