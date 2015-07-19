package net.hiddendungeons.component.render;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.Shader;

public class Shaders extends Component {
	/** Collection of shaders allowing for multi-pass. */
	public Shader[] shaders;
	
	/** Should we use default shader or omit it? */
	public boolean useDefaultShader = true;
}
