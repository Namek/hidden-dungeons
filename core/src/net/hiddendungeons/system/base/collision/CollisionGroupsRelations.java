package net.hiddendungeons.system.base.collision;

/**
 * <p>Manages relations between all groups, supporting up to 63 groups.</p>
 * <p>Relation is always two-sided (if group A is connected to group B, then B is connected to A).</p>
 * 
 * @author Namek
 * @see CollisionDetectionSystem
 */
public final class CollisionGroupsRelations {
	/** Maps groupIndex to groupsToCollide bitset. */
	private final long[] relations = new long[Long.SIZE - 1];

	
	/**
	 * Makes relations between all groups for each of two different sets. Relation is two-sided.
	 */
	public void connectGroups(long group, long groups) {
		assert(group > 0);
		assert(groups > 0);
		assert(Long.numberOfLeadingZeros(group) + Long.numberOfTrailingZeros(group) == Long.SIZE - 1);

		// add all groups to this group
		int groupIndex = bitsetToIndex(group);
		long bitset = relations[groupIndex];
		bitset |= groups;
		relations[groupIndex] = bitset;

		// now add this group to all other groups
		groupIndex = 0;
		while (groups > 0) {
			if ((groups & 1) == 1) {
				bitset = relations[groupIndex];
				bitset |= group;
				relations[groupIndex] = bitset;
			}
			
			groups >>= 1;
			++groupIndex;
		}
	}
	
	/**
	 * Disconnect {@code groups} from {@code group}.
	 */
	public void disconnectGroups(long group, long groups) {
		assert(group > 0);
		assert(groups > 0);
		assert(Long.numberOfLeadingZeros(group) + Long.numberOfTrailingZeros(group) == Long.SIZE - 1);

		// turn off all groups for group
		int groupIndex = bitsetToIndex(group);
		long bitset = relations[groupIndex];
		bitset &= ~groups;
		relations[groupIndex] = bitset;
		
		// now turn off the reverse side
		groupIndex = 0;
		while (groups > 0) {
			if ((groups & 1) == 1) {
				bitset = relations[groupIndex];
				bitset &= ~(group);
				relations[groupIndex] = bitset;
			}
			
			groups >>= 1;
			++groupIndex;
		}
	}
	
	/**
	 * Resets all @{code group} relations by making only relations to given {@code groups}.
	 */
	public void setupGroup(long group, long groups) {
		assert(group > 0);
		assert(groups >= 0);
		assert(Long.numberOfLeadingZeros(group) + Long.numberOfTrailingZeros(group) == Long.SIZE - 1);

		// overwrite groups for this group
		int groupIndex = bitsetToIndex(group);
		long oldGroups = relations[groupIndex];
		relations[groupIndex] = groups;
		
		// now set connections to this group for all other groups
		groupIndex = 0;
		long allGroups = groups | oldGroups;
		while (allGroups > 0) {
			long bitset = relations[groupIndex];
			
			if ((groups & 1) == 1) {
				bitset |= group;
			}
			else {
				bitset &= ~(group);				
			}
			relations[groupIndex] = bitset;
			
			allGroups >>= 1;
			groups >>= 1;
			++groupIndex;
		}
	}
	
	/**
	 * Clear all {@code groups} relations.
	 * @param group
	 */
	public void clearGroupRelations(long group) {
		setupGroup(group, 0);
	}
	
	/**
	 * Checks if there exists a relation between any two groups of two different sets.
	 * 
	 * @param groups1 - bitset
	 * @param groups2 - bitset
	 */
	public boolean anyRelationExists(long groups1, long groups2) {
		long leftGroups = groups1;
		while (leftGroups > 0) {
			int groupIndex = bitsetToIndex(leftGroups);
			leftGroups &= ~(1 << groupIndex);
			
			if ((relations[groupIndex] & groups2) > 0) {
				return true;
			}
		}
		
		return false;
	}

	/** Looks looks for rightmost bit in given {@code bitset}. */ 
	private static int bitsetToIndex(long bitset) {
		return Long.numberOfTrailingZeros(bitset);
	}
}
