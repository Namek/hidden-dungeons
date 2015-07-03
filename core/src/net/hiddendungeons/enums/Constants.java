package net.hiddendungeons.enums;

public interface Constants {
	public interface Player {
		public static final float MaxSpeed = 10f;
		public static final float Acceleration = 15f;
		public static final float Friction = 20f;
		public static final float MaxHeadBob = 0.05f;
		public static final float MouseSensitivity = 1f;
		
		public static final float ViewFinderSize = 0.01f;
	}

	public interface Fireball {
		public static final float RespawnTime = 2f;
		public static final float MaxRadius = 0.12f;
		public static final float MinRadius = 0.1f;
		public static final float TickRadiusIncrement = 0.0001f;
		public static final float Dmg = 0.4f;
		public static final float MaxSpeed = 20f;
		public static final float DisappearTime = 10f; // used in DelayedEntityRemoval
	}

	public interface Enemy {
		public static final float Dmg = 0.3f;
		public static final float Hp = 1f;
		public static final float Friction = 5.0f;
		public static final float MaxSpeed = 1.5f;
		public static final float Size = 2.5f;
		public static final float Depth = 0.1f;
		public static final float DetectionRadius = 8f;
		public static final float AttackRadius = 2f;
	}

}
