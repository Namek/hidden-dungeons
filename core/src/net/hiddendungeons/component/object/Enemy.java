package net.hiddendungeons.component.object;

import com.artemis.Component;

public class Enemy extends Component {
	public enum EnemyState { hurt, normal, aggressive }
	public float dmg;
	public float hp;
	public EnemyState state = EnemyState.normal;
	
	public Enemy(float hp, float dmg) {
		this.hp = hp;
		this.dmg = dmg;
	}
}
