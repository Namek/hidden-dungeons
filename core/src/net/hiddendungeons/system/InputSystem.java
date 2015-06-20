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
	boolean debugCamEnabled = true;

	
	@Override
	protected void initialize() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		debugCamController = new CameraInputController(renderSystem.camera);
		debugCamController.rotateAngle = -180;
		inputMultiplexer.addProcessor(debugCamController);
	}

	@Override
	protected void processSystem() {
		// Toggle debug camera
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			debugCamEnabled = !debugCamEnabled;
			
			inputMultiplexer.removeProcessor(debugCamController);
			if (debugCamEnabled) {
				inputMultiplexer.addProcessor(debugCamController);
			}
		}
		
		if (debugCamEnabled) {
			debugCamController.update();
		}
	}

}
