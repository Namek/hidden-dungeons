package net.hiddendungeons.component.object;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class Fireball extends Component {
	public enum FireballState { pulsing_up, pulsing_down, throwing, throwed, nothing }
	
	public float radius = 0.012f;
	public FireballState state = FireballState.nothing;
	public final Color color = Color.WHITE;
	public float minRadius = 0.01f;
	public float maxRadius = 0.011f;
	public float tickIncrement = 0.0001f;
}
