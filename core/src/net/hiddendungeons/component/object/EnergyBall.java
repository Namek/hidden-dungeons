package net.hiddendungeons.component.object;

import static com.badlogic.gdx.math.MathUtils.PI2;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Constants.MagicHand.MagicType;
import net.hiddendungeons.util.PulseTimer;

public class EnergyBall extends Component {
	public MagicType type;
	public float radius = Constants.Fireball.PulseMaxRadius;
	public PulseTimer pulse = new PulseTimer(2.0f, 0, PI2);
}
