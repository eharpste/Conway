package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
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
			BasicShot b = new BasicShot(origin.getRect());
			b.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
			ret.add(b);
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
