package net.hiddendungeons.component.object;

import com.artemis.Component;

public class Enemy extends Component {
	public float dmg;
	public float hp;
	
	public Enemy(float hp, float dmg) {
		this.hp = hp;
		this.dmg = dmg;
	}
}
