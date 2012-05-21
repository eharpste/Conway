package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
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
				BasicShot b =new BasicShot(origin.getRect().x,origin.getRect().y+origin.getRect().height,0,BasicShot.DEFAULT_SPEED,origin.getRect());
				b.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
				ret.add(b);
				one = false;
			}
			else {
				BasicShot b =new BasicShot(origin.getRect().x+origin.getRect().width/2,origin.getRect().y+origin.getRect().height,0,BasicShot.DEFAULT_SPEED,origin.getRect());
				b.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
				ret.add(b);
				one = true;
			}
			timeSinceFire = 0.0f;
		}
		return ret;
	}
}
