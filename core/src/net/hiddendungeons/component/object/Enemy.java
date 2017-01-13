package net.hiddendungeons.component.object;

import java.util.HashSet;
import java.util.Set;

import com.artemis.Component;

public class Enemy extends Component {
	public enum EnemyState { hurt, normal, aggressive }
	public float hp;
	public EnemyState state = EnemyState.normal;
	public Set<Integer> colliders = new HashSet<>();


	public Enemy() {
	}

	public Enemy(float hp) {
		this.hp = hp;
	}
}
