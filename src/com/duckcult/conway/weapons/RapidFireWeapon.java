package com.duckcult.conway.weapons;

import java.util.ArrayList;

import com.duckcult.conway.player.Ship;

public class RapidFireWeapon extends Weapon {
	private boolean one;
	public RapidFireWeapon() {
		name = "Rapid";
		rofDelay=.25f;
		one = true;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public ArrayList<Shot> fire(Ship origin) {
		ArrayList<Shot> ret = new ArrayList<Shot>();
		if(timeSinceFire>=rofDelay) {
			if(one){
				ret.add(new BasicShot(origin.getRect().x,origin.getRect().y+origin.getRect().height,0,BasicShot.DEFAULT_FAST,origin));
				one = false;
			}
			else {
				ret.add(new BasicShot(origin.getRect().x+origin.getRect().width/2,origin.getRect().y+origin.getRect().height,0,BasicShot.DEFAULT_FAST,origin));
				one = true;
			}
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
