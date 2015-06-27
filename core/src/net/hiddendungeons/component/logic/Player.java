package net.hiddendungeons.component.logic;

import com.artemis.Component;

public class Player extends Component {
	/** value added to camera.position.y */
	public float eyeAltitude;


	public Player() {
	}
	
	public Player(float eyeAltitude) {
		this.eyeAltitude = eyeAltitude;
	}	
}
