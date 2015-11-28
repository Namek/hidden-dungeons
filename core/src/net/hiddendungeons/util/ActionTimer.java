package net.hiddendungeons.util;

/**
 * Timer that simplifies action update.
 *
 * @author Namek
 */
public class ActionTimer {
	public float duration;
	public float timeElapsed;
	public float timeLeft;

	/** value range: [0,1] */
	public float progress = 0;

	public TimerState state;


	public enum TimerState {
		Idle,
		Active,
		JustStopped
	}

	public ActionTimer() {
	}

	public ActionTimer(float maxTime) {
		this.duration = maxTime;
	}

	public TimerState update(float deltaTime) {
		if (state == TimerState.Active) {
			timeElapsed += deltaTime;
			timeLeft = duration - timeElapsed;

			if (timeLeft <= 0) {
				stop();
				return TimerState.JustStopped;
			}
			else {
				progress = timeElapsed / duration;
			}
		}

		return state;
	}

	public boolean isForward(float progressBoundary) {
		return progress < progressBoundary;
	}

	/**
	 *
	 * @param progressBoundary value in range (0, 1)
	 */
	public float getProgressForDirection(float progressBoundary) {
		return progress < progressBoundary
			? progress * progressBoundary
			: (progress - progressBoundary) * progressBoundary;
	}

	public void stop() {
		state = TimerState.Idle;
		timeElapsed = 0;
		timeLeft = 0;
		progress = 0;
	}

	public void start() {
		state = TimerState.Active;
		timeLeft = this.duration;
		timeElapsed = 0;
		progress = 0;
	}

	public void start(float duration) {
		this.duration = duration;
		start();
	}

	public boolean isRunning() {
		return state == TimerState.Active;
	}

	public void pause() {
		state = TimerState.Idle;
	}

	public void resume() {
		state = TimerState.Active;
	}

	public void togglePause() {
		state = state == TimerState.Active
			? TimerState.Idle
			: TimerState.Active;
	}
}