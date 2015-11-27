package net.hiddendungeons.util;

/**
 * Timer that simplifies action update.
 *
 * @author Namek
 */
public class ActionTimer {
	public float duration;
	public boolean doingAction = true;
	public float timeElapsed;
	public float timeLeft;

	/** value range: [0,1] */
	public float progress = 0;

	public TimerState state;


	public enum TimerState {
		Idle,
		DoingAction,
		JustStopped
	}

	public ActionTimer() {
	}

	public ActionTimer(float maxTime) {
		this.duration = maxTime;
	}

	public TimerState update(float deltaTime) {
		if (doingAction) {
			timeElapsed += deltaTime;
			timeLeft = duration - timeElapsed;

			if (timeLeft <= 0) {
				doingAction = false;
				timeElapsed = 0;
				progress = 0;
				state = TimerState.Idle;
				return TimerState.JustStopped;
			}
			else {
				progress = timeElapsed / duration;
				state = TimerState.DoingAction;
			}
		}
		else {
			state = TimerState.Idle;
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
		doingAction = false;
		timeElapsed = 0;
		timeLeft = 0;
	}

	public void start() {
		doingAction = true;
		timeLeft = this.duration;
		timeElapsed = 0;
		progress = 0;
	}

	public void start(float duration) {
		this.duration = duration;
		start();
	}

	public boolean isRunning() {
		return doingAction;
	}

	public void pause() {
		doingAction = false;
	}

	public void resume() {
		doingAction = true;
	}

	public void togglePause() {
		doingAction = !doingAction;
	}
}