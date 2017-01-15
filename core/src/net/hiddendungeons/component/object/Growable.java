package net.hiddendungeons.component.object;

import com.artemis.Component;

/**
 * This component identifies how big is object.
 * It grows from 0 to 1 or decreases from 1 to 0.
 *
 * This component doesn't define any cooldown durations.
 */
public class Growable extends Component {
	public float percent = 0;

	/**
	 * Otherwise it's size is decreasing trough a time.
	 */
	public boolean up = true;
}
