package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.duckcult.conway.player.Ship;

public class TrippleShotWeapon extends Weapon {
	public TrippleShotWeapon() {
		name = "Tripple";
		rofDelay=.75f;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public ArrayList<Shot> fire(Ship origin) {
		ArrayList<Shot> ret = new ArrayList<Shot>();
		if(timeSinceFire>=rofDelay) {
			float mag = getDiagonal(BasicShot.DEFAULT_SPEED);
			ret.add(new BasicShot(origin));
			ret.add(new BasicShot(mag,mag,origin));
			ret.add(new BasicShot(-mag,mag,origin));
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
