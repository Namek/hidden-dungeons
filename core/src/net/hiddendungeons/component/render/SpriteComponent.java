package net.hiddendungeons.component.render;

import com.artemis.Component;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent extends Component {
	public Sprite sprite;
	public int blendSrcFunc = GL20.GL_SRC_ALPHA;
	public int blendDestFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;


	public SpriteComponent blendFunc(int srcFunc, int destFunc) {
		this.blendSrcFunc = srcFunc;
		this.blendDestFunc = destFunc;

		return this;
	}

	public SpriteComponent setup(Sprite sprite) {
		this.sprite = sprite;
		return this;
	}

	public SpriteComponent setup(Sprite sprite, int srcFunc, int destFunc) {
		this.sprite = sprite;
		blendFunc(srcFunc, destFunc);

		return this;
	}
}
