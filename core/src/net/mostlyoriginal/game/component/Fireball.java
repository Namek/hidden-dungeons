package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class Fireball extends Component {
	public float radius;
	public FireballState state = FireballState.nothing;
	public final Color color = Color.WHITE;
	public float minRadius = 2.0f;
	public float maxRadius = 4.0f;
	public float tickIncrement = 0.2f;
}
