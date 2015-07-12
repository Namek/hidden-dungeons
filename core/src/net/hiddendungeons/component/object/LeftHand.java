package net.hiddendungeons.component.object;

import net.hiddendungeons.enums.Constants;

import com.artemis.Component;

public class LeftHand extends Component {
	public enum SwordState { hitting, nothing }
	
	public float rotation = 0.0f;
	public float distance = Constants.LeftHand.DecalStartZ;
	public float dir = 1.0f;
	public SwordState state = SwordState.nothing;
}
