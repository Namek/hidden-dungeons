package net.hiddendungeons.component.object;

import com.artemis.Component;

public class Damage extends Component {
	public float dmg;
	
	public Damage() {
		this.dmg = 0f;
	}
	public Damage(float dmg) {
		this.dmg = dmg;
	}
}
