package net.hiddendungeons.util;

/**
 * Wrapper over a sequenced {@link ActionTimer}s of various time spans.
 *
 * @author Namek
 */
public class ActionSequenceTimer extends ActionTimer {
	protected ActionTimer[] timers;
	protected int currentTimer = -1;


	public ActionSequenceTimer(float... stateDurations) {
		timers = new ActionTimer[stateDurations.length];

		this.duration = 0;
		for (int i = 0; i < stateDurations.length; ++i) {
			float duration = stateDurations[i];
			this.duration += duration;
			timers[i] = new ActionTimer(duration);
		}
	}

	public TimerState update(float deltaTime) {
		float leftDeltaTime = deltaTime;

		while (state == TimerState.Active && leftDeltaTime > 0) {
			ActionTimer timer = timers[currentTimer];

			float timeToReduce = timer.timeLeft;

			if (timer.update(leftDeltaTime) == TimerState.JustStopped) {
				// reduce total by taken time
				leftDeltaTime -= timeToReduce;

				// switch to next timer
				timer.stop();
				++currentTimer;

				// Oh, there are no more timers?
				if (currentTimer >= timers.length) {
					stop();
					return TimerState.JustStopped;
				}

				timer = timers[currentTimer];
				timer.start();
			}
			else {
				// Current timer ate whole deltaTime and didn't stop, so we're not
				// not switching to another timer, just stopping simulation here.
				break;
			}
		}

		timeElapsed += deltaTime;
		timeLeft = duration - timeElapsed;
		progress = timeElapsed / duration;

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
		throw new RuntimeException("This method does not make any sense in context of action sequence where duration is a sum of subactions' durations.");
	}

	@Override
	public void stop() {
		super.stop();
		currentTimer = -1;
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