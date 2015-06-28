package net.hiddendungeons.system;

import net.hiddendungeons.system.view.render.RenderSystem;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

@Wire
public class InputSystem extends BaseSystem {
	RenderSystem renderSystem;

	InputMultiplexer inputMultiplexer;
	CameraInputController debugCamController;
	boolean enableDebugCamera = true;
	private boolean isDebugCamEnabled = false;


	@Override
	protected void initialize() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);

		debugCamController = new CameraInputController(renderSystem.camera);
		debugCamController.rotateAngle = -180;

		Gdx.input.setCursorCatched(true);
	}

	@Override
	protected void processSystem() {
		// Toggle debug camera
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			enableDebugCamera = !enableDebugCamera;
		}

		if (enableDebugCamera && !isDebugCamEnabled) {
			inputMultiplexer.addProcessor(debugCamController);
		}
		else if (!enableDebugCamera && isDebugCamEnabled) {
			inputMultiplexer.removeProcessor(debugCamController);
		}

		if (enableDebugCamera) {
			debugCamController.update();
		}

		if (Gdx.input.isKeyJustPressed(Keys.GRAVE)) {
			Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
		}

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
}
