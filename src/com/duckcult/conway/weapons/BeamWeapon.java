package com.duckcult.conway.weapons;

import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.player.Ship;

public class BeamWeapon extends Weapon {
	private boolean firing = false;
	private float timeFiring = 0.0f;
	private float beamTime = 2.5f;
	
	public BeamWeapon(){
		name = "Beam";
		rofDelay=3f;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public Array<Shot> fire(Ship origin) {
		Array<Shot> ret = new Array<Shot>();
		if(!firing && timeSinceFire >= rofDelay && hasAmmo()) {
			firing = true;
			ammo--;
			timeSinceFire=0.0f;
		}
		if(firing) {
			ret.add(new BasicShot(origin));
		}
		return ret;
	}

	public void update(float deltaTime) {
		if(firing) {
			timeFiring += deltaTime;
			if(timeFiring >=beamTime) {
				firing = false;
				timeFiring = 0.0f;
			}
		}
		else {
			timeSinceFire += deltaTime;
		}
	}
}
