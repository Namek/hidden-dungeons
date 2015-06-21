package net.hiddendungeons.system.base.collision.messaging;

public interface CollisionExitListener {
	void onCollisionExit(int entityId, int otherEntityId);
}
