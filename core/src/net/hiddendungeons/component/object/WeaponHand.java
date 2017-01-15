package net.hiddendungeons.component.object;

import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.util.ActionSequenceTimer;
import net.hiddendungeons.util.ActionTimer;

import com.artemis.Component;

public class WeaponHand extends Component {
	public enum SwordState {
		Attack,
		Cooldown,
		Idle
	}

	public SwordState state = SwordState.Idle;

	public boolean wishToAttack = false;
	public boolean lastHitCollided;

	public ActionSequenceTimer attack = new ActionSequenceTimer(
		Constants.WeaponHand.ForwardHitDuration,
		Constants.WeaponHand.BackwardHitDuration
	);
	public ActionTimer cooldown = new ActionTimer();

}
