package net.hiddendungeons.util;


/**
 * Wrapper over a sequence of {@link ActionTimer}s.
 *
 * @author Namek
 */
public class ActionSequenceTimer extends ActionTimer {
	protected ActionTimer[] timers;
	protected int currentTimer = -1;


	public ActionSequenceTimer(float... stateDurations) {
		timers = new ActionTimer[stateDurations.length];

		for (int i = 0; i < stateDurations.length; ++i) {
			float duration = stateDurations[i];
			this.duration += duration;
			timers[i] = new ActionTimer(duration);
		}
	}

	public TimerState update(float deltaTime) {
		if (doingAction) {
			timeElapsed += deltaTime;
			timeLeft -= deltaTime;
			progress = timeElapsed / duration;

			ActionTimer timer = timers[currentTimer];
			float left = timer.timeLeft - deltaTime;

			if (left >= 0) {
				timer.update(deltaTime);
			}
			else {
				// Switch to next timer
				timer.stop();
				++currentTimer;

				// Oh, there is no next timer?
				if (currentTimer >= timers.length) {
					doingAction = false;
					timeElapsed = 0;
					progress = 0;
					state = TimerState.Idle;
					currentTimer = -1;

					return TimerState.JustStopped;
				}
				else {
					timer = timers[currentTimer];
					timer.start();
					timer.update(-left);
				}
			}
		}
		else {
			state = TimerState.Idle;
		}

		return state;
	}

	@Override
	public void start() {
		currentTimer = 0;
		super.start();
		timers[currentTimer].start();
	}

	@Override
	public void start(float duration) {
		throw new RuntimeException("This method does not make any sense in context of action sequence.");
	}

	public int getCurrentActionIndex() {
		return currentTimer;
	}

	public float getCurrentActionProgress() {
		if (currentTimer >= timers.length) {
			return 1f;
		}
		else if (currentTimer < 0) {
			return 0f;
		}

		ActionTimer timer = timers[currentTimer];
		return timer.progress;
	}
}