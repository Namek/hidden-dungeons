package net.hiddendungeons.component.base;

import net.hiddendungeons.system.base.TimeSystem;

import com.artemis.PooledComponent;

/**
 * @author Namek
 * @see TimeSystem
 */
public class TimeUpdate extends PooledComponent {
	public Updatable updater;
	private boolean _dependsOnTimeFactor;
	
	public TimeUpdate setup(Updatable updater, Boolean dependsOnTimeFactor) {
		this.updater = updater;
		this._dependsOnTimeFactor = dependsOnTimeFactor;
		return this;
	}
	
	public TimeUpdate setup(Updatable updater) {
		return setup(updater, true);
	}
	
	public boolean dependsOnTimeFactor() {
		return _dependsOnTimeFactor;
	}

	@Override
	protected void reset() {
		updater = null;
		_dependsOnTimeFactor = true;
	}
	

	public interface Updatable {
		void update(float deltaTime);
	}
}
