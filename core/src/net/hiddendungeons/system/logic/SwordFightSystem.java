package net.hiddendungeons.system.logic;

import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.LeftHand.SwordState;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.system.base.TimeSystem;
import net.hiddendungeons.util.ActionTimer.TimerState;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class SwordFightSystem extends EntityProcessingSystem {
	ComponentMapper<LeftHand> mLeftHand;

	TimeSystem timeSystem;

	public SwordFightSystem() {
		super(Aspect.all(LeftHand.class));
	}

	@Override
	protected void process(Entity entity) {
		LeftHand hand = mLeftHand.get(entity);
		float deltaTime = timeSystem.getDeltaTime();

		if (hand.state != SwordState.Idle) {
			hand.wishToAttack = false;
		}

		if (hand.state == SwordState.Idle) {
			if (hand.wishToAttack) {
				hand.state = SwordState.Attack;
				hand.attack.start();
			}
		}
		else if (hand.state == SwordState.Attack) {
			if (hand.attack.update(deltaTime) == TimerState.JustStopped) {
				hand.state = SwordState.Cooldown;
				hand.cooldown.start(hand.lastHitCollided
					? Constants.LeftHand.HitCooldown
					: Constants.LeftHand.MissCooldown
				);
				hand.lastHitCollided = false;
			}
		}
		else if (hand.state == SwordState.Cooldown) {
			if (hand.cooldown.update(deltaTime) == TimerState.JustStopped) {
				hand.state = SwordState.Idle;
			}
		}
	}
}
