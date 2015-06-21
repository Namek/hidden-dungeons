package net.hiddendungeons.system.base.collision;

import net.hiddendungeons.system.base.collision.messaging.CollisionEnterListener;
import net.hiddendungeons.system.base.collision.messaging.CollisionExitListener;

import com.artemis.PooledComponent;

/**
 * Collider describes parameters for collision tests of certain entity. 
 * 
 * @author Namek
 * @see CollisionDetectionSystem
 */
public class Collider extends PooledComponent {
	/** Bitset of relations to which this entity belongs. */
	public long groups;
	
	/** For basic shape types supported by `CollisionDetectionSystem`, look into {@link ColliderType} */
	public int colliderType = ColliderType.BOUNDING_BOX;


	public CollisionEnterListener enterListener;
	public CollisionExitListener exitListener;
	
	
	public Collider setup(long groups, int colliderType) {
		this.groups = groups;
		this.colliderType = colliderType;
		
		return this;
	}
	
	public Collider groups(long groups) {
		this.groups = groups;
		return this;
	}


	@Override
	protected void reset() {
		groups = 0;
		colliderType = ColliderType.BOUNDING_BOX;
		enterListener = null;
		exitListener = null;
	}
	
	public boolean hasGroup(long group) {
		return (this.groups & group) > 0;
	}
	
	public boolean hasGroups(long groups) {
		return (this.groups & groups) > 0;
	}
}

