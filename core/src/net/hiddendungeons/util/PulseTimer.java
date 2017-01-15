package net.hiddendungeons.util;

import com.badlogic.gdx.math.MathUtils;

/**
 * A timer that goes up and down without ever stopping until told to.
 * @author Namek
 *
 */
public class PulseTimer {
	public float min = -0.5f, max = 0.5f;

	public float duration;
	public float timeElapsed;
	public float timeLeft;


	public PulseTimer(float duration) {
		this.duration = duration;
	}

	public PulseTimer(float duration, float min, float max) {
		this.duration = duration;
		this.min = min;
		this.max = max;
	}

	public float update(float deltaTime) {
		timeElapsed += deltaTime;
		timeLeft = duration - timeElapsed;

		if (timeLeft < 0) {
			timeElapsed = timeLeft;
			timeLeft = -timeLeft;
		}

		float progress = MathUtils.clamp(timeElapsed / duration, 0, 1);

		return min + progress * (max - min);
	}
}
