package net.hiddendungeons.component.object;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import net.hiddendungeons.enums.Constants;

public class Fireball extends Component {
	public enum FireballState { pulsing_up, pulsing_down, throwing, throwed, nothing }
	
	public float radius = Constants.Fireball.MaxRadius;
	public FireballState state = FireballState.nothing;
	public final Color color = Color.WHITE;
	public float minRadius = Constants.Fireball.MinRadius;
	public float maxRadius = Constants.Fireball.MaxRadius;
	public float tickIncrement = Constants.Fireball.TickRadiusIncrement;
}
