package com.duckcult.conway.weapons;

import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.player.Ship;

public class TrippleShotWeapon extends Weapon {
	public TrippleShotWeapon() {
		name = "Tripple";
		rofDelay=.75f;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public Array<Shot> fire(Ship origin) {
		Array<Shot> ret = new Array<Shot>();
		if(timeSinceFire>=rofDelay && hasAmmo()) {
			float mag = getDiagonal(BasicShot.DEFAULT_SPEED);
			ret.add(new BasicShot(origin));
			ret.add(new BasicShot(mag,mag,origin));
			ret.add(new BasicShot(-mag,mag,origin));
			timeSinceFire = 0.0f;
			ammo--;
		}
		return ret;
	}
}
