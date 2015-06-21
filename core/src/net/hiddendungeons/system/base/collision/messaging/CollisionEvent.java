package net.hiddendungeons.system.base.collision.messaging;

import net.mostlyoriginal.api.event.common.Event;

public class CollisionEvent implements Event {
	public static final int ENTER = 1;
	public static final int EXIT = 2;
	
	public int entity1Id;
	public int entity2Id;
	
	public int type;
	
	public void setup(int entity1Id, int entity2Id, int type) {
		this.entity1Id = entity1Id;
		this.entity2Id = entity2Id;
		this.type = type;
	}
}
