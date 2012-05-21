package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.duckcult.conway.player.Ship;

public class StandardWeapon extends Weapon {
	public StandardWeapon() {
		name = "Standard";
		rofDelay=.5f;
		timeSinceFire = rofDelay;
	}
	
	
	@Override
	public ArrayList<Shot> fire(Ship origin) {
		ArrayList<Shot> ret = new ArrayList<Shot>();
		if(timeSinceFire>=rofDelay) {
			ret.add(new BasicShot(origin));
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
