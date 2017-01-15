package net.hiddendungeons.component.object;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import net.hiddendungeons.enums.Constants.MagicHand.MagicType;

public class MagicHand extends Component {
	public boolean wishToAttack = false;
	public MagicType magicType;

	@EntityId
	public int energyBallId;
}
