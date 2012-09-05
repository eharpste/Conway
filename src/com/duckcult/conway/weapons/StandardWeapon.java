package com.duckcult.conway.weapons;

import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.player.Ship;

public class StandardWeapon extends Weapon {
	public StandardWeapon() {
		name = "Standard";
		rofDelay=.5f;
		timeSinceFire = rofDelay;
	}
	
	
	@Override
	public Array<Shot> fire(Ship origin) {
		Array<Shot> ret = new Array<Shot>();
		if(timeSinceFire>=rofDelay) {
			ret.add(new BasicShot(origin));
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
