package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
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
			BasicShot b1 = new BasicShot(origin.getRect());
			BasicShot b2 = new BasicShot(BasicShot.DEFAULT_SPEED,BasicShot.DEFAULT_SPEED,origin.getRect());
			BasicShot b3 = new BasicShot(-BasicShot.DEFAULT_SPEED,BasicShot.DEFAULT_SPEED,origin.getRect());
			b1.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
			b2.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
			b3.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
			ret.add(b1);
			ret.add(b2);
			ret.add(b3);
			timeSinceFire = 0.0f;
		}
		return ret;
	}

}
